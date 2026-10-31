// ROOT · guarda el juego en el teléfono para poder jugar sin conexión.
// La página principal siempre se pide primero a internet (para recibir actualizaciones);
// las librerías, fuentes e íconos se sirven desde la copia guardada.
const CACHE = 'root-1.2.0';
const CORE = [
  './',
  './index.html',
  './manifest.webmanifest',
  './icon-192.png',
  './icon-512.png',
  './libs/three.min.js',
  './libs/CopyShader.js',
  './libs/LuminosityHighPassShader.js',
  './libs/EffectComposer.js',
  './libs/RenderPass.js',
  './libs/ShaderPass.js',
  './libs/UnrealBloomPass.js',
  './fonts/big-shoulders-stencil-display-latin-700-normal.woff2',
  './fonts/big-shoulders-stencil-display-latin-800-normal.woff2',
  './fonts/chakra-petch-latin-400-normal.woff2',
  './fonts/chakra-petch-latin-500-normal.woff2',
  './fonts/chakra-petch-latin-600-normal.woff2'
];

self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(CORE)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys()
      .then(keys => Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

function withTimeout(promise, ms) {
  return new Promise((resolve, reject) => {
    const t = setTimeout(() => reject(new Error('timeout')), ms);
    promise.then(r => { clearTimeout(t); resolve(r); }, err => { clearTimeout(t); reject(err); });
  });
}

self.addEventListener('fetch', e => {
  const req = e.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);
  if (url.origin !== location.origin) return;
  const isPage = req.mode === 'navigate' || url.pathname.endsWith('/') || url.pathname.endsWith('.html');
  if (isPage) {
    e.respondWith(
      withTimeout(fetch(req, { cache: 'no-cache' }), 6000)
        .then(res => {
          if (res.ok) { const copy = res.clone(); caches.open(CACHE).then(c => c.put('./index.html', copy)); }
          return res;
        })
        .catch(() => caches.match('./index.html'))
    );
    return;
  }
  e.respondWith(
    caches.match(req).then(hit => hit || fetch(req).then(res => {
      if (res.ok) { const copy = res.clone(); caches.open(CACHE).then(c => c.put(req, copy)); }
      return res;
    }))
  );
});
