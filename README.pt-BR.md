[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/historytracers/android?utm_source=oss&utm_medium=github&utm_campaign=historytracers%2Fandroid&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# android

O History Tracers é um projeto educacional público e de código aberto que ensina conteúdo interdisciplinar por meio de aplicativos Android.

## Pré-requisitos

- Java Development Kit (JDK) 17 ou superior
- Android SDK (compileSdk 34, minSdk 26)
- Android SDK build tools

## Compilação

Use o script de compilação multiplataforma (requer JDK 17+ e Android SDK):

```sh
./build-android.sh      # Linux, macOS, Git Bash / MSYS2
```

No Windows (PowerShell):

```powershell
.\build-android.ps1
```

Ou abra o projeto no Android Studio e sincronize o Gradle.

## Testando a compilação

Após uma compilação bem-sucedida, o APK é gerado em:

```sh
app/build/outputs/apk/debug/app-debug.apk
```

Instale em um dispositivo ou emulador conectado:

```sh
adb install app/build/outputs/apk/debug/app-debug.apk
```

O AAR da biblioteca comum é gerado em:

```sh
common/src/android/build/outputs/aar/common-debug.aar
```

## Estrutura do projeto

| Caminho | Descrição |
|---|---|
| `app/` | Módulo principal do aplicativo Android (Jetpack Compose, Material 3) |
| `common/` | Definições de tipos de dados compartilhados (submódulo Git) |
| `common/src/android/` | Biblioteca Android com todas as classes Java mapeadas para JSON (Gson) |
