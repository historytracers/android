// SPDX-License-Identifier: GPL-3.0-or-later
import java.io.InputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Release signing credentials come from keystore.properties (project root,
// gitignored, keys: ht.store.file, ht.store.password, ht.key.alias,
// ht.key.password) or HT_* environment variables (HT_STORE_FILE,
// HT_STORE_PASSWORD, HT_KEY_ALIAS, HT_KEY_PASSWORD). When absent, the release
// build stays unsigned so bundleRelease still runs for local testing; Play
// Store uploads need a signed bundle.
val keystoreProps = Properties()
val keystorePropsFile = rootProject.file("keystore.properties")
if (keystorePropsFile.exists()) {
    keystorePropsFile.inputStream().use { stream: InputStream -> keystoreProps.load(stream) }
}
fun keystoreProp(name: String): String? =
    (keystoreProps.getProperty(name) ?: System.getenv(name.uppercase().replace('.', '_')))
        ?.takeIf { it.isNotBlank() }
val hasReleaseSigning = keystoreProp("ht.store.file") != null &&
    keystoreProp("ht.store.password") != null &&
    keystoreProp("ht.key.alias") != null &&
    keystoreProp("ht.key.password") != null

android {
    namespace = "com.historytracers.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.historytracers.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            keystoreProp("ht.store.file")?.let { storeFile = rootProject.file(it) }
            keystoreProp("ht.store.password")?.let { storePassword = it }
            keystoreProp("ht.key.alias")?.let { keyAlias = it }
            keystoreProp("ht.key.password")?.let { keyPassword = it }
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../common/src/smartphone")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = if (hasReleaseSigning) signingConfigs.getByName("release") else null
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))

    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.webkit:webkit:1.8.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
