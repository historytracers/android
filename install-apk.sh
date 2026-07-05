#!/bin/bash

set -eo pipefail

# SPDX-License-Identifier: GPL-3.0-or-later

# History Tracers Android - APK installer script
# Run this after a successful build to install the APK on a device/emulator.

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# --- Locate adb via ANDROID_HOME or well-known paths ---
find_adb() {
    for var in ANDROID_HOME ANDROID_SDK_ROOT; do
        if [ -n "${!var}" ]; then
            local sdk
            sdk="$(cygpath -u "${!var}" 2>/dev/null || echo "${!var}")"
            if [ -x "$sdk/platform-tools/adb" ]; then
                echo "$sdk/platform-tools/adb"
                return 0
            fi
        fi
    done
    for sdk in \
        "/c/Program Files/Android/Sdk" \
        "/c/Users/$USER/AppData/Local/Android/Sdk" \
        "$HOME/Android/Sdk" \
        "/usr/lib/android-sdk" \
        "/opt/android-sdk"; do
        if [ -x "$sdk/platform-tools/adb" ]; then
            echo "$sdk/platform-tools/adb"
            return 0
        fi
    done
    if command -v adb &>/dev/null; then
        echo "adb"
        return 0
    fi
    return 1
}

ADB=$(find_adb) || { echo "ERROR: adb not found. Set ANDROID_HOME or install platform-tools."; exit 1; }
echo "=== Using adb: $ADB ==="

# --- Locate APK ---
APK="$SCRIPT_DIR/app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK" ]; then
    echo "ERROR: APK not found. Run the build script first."
    echo "  Expected: $APK"
    exit 1
fi
echo "=== APK: $APK ==="

# --- Check device ---
DEVICES=$("$ADB" devices | awk 'NR>1 && $2=="device" {print $1}')
if [ -z "$DEVICES" ]; then
    echo "WARNING: No Android device or emulator connected."

    # --- Try to find and start an AVD ---
    ANDROID_SDK="$(dirname "$(dirname "$ADB")")"
    EMULATOR="$ANDROID_SDK/emulator/emulator"
    AVD_LIST=$("$EMULATOR" -list-avds 2>/dev/null || true)

    if [ -n "$AVD_LIST" ]; then
        echo "Available AVDs:"
        echo "$AVD_LIST" | nl -w2 -s') '
        echo ""
        read -rp "Start which AVD? (0 to skip, default 1): " CHOICE
        CHOICE=${CHOICE:-1}
        if [ "$CHOICE" != "0" ]; then
            AVD_NAME=$(echo "$AVD_LIST" | sed -n "${CHOICE}p")
            if [ -n "$AVD_NAME" ]; then
                echo "=== Starting emulator \"$AVD_NAME\"... ==="
                "$EMULATOR" -avd "$AVD_NAME" -no-snapshot-load &
                EMU_PID=$!
                echo "=== Waiting for emulator to boot (this may take a while)... ==="
                "$ADB" wait-for-device
                # Wait until the package manager is ready (boot completed)
                while [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r\n')" != "1" ]; do
                    sleep 3
                done
                echo "=== Emulator booted ==="
                DEVICES=$("$ADB" devices | awk 'NR>1 && $2=="device" {print $1}')
            fi
        fi
    fi

    if [ -z "$DEVICES" ]; then
        echo "ERROR: No Android device or emulator connected."
        echo "  Connect a device via USB (with USB debugging enabled) or start an emulator."
        echo ""
        echo "  To start an emulator manually from the command line:"
        if [ -x "$EMULATOR" ] && [ -n "$AVD_LIST" ]; then
            echo "    $EMULATOR -avd <avd_name>"
            echo "  Run '$EMULATOR -list-avds' to see available AVDs."
        else
            echo "  1. Open Android Studio"
            echo "  2. Click the Device Manager icon (or Tools > Device Manager)"
            echo "  3. Create or start a virtual device"
        fi
        exit 1
    fi
fi

# --- Install ---
echo "=== Installing APK on device(s): $DEVICES ==="
"$ADB" install -r "$APK"
echo "=== Install complete ==="
