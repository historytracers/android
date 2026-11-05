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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
 * scope only grants access to the backup file this app created, never to the rest of
 * the user's Drive.
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
     * Downloads the backup, merges it into the local preferences and uploads the merged
     * result back to Drive.
     */
    suspend fun syncNow(preferences: UserPreferences): DriveSyncResult =
        runSync(preferences, mergeRemote = true)

    /** Uploads the current local preferences without touching what is stored remotely. */
    suspend fun uploadNow(preferences: UserPreferences): DriveSyncResult =
        runSync(preferences, mergeRemote = false)

    suspend fun disconnect(preferences: UserPreferences) = withContext(Dispatchers.IO) {
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

    private suspend fun runSync(
        preferences: UserPreferences,
        mergeRemote: Boolean
    ): DriveSyncResult = withContext(Dispatchers.IO) {
        if (!isPlayServicesAvailable(context)) {
            return@withContext DriveSyncResult(false, DriveSyncError.PLAY_SERVICES_UNAVAILABLE)
        }
        val account = signedInAccount()
            ?: return@withContext DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN)
        val accountObject = account.account
            ?: account.email?.let { android.accounts.Account(it, "com.google") }
            ?: return@withContext DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN)

        try {
            val token = GoogleAuthUtil.getToken(context, accountObject, DRIVE_SCOPE)
            val existingFileId = findBackupFile(token)

            // After "Reset Classes" the local state must win: upload it as-is instead
            // of merging the old remote progress back in.
            val resetPending = preferences.driveSyncResetPending.first()
            if (mergeRemote && !resetPending && existingFileId != null) {
                val remoteBackup = downloadBackup(token, existingFileId)
                if (remoteBackup.isNullOrBlank() || !preferences.mergeBackupJson(remoteBackup)) {
                    return@withContext DriveSyncResult(false, DriveSyncError.INVALID_BACKUP)
                }
            }

            val backupJson = preferences.exportBackupJson()
            uploadBackup(token, existingFileId, backupJson)

            val now = System.currentTimeMillis()
            preferences.setDriveSyncAccount(account.email ?: "")
            preferences.setDriveSyncLastSync(now)
            preferences.setDriveSyncResetPending(false)
            DriveSyncResult(success = true, lastSync = now)
        } catch (e: UserRecoverableAuthException) {
            DriveSyncResult(false, DriveSyncError.PERMISSION_REQUIRED, recoverIntent = e.intent)
        } catch (e: GoogleAuthException) {
            DriveSyncResult(false, DriveSyncError.NOT_SIGNED_IN, detail = e.message)
        } catch (e: DriveHttpException) {
            val error = if (e.status == 401 || e.status == 403) {
                DriveSyncError.NOT_SIGNED_IN
            } else {
                DriveSyncError.SERVER
            }
            DriveSyncResult(false, error, detail = e.message)
        } catch (e: IOException) {
            DriveSyncResult(false, DriveSyncError.NO_CONNECTION, detail = e.message)
        } catch (e: Exception) {
            DriveSyncResult(false, DriveSyncError.UNKNOWN, detail = e.message)
        }
    }

    private fun findBackupFile(token: String): String? {
        val query = URLEncoder.encode("name = '$BACKUP_FILE_NAME' and trashed = false", "UTF-8")
        val body = execute(
            token = token,
            method = "GET",
            url = "$FILES_URL?pageSize=1&orderBy=modifiedTime%20desc" +
                "&fields=files(id,modifiedTime)&q=$query"
        )
        val files = JsonParser.parseString(body).asJsonObject.getAsJsonArray("files") ?: return null
        return files.firstOrNull()?.asJsonObject?.get("id")?.asString
    }

    private fun downloadBackup(token: String, fileId: String): String? =
        execute(token, "GET", "$FILES_URL/$fileId?alt=media")

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
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun readError(connection: HttpURLConnection): String =
        connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""

    private class DriveHttpException(val status: Int, body: String) :
        IOException("Google Drive request failed ($status): $body")
}
