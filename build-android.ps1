# SPDX-License-Identifier: GPL-3.0-or-later

# History Tracers Android - cross-platform build script for Windows

param(
    [switch]$Release
)

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location -LiteralPath $ScriptDir

Write-Output "=== Platform: windows ==="

# --- JDK detection (need 17+) ---
$jdkFound = $false
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME/bin/javac.exe")) {
    $jdkFound = $true
}
if (-not $jdkFound) {
    $candidates = @(
        "${env:ProgramFiles}\Java\jdk-17",
        "${env:ProgramFiles}\Java\jdk-21",
        "${env:ProgramFiles}\Eclipse Adoptium\jdk-17*",
        "${env:ProgramFiles}\Eclipse Adoptium\jdk-21*",
        "${env:ProgramFiles}\Microsoft\jdk-17*",
        "${env:ProgramFiles}\Microsoft\jdk-21*",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-17*",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-21*"
    )
    foreach ($cand in $candidates) {
        $paths = Get-ChildItem -Path $cand -ErrorAction SilentlyContinue
        foreach ($p in $paths) {
            if (Test-Path "$p\bin\javac.exe") {
                $env:JAVA_HOME = $p.FullName
                $jdkFound = $true
                break
            }
        }
        if ($jdkFound) { break }
    }
}

if (-not $jdkFound) {
    Write-Error "JDK 17+ not found. Set JAVA_HOME or install a JDK."
    exit 1
}
Write-Output "=== JDK found: $env:JAVA_HOME ==="

# --- Android SDK detection ---
$sdkFound = $false
if ($env:ANDROID_HOME -and (Test-Path $env:ANDROID_HOME)) {
    $sdkFound = $true
}
if (-not $sdkFound -and $env:ANDROID_SDK_ROOT -and (Test-Path $env:ANDROID_SDK_ROOT)) {
    $env:ANDROID_HOME = $env:ANDROID_SDK_ROOT
    $sdkFound = $true
}
if (-not $sdkFound) {
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "${env:ProgramFiles}\Android\Sdk",
        "C:\Android\Sdk"
    )
    foreach ($cand in $candidates) {
        if (Test-Path $cand) {
            $env:ANDROID_HOME = $cand
            $sdkFound = $true
            break
        }
    }
}

if (-not $sdkFound) {
    Write-Error "Android SDK not found. Set ANDROID_HOME or install the SDK."
    exit 1
}
Write-Output "=== Android SDK found: $env:ANDROID_HOME ==="

# --- Gradle wrapper ---
$wrapperJar = "$ScriptDir\gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $wrapperJar)) {
    Write-Output "=== Generating Gradle wrapper... ==="
    $gradlePath = $null
    $gradleCmd = Get-Command gradle -ErrorAction SilentlyContinue
    if ($gradleCmd) {
        $gradlePath = "gradle"
    } elseif (Test-Path "C:\gradle\bin\gradle.bat") {
        $gradlePath = "C:\gradle\bin\gradle.bat"
    } elseif (Test-Path "$env:USERPROFILE\gradle\bin\gradle.bat") {
        $gradlePath = "$env:USERPROFILE\gradle\bin\gradle.bat"
    }
    if ($gradlePath) {
        & $gradlePath wrapper --gradle-version 9.4.1 --project-dir "$ScriptDir"
        if (-not $?) { exit 1 }
    } else {
        Write-Error "Gradle not installed and no wrapper JAR found."
        Write-Error "Run 'gradle wrapper' manually or open the project in Android Studio."
        exit 1
    }
}

# --- Build ---
# Default builds debug APK (local install via install-apk.ps1) and debug AAB
# (same content, Play Bundle format). Pass -Release for signed release
# artifacts (needs keystore.properties or HT_* env vars, else unsigned).
$buildTasks = "assembleDebug", "bundleDebug"
$apkDir = "app\build\outputs\apk\debug\"
$aabFile = "app\build\outputs\bundle\debug\historytracers-debug.aab"
if ($Release) {
    $buildTasks = "assembleRelease", "bundleRelease"
    $apkDir = "app\build\outputs\apk\release\"
    $aabFile = "app\build\outputs\bundle\release\historytracers-release.aab"
}
Write-Output "=== Building Android app ($($buildTasks -join ' '))... ==="
& "$ScriptDir\gradlew.bat" $buildTasks
if ($?) {
    Write-Output "=== Build complete ==="
    Write-Output "APK location (local install): $apkDir"
    Write-Output "AAB location (Play Store upload): $aabFile"
}
