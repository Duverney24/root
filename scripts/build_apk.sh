#!/usr/bin/env bash
# Arma el APK de ROOT a partir de los archivos del juego.
#
# Requisitos: Java 17+, tools/apktool.jar, tools/signer.jar (uber-apk-signer)
# y la llave de firma en root.keystore (o la ruta en KEYSTORE_FILE).
# La contraseña de la llave se pasa en KEYSTORE_PASS.
#
# El número de versión se toma de ROOT_VERSION dentro de index.html.
set -euo pipefail
cd "$(dirname "$0")/.."

KEYSTORE_FILE=${KEYSTORE_FILE:-root.keystore}
: "${KEYSTORE_PASS:?Falta KEYSTORE_PASS}"

VERSION=$(grep -oE "ROOT_VERSION='[0-9]+\.[0-9]+\.[0-9]+'" index.html | grep -oE "[0-9]+\.[0-9]+\.[0-9]+")
IFS=. read -r MA MI PA <<< "$VERSION"
CODE=$((MA * 10000 + MI * 100 + PA))
echo "Versión $VERSION (código $CODE)"

BUILD=build/android
rm -rf build dist && mkdir -p "$BUILD" dist
cp -r android/. "$BUILD/"
mkdir -p "$BUILD/assets"
cp index.html "$BUILD/assets/"
cp -r libs fonts "$BUILD/assets/"
sed -i "s/versionCode: .*/versionCode: $CODE/; s/versionName: .*/versionName: $VERSION/" "$BUILD/apktool.yml"

java -jar tools/apktool.jar b "$BUILD" -o build/root-sin-firmar.apk
java -jar tools/signer.jar -a build/root-sin-firmar.apk -o build/firmado \
  --ks "$KEYSTORE_FILE" --ksAlias raiz --ksPass "$KEYSTORE_PASS" --ksKeyPass "$KEYSTORE_PASS"
cp build/firmado/*.apk "dist/ROOT-$VERSION.apk"
echo "Listo: dist/ROOT-$VERSION.apk"
