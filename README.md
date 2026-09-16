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
app/build/outputs/apk/debug/app-debug.apk
```

The same build also produces the App Bundle (AAB) at:

```sh
app/build/outputs/bundle/debug/app-debug.aab
```

The release build produces `app/build/outputs/apk/release/app-release.apk` and `app/build/outputs/bundle/release/app-release.aab`. Upload the AAB to Google Play; keep using the APK for direct device installs, since AAB files cannot be installed with `adb install` directly.

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

## Project structure

| Path | Description |
|---|---|
| `app/` | Main Android application module (Jetpack Compose, Material 3) |
| `common/` | Shared data type definitions (Git submodule) |
| `common/src/android/` | Android library with all JSON-mapped Java classes (Gson) |
