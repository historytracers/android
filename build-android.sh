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
    # Strategy 1: JAVA_HOME already set correctly
    if [ -n "$JAVA_HOME" ]; then
        local jh
        jh="$(cygpath -u "$JAVA_HOME" 2>/dev/null || echo "$JAVA_HOME")"
        if [ -x "$jh/bin/javac" ]; then
            export JAVA_HOME="$jh"
            return 0
        fi
    fi

    # Strategy 2: Scan well-known base directories for any JDK
    for base in \
        "/c/Program Files/Java" \
        "/c/Program Files/Eclipse Adoptium" \
        "/c/Program Files/Amazon Corretto" \
        "/usr/lib/jvm" \
        "/usr/local/opt" \
        "/mingw64/lib/jvm"; do
        if [ -d "$base" ]; then
            for jdk in "$base"/*/; do
                if [ -d "$jdk" ] && [ -x "${jdk}bin/javac" ]; then
                    export JAVA_HOME="${jdk%/}"
                    return 0
                fi
            done
        fi
    done

    # Strategy 3: Hardcoded specific paths
    for cand in \
        "/usr/lib/jvm/java-17-openjdk" \
        "/usr/lib/jvm/java-17" \
        "/usr/lib/jvm/java-21-openjdk" \
        "/usr/lib/jvm/java-21" \
        "/usr/lib/jvm/default-java" \
        "/usr/local/opt/openjdk@17" \
        "/usr/local/opt/openjdk@21"; do
        if [ -x "$cand/bin/javac" ]; then
            export JAVA_HOME="$cand"
            return 0
        fi
    done

    # Strategy 4: JDK on PATH (via java -> javac parent dir)
    if command -v java &> /dev/null; then
        local java_exe
        java_exe="$(command -v java)"
        if command -v readlink &> /dev/null; then
            java_exe="$(readlink -f "$java_exe" 2>/dev/null || echo "$java_exe")"
        fi
        local jdk_path
        jdk_path="$(cd "$(dirname "$java_exe")/.." && pwd -P 2>/dev/null || true)"
        if [ -n "$jdk_path" ] && [ -x "$jdk_path/bin/javac" ]; then
            local ver
            ver=$("$jdk_path/bin/java" -version 2>&1 | awk -F[\".] '/version/{print $2}')
            if [ -n "$ver" ] && [ "$ver" -ge 17 ] 2>/dev/null; then
                export JAVA_HOME="$jdk_path"
                return 0
            fi
        fi
    fi

    return 1
}

if ! find_jdk; then
    echo "ERROR: JDK 17+ not found. Set JAVA_HOME or install a JDK."
    echo ""
    echo "Example:"
    echo "  export JAVA_HOME=\"/c/Program Files/Java/jdk-26.0.1\""
    echo ""
    echo "To locate your JDK, run: ls -d '/c/Program Files/Java/jdk-'*"
    exit 1
fi
echo "=== JDK found: $JAVA_HOME ==="

# --- Android SDK detection ---
find_android_sdk() {
    # Strategy 1: ANDROID_HOME / ANDROID_SDK_ROOT already set
    for var in ANDROID_HOME ANDROID_SDK_ROOT; do
        if [ -n "${!var}" ]; then
            local sdk
            sdk="$(cygpath -u "${!var}" 2>/dev/null || echo "${!var}")"
            if [ -d "$sdk" ]; then
                export ANDROID_HOME="$sdk"
                return 0
            fi
        fi
    done

    # Strategy 2: Check exact known SDK paths directly
    for sdk in \
        "/c/Program Files/Android/Sdk" \
        "/c/Users/$USER/AppData/Local/Android/Sdk" \
        "/c/Users/$USER/AppData/Local/Android/sdk" \
        "$HOME/Android/Sdk"; do
        if [ -d "$sdk" ]; then
            export ANDROID_HOME="$sdk"
            return 0
        fi
    done

    # Strategy 3: Scan base directories for any SDK
    for base in \
        "/c/Program Files/Android" \
        "/c/Users/$USER/AppData/Local/Android" \
        "$HOME/Android" \
        "/usr/lib/android-sdk" \
        "/opt/android-sdk" \
        "/usr/local/opt/android-sdk"; do
        if [ -d "$base" ]; then
            for cand in "$base"/*/; do
                for marker in "platforms" "platform-tools" "build-tools" "cmdline-tools" "tools"; do
                    if [ -d "${cand}${marker}" ]; then
                        export ANDROID_HOME="${cand%/}"
                        return 0
                    fi
                done
            done
        fi
    done

    # Strategy 4: Android Studio might bundle SDK inside its dir
    for studio in \
        "/c/Program Files/Android/Android Studio"* \
        "/c/Program Files/Android Studio"* \
        "$HOME/AppData/Local/Android/android-studio"*; do
        if [ -d "$studio" ]; then
            for sub in "sdk" "Sdk" "SDK"; do
                local cand="${studio}/${sub}"
                if [ -d "$cand" ]; then
                    for marker in "platforms" "platform-tools" "build-tools" "cmdline-tools" "tools"; do
                        if [ -d "${cand}/${marker}" ]; then
                            export ANDROID_HOME="$cand"
                            return 0
                        fi
                    done
                fi
            done
        fi
    done

    return 1
}

if ! find_android_sdk; then
    echo "ERROR: Android SDK not found. Set ANDROID_HOME or install the SDK."
    echo ""
    echo "  export ANDROID_HOME=\"/c/Users/$USER/AppData/Local/Android/Sdk\""
    echo ""
    ls -d "/c/Program Files/Android"/*/ "/c/Users/$USER/AppData/Local/Android"/*/ "$HOME/Android"/*/ 2>/dev/null || echo "(no candidates found in searched locations)"
    exit 1
fi
echo "=== Android SDK found: $ANDROID_HOME ==="

# --- Check for Gradle wrapper ---
if [ ! -f "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "=== Generating Gradle wrapper... ==="
    GRADLE_CMD=""
    if command -v gradle &> /dev/null; then
        GRADLE_CMD="gradle"
    elif [ -f "/c/gradle/bin/gradle" ]; then
        GRADLE_CMD="/c/gradle/bin/gradle"
    elif [ -f "/c/Users/$USER/gradle/bin/gradle" ]; then
        GRADLE_CMD="/c/Users/$USER/gradle/bin/gradle"
    fi
    if [ -n "$GRADLE_CMD" ]; then
        "$GRADLE_CMD" wrapper --gradle-version 9.4.1 --project-dir "$SCRIPT_DIR"
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
