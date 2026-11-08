[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/historytracers/android?utm_source=oss&utm_medium=github&utm_campaign=historytracers%2Fandroid&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# android

History Tracers is a public, open-source educational project that teaches interdisciplinary content through Android applications.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Android SDK (compileSdk 34, minSdk 26)
- Android SDK build tools

## Building

Use the cross-platform build script (requires JDK 17+ and Android SDK):

```sh
./build-android.sh      # Linux, macOS, Git Bash / MSYS2
```

On Windows (PowerShell):

```powershell
.\build-android.ps1
```

For release artifacts (APK + AAB):

```sh
./build-android.sh --release      # Linux, macOS, Git Bash / MSYS2
```

On Windows (PowerShell):

```powershell
.\build-android.ps1 -Release
```

Or open the project in Android Studio and sync Gradle.

## Testing the build

After a successful build, the APK is produced at:

```sh
app/build/outputs/apk/debug/historytracers-debug.apk
```

The same build also produces the App Bundle (AAB) at:

```sh
app/build/outputs/bundle/debug/historytracers-debug.aab
```

The release build produces `app/build/outputs/apk/release/historytracers-release.apk` and `app/build/outputs/bundle/release/historytracers-release.aab`. Upload the AAB to Google Play; keep using the APK for direct device installs, since AAB files cannot be installed with `adb install` directly.

Install it on a connected device or emulator:

```sh
./install-apk.sh      # Linux, macOS, Git Bash / MSYS2
```

On Windows (PowerShell):

```powershell
.\install-apk.ps1
```

The common library AAR is produced at:

```sh
common/src/android/build/outputs/aar/common-debug.aar
```

## Signing the release build

Without signing credentials the release build is unsigned (fine for local testing, not for Play Store uploads). To sign it, create a keystore:

```sh
keytool -genkeypair -v -keystore historytracers.keystore -alias historytracers -keyalg RSA -keysize 2048 -validity 10000
```

Then create a gitignored `keystore.properties` file in the project root:

```properties
ht.store.file=/absolute/path/to/historytracers.keystore
ht.store.password=<store-password>
ht.key.alias=historytracers
ht.key.password=<key-password>
```

Alternatively, set the `HT_STORE_FILE`, `HT_STORE_PASSWORD`, `HT_KEY_ALIAS` and `HT_KEY_PASSWORD` environment variables with the same values.

## Google Drive sync setup

The optional **Google Drive Sync** setting (off by default) saves a JSON backup of the user's score, streak and preferences as `History Tracers Backup.json` in their own Google Drive. No History Tracers server is involved: the app calls the Google Drive REST API directly with an OAuth token obtained from Google Sign-In, using the `drive.file` scope. That scope lets the app access files it creates or that the user explicitly shares with it; the app uses it to create and update the backup file.

The publisher configures this once in the Google Cloud Console:

1. Create (or reuse) a Google Cloud project.
2. Enable the **Google Drive API** for that project.
3. Configure the **OAuth consent screen** and add the scope `https://www.googleapis.com/auth/drive.file`. If the audience is **External** and the app is in **Testing**, only users listed as test users can authorize Drive sync: add the users as testers, or publish the app and complete any required verification.
4. Create an **OAuth 2.0 Client ID** of type **Android** for the application id `com.historytracers.app`, one per signing certificate, and register that certificate's SHA-1 fingerprint. Register the debug certificate, the release/upload certificate, and — when Play App Signing uses a different key — the app-signing certificate SHA-1 shown in Play Console under *Test and release → App integrity*.

    The debug fingerprint can be printed with:

    ```sh
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
    ```

No `google-services.json` is needed: Google Play services resolves the Android OAuth client by package name and signing certificate. Until a matching client is registered, the setting stays visible; when sign-in fails, Settings shows the prompt to sign in with Google to sync progress.

## Project structure

| Path | Description |
|---|---|
| `app/` | Main Android application module (Jetpack Compose, Material 3) |
| `common/` | Shared data type definitions (Git submodule) |
| `common/src/android/` | Android library with all JSON-mapped Java classes (Gson) |
