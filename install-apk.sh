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

# --- Add a home-screen shortcut for the app ---

# Dump the current UI hierarchy to /sdcard/ht_ui.xml (with retries).
screen_dump() {
    local tries=0
    while [ $tries -lt 5 ]; do
        if MSYS_NO_PATHCONV=1 "$ADB" shell uiautomator dump /sdcard/ht_ui.xml >/dev/null 2>&1; then
            return 0
        fi
        tries=$((tries + 1))
        sleep 1
    done
    return 1
}

# Print the center "X Y" of the first node whose text equals $1 (or nothing).
node_center() {
    local label="$1"
    local line bounds x1 y1 x2 y2
    line=$(MSYS_NO_PATHCONV=1 "$ADB" shell cat /sdcard/ht_ui.xml 2>/dev/null | tr -d '\r' \
        | grep -o "<node[^>]*text=\"$label\"[^>]*bounds=\"\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]\"" \
        | head -n1)
    [ -z "$line" ] && return 1
    bounds=$(echo "$line" | grep -o '\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]')
    x1=$(echo "$bounds" | cut -d',' -f1 | tr -d '[')
    y1=$(echo "$bounds" | cut -d',' -f2 | cut -d']' -f1)
    x2=$(echo "$bounds" | cut -d',' -f2 | cut -d']' -f2 | tr -d '[')
    y2=$(echo "$bounds" | cut -d',' -f3 | tr -d ']')
    echo "$(( (x1 + x2) / 2 )) $(( (y1 + y2) / 2 ))"
}

# Print the physical display size as "WIDTH HEIGHT" (or nothing).
screen_size() {
    "$ADB" shell wm size 2>/dev/null | awk '/Physical size/ {print $3}' | head -n1 | tr 'x' ' '
}

# Add a "History Tracers" shortcut to the home screen (app drawer -> long press
# -> "Add to home screen"). Safe to call repeatedly: it skips if already there.
add_home_shortcut() {
    echo ""
    echo "=== Adding \"History Tracers\" shortcut to the home screen ==="

    # 1. Go to the home screen (KEYCODE_HOME also dismisses the notification shade)
    "$ADB" shell input keyevent KEYCODE_HOME >/dev/null 2>&1
    "$ADB" shell am start -a android.intent.action.MAIN -c android.intent.category.HOME >/dev/null 2>&1
    sleep 2

    # 2. Already on the home screen?
    if screen_dump && node_center "History Tracers" >/dev/null 2>&1; then
        echo "=== Shortcut already on the home screen ==="
        return 0
    fi

    # 3. Compute the drawer swipe (slow swipe up from the bottom edge)
    local size w h swx swy1 swy2 try
    size=$(screen_size)
    if [ -n "$size" ]; then
        w=$(echo "$size" | cut -d' ' -f1)
        h=$(echo "$size" | cut -d' ' -f2)
        swx=$((w / 2))
        swy1=$((h - 150))
        swy2=$((h / 4))
    else
        swx=540
        swy1=2250
        swy2=600
    fi

    # 4. Find the app in the drawer (re-open the drawer from home each attempt so
    #    it always starts at the top of the alphabetized list)
    local center cx cy add ax ay
    center=""
    try=0
    while [ $try -lt 6 ]; do
        "$ADB" shell input swipe "$swx" "$swy1" "$swx" "$swy2" 800
        sleep 3
        if screen_dump && center=$(node_center "History Tracers" 2>/dev/null); then
            break
        fi
        "$ADB" shell input keyevent KEYCODE_HOME >/dev/null 2>&1
        sleep 1
        try=$((try + 1))
    done

    if [ -z "$center" ]; then
        echo "WARNING: Could not find the app in the app drawer. Add the shortcut manually."
        return 0
    fi

    # 5. Long-press the icon to open its context menu
    cx=$(echo "$center" | cut -d' ' -f1)
    cy=$(echo "$center" | cut -d' ' -f2)
    "$ADB" shell input swipe "$cx" "$cy" "$cx" "$cy" 1600
    sleep 2

    # 6. Tap "Add to home screen" if available
    if screen_dump && add=$(node_center "Add to home screen" 2>/dev/null); then
        ax=$(echo "$add" | cut -d' ' -f1)
        ay=$(echo "$add" | cut -d' ' -f2)
        "$ADB" shell input tap "$ax" "$ay"
        sleep 3
        echo "=== Shortcut added to the home screen ==="
    else
        echo "WARNING: \"Add to home screen\" not found. Add the shortcut manually."
    fi

    # 7. Return to the home screen
    "$ADB" shell input keyevent KEYCODE_HOME >/dev/null 2>&1
    "$ADB" shell am start -a android.intent.action.MAIN -c android.intent.category.HOME >/dev/null 2>&1
    sleep 1
}

# --- Install ---
echo "=== Installing APK on device(s): $DEVICES ==="
"$ADB" install -r "$APK"
echo "=== Install complete ==="

# --- Add a shortcut on the home screen ---
# Let the launcher settle after the install before automating the UI.
sleep 3
add_home_shortcut
