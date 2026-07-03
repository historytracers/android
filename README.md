# android

History Tracers is a public, open-source educational project that teaches interdisciplinary content through Android applications.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Android SDK (compileSdk 34, minSdk 26)
- Android SDK build tools

## Building

Generate the Gradle wrapper (if not present):

```
gradle wrapper
```

Then build with:

```
./gradlew assembleDebug
```

Or open the project in Android Studio and sync Gradle.

## Project structure

| Path | Description |
|---|---|
| `app/` | Main Android application module (Jetpack Compose, Material 3) |
| `common/` | Shared data type definitions (Git submodule) |
| `common/src/android/` | Android library with all JSON-mapped Java classes (Gson) |
