// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

enum class DriveSyncError {
    PLAY_SERVICES_UNAVAILABLE,
    NOT_SIGNED_IN,
    PERMISSION_REQUIRED,
    NO_CONNECTION,
    SERVER,
    INVALID_BACKUP,
    UNKNOWN
}

data class DriveSyncResult(
    val success: Boolean,
    val error: DriveSyncError? = null,
    val recoverIntent: Intent? = null,
    val lastSync: Long = 0L,
    val detail: String? = null
)

/**
 * Synchronizes [UserPreferences] with a JSON backup file stored in the signed-in
 * user's own Google Drive.
 *
 * There is no History Tracers server involved: the app talks to the Google Drive REST
 * API directly using the access token obtained from Google Sign-In. The `drive.file`
 * scope grants access to files this app creates or that the user explicitly shares with
 * it; the app uses it to create and update its backup file.
 *
 * Setup required by the app publisher (one time, in Google Cloud Console):
 *  - enable the Google Drive API;
 *  - create an Android OAuth client for application id `com.historytracers.app` with
 *    the SHA-1 of each signing certificate (debug and release);
 *  - add `https://www.googleapis.com/auth/drive.file` to the OAuth consent screen.
 */
class DriveSyncManager(private val context: Context) {

    companion object {
        private const val BACKUP_FILE_NAME = "History Tracers Backup.json"
        private const val DRIVE_SCOPE_URL = "https://www.googleapis.com/auth/drive.file"
        private const val DRIVE_SCOPE = "oauth2:$DRIVE_SCOPE_URL"
        private const val FILES_URL = "https://www.googleapis.com/drive/v3/files"
        private const val UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3/files"
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 15_000
        private const val SIGN_IN_TIMEOUT_SECONDS = 60L

        // A process-wide lock: different DriveSyncManager instances (Settings and the
        // background sync) must not race while choosing/creating the backup file.
        private val SYNC_MUTEX = Mutex()

        fun isPlayServicesAvailable(context: Context): Boolean =
            GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

        fun signInOptions(): GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DRIVE_SCOPE_URL))
                .build()
    }

    fun signInIntent(): Intent = GoogleSignIn.getClient(context, signInOptions()).signInIntent

    /** Returns the account when sign-in succeeded, or null when the user cancelled/failed. */
    fun completeSignIn(resultData: Intent?): GoogleSignInAccount? = runCatching {
        val task = GoogleSignIn.getSignedInAccountFromIntent(resultData)
        if (task.isSuccessful) task.result else null
    }.getOrNull()

    fun signedInAccount(): GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

    /**
     * Downloads every copy of the backup, merges them into the local preferences and
     * uploads the merged result back to Drive. When a reset is pending for the connected
     * account, the cleared local state is uploaded instead of merging.
     */
    suspend fun syncNow(preferences: UserPreferences): DriveSyncResult =
        SYNC_MUTEX.withLock {
            withContext(Dispatchers.IO) { runSyncLocked(preferences) }
        }

    suspend fun disconnect(preferences: UserPreferences) = SYNC_MUTEX.withLock {
        withContext(Dispatchers.IO) {
            runCatching {
                Tasks.await(
                    GoogleSignIn.getClient(context, signInOptions()).signOut(),
                    SIGN_IN_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
                )
            }
            preferences.setDriveSyncEnabled(false)
            preferences.setDriveSyncAccount("")
            preferences.setDriveSyncLastSync(0L)
        }
    }

    private suspend fun runSyncLocked(preferences: UserPreferences): DriveSyncResult {
        if (!isPlayServicesAvailable(context)) {
            return DriveSyncResult(false, DriveSyncError.PLAY_SERVICES_UNAVAILABLE)
        }
        val account = signedInAccount()
            ?: return DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN)
        val accountObject = account.account
            ?: account.email?.let { android.accounts.Account(it, "com.google") }
            ?: return DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN)

        try {
            val token = GoogleAuthUtil.getToken(context, accountObject, DRIVE_SCOPE)
            val backups = findBackupFiles(token)
            val existingFileId = backups.firstOrNull()?.id

            // After "Reset Classes" the local state must win: upload it as-is instead
            // of merging the old remote progress back in. The reset only applies to the
            // account it was created for (or to the first account when it happened
            // before Drive was connected).
            val currentEmail = account.email ?: ""
            val resetPending = preferences.driveSyncResetPending.first()
            val resetAccount = preferences.driveSyncResetAccount.first()
            val resetApplies = resetPending &&
                (resetAccount.isBlank() || resetAccount.equals(currentEmail, ignoreCase = true))

            if (!resetApplies) {
                var foundNonBlankBackup = false
                var mergedAnyBackup = false
                backups.forEach { backup ->
                    val remoteBackup = runCatching { downloadBackup(token, backup.id) }.getOrNull()
                    if (!remoteBackup.isNullOrBlank()) {
                        foundNonBlankBackup = true
                        if (preferences.mergeBackupJson(remoteBackup)) {
                            mergedAnyBackup = true
                        }
                    }
                }
                if (foundNonBlankBackup && !mergedAnyBackup) {
                    return DriveSyncResult(false, DriveSyncError.INVALID_BACKUP)
                }
            }

            val resetVersionBefore = preferences.driveSyncResetVersion.first()
            val backupJson = preferences.exportBackupJson()
            uploadBackup(token, existingFileId, backupJson)

            // Keep one canonical backup; remove older duplicates from previous races.
            backups.drop(1).forEach { duplicate ->
                runCatching { deleteBackup(token, duplicate.id) }
            }

            val now = System.currentTimeMillis()
            preferences.setDriveSyncAccount(currentEmail)
            preferences.setDriveSyncLastSync(now)
            if (resetApplies &&
                resetVersionBefore == preferences.driveSyncResetVersion.first()
            ) {
                preferences.setDriveSyncResetPending(false)
            }
            return DriveSyncResult(success = true, lastSync = now)
        } catch (e: UserRecoverableAuthException) {
            return DriveSyncResult(false, DriveSyncError.PERMISSION_REQUIRED, recoverIntent = e.intent)
        } catch (e: GoogleAuthException) {
            return DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN, detail = e.message)
        } catch (e: DriveHttpException) {
            val error = if (e.status == 401 || e.status == 403) {
                DriveSyncError.NOT_SIGNED_IN
            } else {
                DriveSyncError.SERVER
            }
            return DriveSyncResult(false, error, detail = e.message)
        } catch (e: IOException) {
            return DriveSyncResult(false, DriveSyncError.NO_CONNECTION, detail = e.message)
        } catch (e: Exception) {
            return DriveSyncResult(false, DriveSyncError.UNKNOWN, detail = e.message)
        }
    }

    private data class RemoteBackup(val id: String, val modifiedTime: Long)

    private fun findBackupFiles(token: String): List<RemoteBackup> {
        val query = URLEncoder.encode("name = '$BACKUP_FILE_NAME' and trashed = false", "UTF-8")
        val body = execute(
            token = token,
            method = "GET",
            url = "$FILES_URL?pageSize=100&orderBy=modifiedTime%20desc" +
                "&fields=files(id,modifiedTime)&q=$query"
        )
        val files = JsonParser.parseString(body).asJsonObject.getAsJsonArray("files")
            ?: return emptyList()
        return files.mapNotNull { element ->
            val obj = element.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
            val id = obj.get("id")?.takeIf { it.isJsonPrimitive }?.asString
                ?: return@mapNotNull null
            val modifiedTime = obj.get("modifiedTime")
                ?.takeIf { it.isJsonPrimitive }
                ?.asString
                ?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }
                ?: 0L
            RemoteBackup(id, modifiedTime)
        }.sortedWith(
            compareByDescending<RemoteBackup> { it.modifiedTime }
                .thenByDescending { it.id }
        )
    }

    private fun downloadBackup(token: String, fileId: String): String? =
        execute(token, "GET", "$FILES_URL/$fileId?alt=media")

    private fun deleteBackup(token: String, fileId: String) {
        execute(token, "DELETE", "$FILES_URL/$fileId")
    }

    private fun uploadBackup(token: String, existingFileId: String?, content: String) {
        if (existingFileId == null) {
            val metadata = JsonObject().apply {
                addProperty("name", BACKUP_FILE_NAME)
                addProperty("mimeType", "application/json")
            }
            val created = execute(token, "POST", "$FILES_URL?fields=id", metadata.toString())
            val fileId = JsonParser.parseString(created).asJsonObject.get("id").asString
            execute(token, "PATCH", "$UPLOAD_URL/$fileId?uploadType=media", content)
        } else {
            execute(token, "PATCH", "$UPLOAD_URL/$existingFileId?uploadType=media", content)
        }
    }

    private fun execute(
        token: String,
        method: String,
        url: String,
        body: String? = null
    ): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.setRequestProperty("Authorization", "Bearer $token")
            // Android's HttpURLConnection rejects the PATCH verb; Google APIs accept a
            // POST with the X-HTTP-Method-Override header instead.
            if (method == "PATCH") {
                connection.requestMethod = "POST"
                connection.setRequestProperty("X-HTTP-Method-Override", "PATCH")
            } else {
                connection.requestMethod = method
            }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }

            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_UNAUTHORIZED || status == HttpURLConnection.HTTP_FORBIDDEN) {
                throw DriveHttpException(status, readError(connection))
            }
            if (status !in 200..299) {
                throw DriveHttpException(status, readError(connection))
            }
            return connection.inputStream?.bufferedReader()?.use { it.readText() } ?: ""
        } finally {
            connection.disconnect()
        }
    }

    private fun readError(connection: HttpURLConnection): String =
        connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""

    private class DriveHttpException(val status: Int, body: String) :
        IOException("Google Drive request failed ($status): $body")
}
