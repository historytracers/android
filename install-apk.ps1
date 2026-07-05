# SPDX-License-Identifier: GPL-3.0-or-later

# History Tracers Android - APK installer script for Windows
# Run this after a successful build to install the APK on a device/emulator.

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

# --- Locate adb ---
$adbPath = $null
if ($env:ANDROID_HOME) {
    $cand = "$env:ANDROID_HOME\platform-tools\adb.exe"
    if (Test-Path $cand) { $adbPath = $cand }
}
if (-not $adbPath -and $env:ANDROID_SDK_ROOT) {
    $cand = "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe"
    if (Test-Path $cand) { $adbPath = $cand }
}
if (-not $adbPath) {
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "${env:ProgramFiles}\Android\Sdk\platform-tools\adb.exe",
        "C:\Android\Sdk\platform-tools\adb.exe"
    )
    foreach ($cand in $candidates) {
        if (Test-Path $cand) { $adbPath = $cand; break }
    }
}
if (-not $adbPath) {
    $cmd = Get-Command adb -ErrorAction SilentlyContinue
    if ($cmd) { $adbPath = "adb" }
}
if (-not $adbPath) {
    Write-Error "adb not found. Set ANDROID_HOME or install platform-tools."
    exit 1
}
Write-Output "=== Using adb: $adbPath ==="

# --- Locate APK ---
$apkPath = "$ScriptDir\app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apkPath)) {
    Write-Error "APK not found. Run the build script first."
    Write-Error "  Expected: $apkPath"
    exit 1
}
Write-Output "=== APK: $apkPath ==="

# --- Check connected device ---
$devices = & $adbPath devices
$connected = ($devices | Select-String -Pattern "device$" -SimpleMatch)
if (-not $connected) {
    Write-Warning "No Android device or emulator connected."

    # --- Try to find and start an AVD ---
    $sdkPath = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Split-Path -Parent (Split-Path -Parent $adbPath) }
    $emulatorPath = "$sdkPath\emulator\emulator.exe"
    $avds = @()
    if (Test-Path $emulatorPath) {
        $avdOutput = & $emulatorPath -list-avds 2>$null
        $avds = $avdOutput | Where-Object { $_ -match '\S' }
    }

    if ($avds.Count -gt 0) {
        Write-Output "Available AVDs:"
        for ($i = 0; $i -lt $avds.Count; $i++) {
            Write-Output "  $($i+1)) $($avds[$i])"
        }
        $choice = Read-Host "Start which AVD? (0 to skip, default 1)"
        if ([string]::IsNullOrEmpty($choice)) { $choice = "1" }
        if ($choice -ne "0") {
            $idx = [int]$choice - 1
            if ($idx -ge 0 -and $idx -lt $avds.Count) {
                $avdName = $avds[$idx]
                Write-Output "=== Starting emulator `"$avdName`"... ==="
                $proc = Start-Process -FilePath $emulatorPath -ArgumentList "-avd", $avdName, "-no-snapshot-load" -PassThru -NoNewWindow
                Write-Output "=== Waiting for emulator to boot (this may take a while)... ==="
                & $adbPath wait-for-device
                do {
                    Start-Sleep -Seconds 3
                    $boot = & $adbPath shell getprop sys.boot_completed 2>$null
                } while ($boot -ne "1")
                Write-Output "=== Emulator booted ==="
                $devices = & $adbPath devices
                $connected = ($devices | Select-String -Pattern "device$" -SimpleMatch)
            }
        }
    }

    if (-not $connected) {
        Write-Error "No Android device or emulator connected."
        Write-Error "  Connect a device via USB (with USB debugging enabled) or start an emulator."
        Write-Error ""
        if (Test-Path $emulatorPath -and $avds.Count -gt 0) {
            Write-Error "  To start an emulator manually:"
            Write-Error "    & `"$emulatorPath`" -avd <avd_name>"
            Write-Error "  Run `"& `"$emulatorPath`" -list-avds`" to see available AVDs."
        } else {
            Write-Error "  1. Open Android Studio"
            Write-Error "  2. Click the Device Manager icon (or Tools > Device Manager)"
            Write-Error "  3. Create or start a virtual device"
        }
        exit 1
    }
}

# --- Install ---
Write-Output "=== Installing APK... ==="
& $adbPath install -r $apkPath
if ($?) {
    Write-Output "=== Install complete ==="
}
