#!/bin/bash

set -eo pipefail

# SPDX-License-Identifier: GPL-3.0-or-later

# History Tracers Android - cross-platform build script

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# --- Platform detection ---
case "$(uname -s)" in
    Linux)
        PLATFORM="linux"
        ;;
    Darwin)
        PLATFORM="darwin"
        ;;
    CYGWIN*|MINGW*|MSYS*)
        PLATFORM="msys2"
        # MSYS2 path conversion breaks Gradle's Java classpath arguments
        export MSYS2_ARG_CONV_EXCL="*"
        ;;
    *)
        echo "Unknown platform: $(uname -s)"
        exit 1
        ;;
esac
echo "=== Platform: $PLATFORM ==="

# --- JDK detection (need 17+) ---
find_jdk() {
    if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
        return 0
    fi
    for cand in \
        "/usr/lib/jvm/java-17-openjdk" \
        "/usr/lib/jvm/java-17" \
        "/usr/lib/jvm/java-21-openjdk" \
        "/usr/lib/jvm/java-21" \
        "/usr/lib/jvm/default-java" \
        "/usr/local/opt/openjdk@17" \
        "/usr/local/opt/openjdk@21" \
        "/c/Program Files/Java/jdk-17" \
        "/c/Program Files/Java/jdk-21" \
        "/c/Program Files/Eclipse Adoptium/jdk-17"* \
        "/c/Program Files/Eclipse Adoptium/jdk-21"* \
        "/mingw64/lib/jvm/openjdk"* \
        "/mingw64/lib/jvm/msopenjdk"* \
        "/usr/lib/jvm/openjdk"* \
        "/usr/lib/jvm/msopenjdk"*; do
        for jdk in $cand; do
            if [ -x "$jdk/bin/javac" ]; then
                export JAVA_HOME="$jdk"
                return 0
            fi
        done
    done
    return 1
}

if ! find_jdk; then
    echo "ERROR: JDK 17+ not found. Set JAVA_HOME or install a JDK."
    exit 1
fi
echo "=== JDK found: $JAVA_HOME ==="

# --- Android SDK detection ---
find_android_sdk() {
    if [ -n "$ANDROID_HOME" ] && [ -d "$(cygpath -u "$ANDROID_HOME" 2>/dev/null || echo "$ANDROID_HOME")" ]; then
        return 0
    fi
    if [ -n "$ANDROID_SDK_ROOT" ] && [ -d "$(cygpath -u "$ANDROID_SDK_ROOT" 2>/dev/null || echo "$ANDROID_SDK_ROOT")" ]; then
        export ANDROID_HOME="$ANDROID_SDK_ROOT"
        return 0
    fi
    case "$PLATFORM" in
        linux)
            for cand in "$HOME/Android/Sdk" "/usr/lib/android-sdk" "/opt/android-sdk"; do
                if [ -d "$cand" ]; then
                    export ANDROID_HOME="$cand"
                    return 0
                fi
            done
            ;;
        darwin)
            for cand in "$HOME/Library/Android/sdk" "/usr/local/opt/android-sdk"; do
                if [ -d "$cand" ]; then
                    export ANDROID_HOME="$cand"
                    return 0
                fi
            done
            ;;
        msys2)
            WINUSER="${USER:-${USERNAME:-$(whoami 2>/dev/null || echo "$USER")}}"
            for cand in "/c/Users/$WINUSER/AppData/Local/Android/Sdk" "/c/Program Files/Android/Sdk" "/c/Android/Sdk"; do
                if [ -d "$cand" ]; then
                    export ANDROID_HOME="$cand"
                    return 0
                fi
            done
            ;;
    esac
    return 1
}

if ! find_android_sdk; then
    echo "ERROR: Android SDK not found. Set ANDROID_HOME or install the SDK."
    exit 1
fi
echo "=== Android SDK found: $ANDROID_HOME ==="

# --- Check for Gradle wrapper ---
if [ ! -f "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "=== Generating Gradle wrapper... ==="
    if command -v gradle &> /dev/null; then
        gradle wrapper --project-dir "$SCRIPT_DIR"
    else
        echo "ERROR: Gradle not installed and no wrapper JAR found."
        echo "Run 'gradle wrapper' manually or open the project in Android Studio."
        exit 1
    fi
fi

# --- Build ---
echo "=== Building Android app (assembleDebug)... ==="
"$SCRIPT_DIR/gradlew" assembleDebug
echo "=== Build complete ==="
echo "APK location: app/build/outputs/apk/debug/"
