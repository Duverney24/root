#!/usr/bin/env bash
# Arma la app de Android de ROOT: el APK (para Releases) y el AAB (para Google Play).
#
# Requisitos: Java 17+, el SDK de Android (ANDROID_HOME) y la llave de firma en
# root.keystore (o la ruta en KEYSTORE_FILE). La contraseña va en KEYSTORE_PASS.
# Opcional: ADMOB_APP_ID, ADMOB_INTERSTICIAL y ADMOB_RECOMPENSA para los anuncios.
#
# El número de versión se toma de ROOT_VERSION dentro de index.html.
set -euo pipefail
cd "$(dirname "$0")/.."
RAIZ=$(pwd)

export KEYSTORE_FILE=$(realpath "${KEYSTORE_FILE:-root.keystore}")
: "${KEYSTORE_PASS:?Falta KEYSTORE_PASS}"

VERSION=$(grep -oE "ROOT_VERSION='[0-9]+\.[0-9]+\.[0-9]+'" index.html | grep -oE "[0-9]+\.[0-9]+\.[0-9]+")
echo "Versión $VERSION"
if [ -n "${ADMOB_APP_ID:-}" ]; then echo "Con anuncios de AdMob"; else echo "Sin códigos de AdMob: la app sale sin anuncios"; fi

# La copia del juego que viene dentro de la app, para jugar sin internet.
JUEGO=android/app/build/juego
rm -rf "$JUEGO" dist && mkdir -p "$JUEGO" dist
cp index.html "$JUEGO/"
cp -r libs fonts "$JUEGO/"

(cd android && ./gradlew --no-daemon assembleRelease bundleRelease)

cp android/app/build/outputs/apk/release/app-release.apk "dist/ROOT-$VERSION.apk"
cp android/app/build/outputs/bundle/release/app-release.aab "dist/ROOT-$VERSION.aab"
echo "Listo: dist/ROOT-$VERSION.apk y dist/ROOT-$VERSION.aab"
