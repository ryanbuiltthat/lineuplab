#!/bin/bash
# Generate a debug keystore for local development
# This allows APK updates without uninstalling the previous version

KEYSTORE_DIR="app/keystore"
KEYSTORE_FILE="$KEYSTORE_DIR/debug.keystore"

if [ -f "$KEYSTORE_FILE" ]; then
    echo "Debug keystore already exists at $KEYSTORE_FILE"
    exit 0
fi

echo "Creating debug keystore directory..."
mkdir -p "$KEYSTORE_DIR"

echo "Generating debug keystore..."
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass android \
    -keypass android \
    -alias androiddebugkey \
    -dname "CN=Android Debug,O=Android,C=US"

if [ $? -eq 0 ]; then
    echo "✓ Debug keystore created successfully at $KEYSTORE_FILE"
    echo ""
    echo "Note: This keystore uses default debug credentials:"
    echo "  Store password: android"
    echo "  Key password: android"
    echo "  Alias: androiddebugkey"
else
    echo "✗ Failed to create debug keystore"
    exit 1
fi
