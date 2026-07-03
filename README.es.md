# android

History Tracers es un proyecto educativo público y de código abierto que enseña contenido interdisciplinario a través de aplicaciones Android.

## Requisitos previos

- Java Development Kit (JDK) 17 o superior
- Android SDK (compileSdk 34, minSdk 26)
- Android SDK build tools

## Compilación

Use el script de compilación multiplataforma (requiere JDK 17+ y Android SDK):

```sh
./build-android.sh      # Linux, macOS, Git Bash / MSYS2
```

En Windows (PowerShell):

```powershell
.\build-android.ps1
```

O abra el proyecto en Android Studio y sincronice Gradle.

## Estructura del proyecto

| Ruta | Descripción |
|---|---|
| `app/` | Módulo principal de la aplicación Android (Jetpack Compose, Material 3) |
| `common/` | Definiciones de tipos de datos compartidos (submódulo Git) |
| `common/src/android/` | Biblioteca Android con todas las clases Java mapeadas a JSON (Gson) |
