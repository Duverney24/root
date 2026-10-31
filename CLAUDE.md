# ROOT

- Todo lo que se sube a `main` o a una rama `claude/…` se publica solo (acción **Publicar**): pasa a `main`, GitHub Pages lo publica y la app lo recibe.
- Antes de subir, correr `node scripts/revisar.js`; si falla, la acción no publica.
- En cada cambio del juego, subir `ROOT_VERSION` en `index.html` (1.3.0 → 1.3.1, etc.): así se arma y publica un APK nuevo en Releases.
- Probar en celular: el juego debe sostener 60 FPS (Menú → Ver FPS).
