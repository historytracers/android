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
app/build/outputs/apk/debug/historytracers-debug.apk
```

A mesma compilação também gera o App Bundle (AAB) em:

```sh
app/build/outputs/bundle/debug/historytracers-debug.aab
```

A compilação release gera `app/build/outputs/apk/release/historytracers-release.apk` e `app/build/outputs/bundle/release/historytracers-release.aab`. Envie o AAB para o Google Play; continue usando o APK para instalações diretas em dispositivos, pois arquivos AAB não podem ser instalados diretamente com `adb install`.

Instale em um dispositivo ou emulador conectado:

```sh
adb install app/build/outputs/apk/debug/historytracers-debug.apk
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

## Sincronização com o Google Drive

A opção **Sincronização com o Google Drive** (desativada por padrão) salva um backup JSON da pontuação, da sequência e das preferências do usuário como `History Tracers Backup.json` no próprio Google Drive. Não há servidor do History Tracers envolvido: o aplicativo chama diretamente a API REST do Google Drive com um token OAuth obtido pelo login do Google, usando o escopo `drive.file`. Esse escopo permite ao aplicativo acessar arquivos que ele cria ou que o usuário compartilha explicitamente com ele; o aplicativo o usa para criar e atualizar o arquivo de backup.

O publicador configura isso uma única vez no Google Cloud Console:

1. Crie (ou reutilize) um projeto do Google Cloud.
2. Ative a **Google Drive API** nesse projeto.
3. Configure a **tela de consentimento OAuth** e adicione o escopo `https://www.googleapis.com/auth/drive.file`. Se a audiência for **Externa** e o aplicativo estiver em **Teste**, somente os usuários adicionados como testadores poderão autorizar a sincronização com o Drive: adicione os usuários como testadores ou publique o aplicativo e conclua a verificação necessária.
4. Crie um **ID de cliente OAuth 2.0** do tipo **Android** para o id de aplicativo `com.historytracers.app`, um para cada certificado de assinatura, e registre a impressão digital SHA-1 desse certificado. Registre o certificado de debug, o de release/upload e, quando a assinatura do Play usar uma chave diferente, a impressão digital SHA-1 do certificado de assinatura do aplicativo exibida no Play Console em *Testar e lançar → Integridade do app*.

    A impressão digital de debug pode ser obtida com:

    ```sh
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
    ```

Não é necessário `google-services.json`: os serviços do Google Play encontram o cliente OAuth Android pelo nome do pacote e pelo certificado de assinatura. Enquanto um cliente correspondente não for registrado, a opção continua visível; se o login falhar, as Configurações mostram o aviso para entrar com o Google e sincronizar o progresso.

## Estrutura do projeto

| Caminho | Descrição |
|---|---|
| `app/` | Módulo principal do aplicativo Android (Jetpack Compose, Material 3) |
| `common/` | Definições de tipos de dados compartilhados (submódulo Git) |
| `common/src/android/` | Biblioteca Android com todas as classes Java mapeadas para JSON (Gson) |
