pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "historytracers-android"
include(":app")
include(":common")
project(":common").projectDir = file("common/src/android")
