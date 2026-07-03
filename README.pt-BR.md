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

## Estrutura do projeto

| Caminho | Descrição |
|---|---|
| `app/` | Módulo principal do aplicativo Android (Jetpack Compose, Material 3) |
| `common/` | Definições de tipos de dados compartilhados (submódulo Git) |
| `common/src/android/` | Biblioteca Android com todas as classes Java mapeadas para JSON (Gson) |
