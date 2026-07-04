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

Or open the project in Android Studio and sync Gradle.

## Testing the build

After a successful build, the APK is produced at:

```sh
app/build/outputs/apk/debug/app-debug.apk
```

Install it on a connected device or emulator:

```sh
adb install app/build/outputs/apk/debug/app-debug.apk
```

The common library AAR is produced at:

```sh
common/src/android/build/outputs/aar/common-debug.aar
```

## Project structure

| Path | Description |
|---|---|
| `app/` | Main Android application module (Jetpack Compose, Material 3) |
| `common/` | Shared data type definitions (Git submodule) |
| `common/src/android/` | Android library with all JSON-mapped Java classes (Gson) |
