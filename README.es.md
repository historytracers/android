[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/historytracers/android?utm_source=oss&utm_medium=github&utm_campaign=historytracers%2Fandroid&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

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

Para artefactos release (APK + AAB):

```sh
./build-android.sh --release      # Linux, macOS, Git Bash / MSYS2
```

En Windows (PowerShell):

```powershell
.\build-android.ps1 -Release
```

O abra el proyecto en Android Studio y sincronice Gradle.

## Prueba de la compilación

Después de una compilación exitosa, el APK se genera en:

```sh
app/build/outputs/apk/debug/historytracers-debug.apk
```

La misma compilación también genera el App Bundle (AAB) en:

```sh
app/build/outputs/bundle/debug/historytracers-debug.aab
```

La compilación release genera `app/build/outputs/apk/release/historytracers-release.apk` y `app/build/outputs/bundle/release/historytracers-release.aab`. Suba el AAB a Google Play; siga usando el APK para instalaciones directas en dispositivos, ya que los archivos AAB no se pueden instalar directamente con `adb install`.

Instálelo en un dispositivo o emulador conectado:

```sh
adb install app/build/outputs/apk/debug/historytracers-debug.apk
```

El AAR de la biblioteca común se genera en:

```sh
common/src/android/build/outputs/aar/common-debug.aar
```

## Firma de la compilación release

Sin credenciales de firma, la compilación release no está firmada (sirve para pruebas locales, no para subidas a Play Store). Para firmarla, cree un keystore:

```sh
keytool -genkeypair -v -keystore historytracers.keystore -alias historytracers -keyalg RSA -keysize 2048 -validity 10000
```

Luego cree un archivo `keystore.properties` (ignorado por git) en la raíz del proyecto:

```properties
ht.store.file=/ruta/absoluta/a/historytracers.keystore
ht.store.password=<contraseña-del-almacén>
ht.key.alias=historytracers
ht.key.password=<contraseña-de-la-clave>
```

Como alternativa, defina las variables de entorno `HT_STORE_FILE`, `HT_STORE_PASSWORD`, `HT_KEY_ALIAS` y `HT_KEY_PASSWORD` con los mismos valores.

## Sincronización con Google Drive

La opción **Sincronización con Google Drive** (desactivada por defecto) guarda una copia de seguridad JSON de la puntuación, la racha y las preferencias del usuario como `History Tracers Backup.json` en su propio Google Drive. No interviene ningún servidor de History Tracers: la aplicación llama directamente a la API REST de Google Drive con un token OAuth obtenido mediante el inicio de sesión de Google, usando el alcance `drive.file` (la aplicación solo puede acceder al archivo de copia de seguridad que ella misma creó).

El publicador configura esto una sola vez en Google Cloud Console:

1. Cree (o reutilice) un proyecto de Google Cloud.
2. Habilite la **Google Drive API** para ese proyecto.
3. Configure la **pantalla de consentimiento de OAuth** y añada el alcance `https://www.googleapis.com/auth/drive.file`.
4. Cree un **ID de cliente OAuth 2.0** de tipo **Android** para el id de aplicación `com.historytracers.app` y registre la huella SHA-1 de cada certificado de firma (debug y release). La huella de debug se puede obtener con:

    ```sh
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
    ```

No se necesita `google-services.json`: los servicios de Google Play resuelven el cliente OAuth de Android por el nombre del paquete y el certificado de firma. Hasta que se registre el cliente, la opción sigue visible, pero el inicio de sesión falla con el error genérico de sincronización.

## Estructura del proyecto

| Ruta | Descripción |
|---|---|
| `app/` | Módulo principal de la aplicación Android (Jetpack Compose, Material 3) |
| `common/` | Definiciones de tipos de datos compartidos (submódulo Git) |
| `common/src/android/` | Biblioteca Android con todas las clases Java mapeadas a JSON (Gson) |
