// Revisión rápida antes de publicar: que el código del juego no tenga errores
// de sintaxis y que existan los archivos que la página carga.
const fs = require('fs');
const path = require('path');
const raiz = path.join(__dirname, '..');
let fallos = 0;
const mal = m => { console.error('✗ ' + m); fallos++; };

const html = fs.readFileSync(path.join(raiz, 'index.html'), 'utf8');
const bloques = [...html.matchAll(/<script>([\s\S]*?)<\/script>/g)].map(m => m[1]);
if (!bloques.length) mal('index.html no tiene el código del juego');
bloques.forEach((b, i) => { try { new Function(b); } catch (e) { mal(`index.html, bloque ${i + 1}: ${e.message}`); } });

for (const m of html.matchAll(/(?:src|href)="([^"#:]+)"/g)) {
  if (!fs.existsSync(path.join(raiz, m[1]))) mal('falta el archivo ' + m[1]);
}
try { new Function(fs.readFileSync(path.join(raiz, 'sw.js'), 'utf8')); } catch (e) { mal('sw.js: ' + e.message); }
try { JSON.parse(fs.readFileSync(path.join(raiz, 'manifest.webmanifest'), 'utf8')); } catch (e) { mal('manifest.webmanifest: ' + e.message); }
if (!/ROOT_VERSION='\d+\.\d+\.\d+'/.test(html)) mal('no se encontró ROOT_VERSION en index.html');

if (fallos) { console.error(`${fallos} problema(s): no se publica.`); process.exit(1); }
console.log('✓ El juego está listo para publicar');
