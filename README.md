# ROOT

Aventura de supervivencia en tercera persona. Noventa y siete años después del colapso, cuarenta jóvenes salen de La Bóveda a una selva que se tragó las ciudades.

**Acto 1 · La llegada**: siete misiones, una decisión que cambia el final, sigilo frente a un jaguar y ciclo de día y noche.

## Jugar

- **En el navegador** (celular o computador): https://duverney24.github.io/root/
- **En Android**: descarga el APK más reciente en [Releases](https://github.com/Duverney24/root/releases) e instálalo. La app carga el juego desde la dirección de arriba, así que las mejoras llegan solas cuando abres el juego con internet. Sin conexión juega con la última versión guardada.

## Controles

| Acción | Computador | Celular |
| --- | --- | --- |
| Moverse | W A S D | Joystick |
| Mirar | Ratón (clic para fijar) | Arrastrar en la pantalla |
| Correr | Shift | Correr |
| Agacharse | C | Agachar |
| Saltar | Espacio | Saltar |
| Acción | E (mantener para usar) | Acción (mantener para usar) |
| Menú | P | Menú |

## Rendimiento

- La resolución se ajusta sola para sostener 60 FPS: baja si el teléfono no alcanza y vuelve a subir cuando sobra.
- Si aun así no alcanza, la calidad baja un nivel (solo si no la elegiste a mano en el menú).
- En **Menú → Ver FPS** se muestra el contador y el porcentaje de resolución en uso.

## Cómo se actualiza

Todo es automático con la acción **Publicar** (`.github/workflows/publicar.yml`). Cada vez que se sube un cambio a `main` o a una rama `claude/…`:

1. Se revisa el juego (`scripts/revisar.js`). Si tiene errores, no se publica nada.
2. El cambio pasa a `main` y GitHub Pages publica la nueva versión en unos minutos.
3. La app de Android y la web la reciben solas la próxima vez que se abren con internet.
4. Si `ROOT_VERSION` en `index.html` es un número nuevo, se arma el APK firmado y se publica en Releases como `vX.Y.Z`.

Para que el APK se firme, el repositorio necesita los secretos `KEYSTORE_B64` (la llave `root.keystore` en base64) y `KEYSTORE_PASS` (Settings → Secrets and variables → Actions). Sin ellos, la web se publica igual y la acción solo avisa.

Cada versión publica en Releases el APK (para instalar a mano) y el AAB (el que se sube a Google Play Console).

## Anuncios y compra

Solo en la app de Android; la web no tiene anuncios.

- **Entre misiones**: un anuncio de pantalla completa, nunca antes de la misión 3 y como mucho uno cada tres minutos.
- **Con recompensa**: si el jaguar te alcanza, puedes ver un anuncio y seguir desde ahí en vez de volver al borde de la ciudad.
- **Quitar anuncios**: compra única en Google Play (Menú → Quitar anuncios). Se recupera sola en otro teléfono con la misma cuenta.
- **Privacidad**: la app pide el consentimiento donde la ley lo exige; se cambia en Menú → Privacidad de anuncios. Política: https://duverney24.github.io/root/privacidad.html

Para activarlos, agrega estos secretos en el repositorio y sube `ROOT_VERSION`:

| Secreto | Qué es |
| --- | --- |
| `ADMOB_APP_ID` | ID de la app en AdMob (`ca-app-pub-…~…`) |
| `ADMOB_INTERSTICIAL` | Bloque de anuncios intersticial (`ca-app-pub-…/…`) |
| `ADMOB_RECOMPENSA` | Bloque de anuncios bonificado (`ca-app-pub-…/…`) |

Sin ellos la app sale sin anuncios. En Play Console, el producto integrado en la aplicación debe tener el ID `quitar_anuncios`. La compra solo funciona si la app se instaló desde Google Play.

## Estructura

- `index.html`: el juego completo (Three.js).
- `libs/`, `fonts/`: librerías y tipografías locales, para que funcione sin internet.
- `sw.js`, `manifest.webmanifest`: modo sin conexión e instalación como app web.
- `android/`: la app de Android (proyecto Gradle): la pantalla que muestra el juego, los anuncios (`Anuncios.java`), la compra (`Tienda.java`) y el puente con el juego (`Puente.java`).
- `privacidad.html`: política de privacidad (la pide Google Play).
- `scripts/build_apk.sh`: arma y firma el APK y el AAB.
- `scripts/revisar.js`: revisión antes de publicar.
- `.github/workflows/`: publicación automática y armado del APK.

Créditos: [Three.js](https://threejs.org) (MIT), tipografías Big Shoulders Stencil Display y Chakra Petch (SIL Open Font License).
