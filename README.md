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

1. Los cambios del juego van en `index.html`. En cada versión, subir `ROOT_VERSION` en `index.html` y `CACHE` en `sw.js` al mismo número, para que los teléfonos descarguen las librerías nuevas.
2. Al subirlos a `main`, GitHub Pages publica la nueva versión en unos minutos y la app la recibe sola.
3. Solo cuando cambia la app en sí (carpeta `android/`), se crea una etiqueta `vX.Y.Z` y la acción **Armar APK** genera el APK firmado en Releases.

## Estructura

- `index.html`: el juego completo (Three.js).
- `libs/`, `fonts/`: librerías y tipografías locales, para que funcione sin internet.
- `sw.js`, `manifest.webmanifest`: modo sin conexión e instalación como app web.
- `android/`: la app de Android (pantalla que muestra el juego).
- `scripts/build_apk.sh`: arma y firma el APK.

Créditos: [Three.js](https://threejs.org) (MIT), tipografías Big Shoulders Stencil Display y Chakra Petch (SIL Open Font License).
