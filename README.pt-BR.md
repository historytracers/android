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

Para artefatos release (APK + AAB):

```sh
./build-android.sh --release      # Linux, macOS, Git Bash / MSYS2
```

No Windows (PowerShell):

```powershell
.\build-android.ps1 -Release
```

Ou abra o projeto no Android Studio e sincronize o Gradle.

## Testando a compilação

Após uma compilação bem-sucedida, o APK é gerado em:

```sh
app/build/outputs/apk/debug/app-debug.apk
```

A mesma compilação também gera o App Bundle (AAB) em:

```sh
app/build/outputs/bundle/debug/app-debug.aab
```

A compilação release gera `app/build/outputs/apk/release/app-release.apk` e `app/build/outputs/bundle/release/app-release.aab`. Envie o AAB para o Google Play; continue usando o APK para instalações diretas em dispositivos, pois arquivos AAB não podem ser instalados diretamente com `adb install`.

Instale em um dispositivo ou emulador conectado:

```sh
adb install app/build/outputs/apk/debug/app-debug.apk
```

O AAR da biblioteca comum é gerado em:

```sh
common/src/android/build/outputs/aar/common-debug.aar
```

## Assinatura da compilação release

Sem credenciais de assinatura, a compilação release não é assinada (serve para testes locais, não para envios à Play Store). Para assiná-la, crie um keystore:

```sh
keytool -genkeypair -v -keystore historytracers.keystore -alias historytracers -keyalg RSA -keysize 2048 -validity 10000
```

Em seguida, crie um arquivo `keystore.properties` (ignorado pelo git) na raiz do projeto:

```properties
ht.store.file=/caminho/absoluto/para/historytracers.keystore
ht.store.password=<senha-do-keystore>
ht.key.alias=historytracers
ht.key.password=<senha-da-chave>
```

Como alternativa, defina as variáveis de ambiente `HT_STORE_FILE`, `HT_STORE_PASSWORD`, `HT_KEY_ALIAS` e `HT_KEY_PASSWORD` com os mesmos valores.

## Estrutura do projeto

| Caminho | Descrição |
|---|---|
| `app/` | Módulo principal do aplicativo Android (Jetpack Compose, Material 3) |
| `common/` | Definições de tipos de dados compartilhados (submódulo Git) |
| `common/src/android/` | Biblioteca Android com todas as classes Java mapeadas para JSON (Gson) |
