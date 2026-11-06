package com.raiz.juego;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * ROOT · pantalla única que muestra el juego.
 * Carga la versión publicada en GitHub Pages (así recibe actualizaciones);
 * si no hay conexión, abre la copia del juego que viene dentro de la app.
 * El juego habla con la app por {@link Puente} (anuncios y compra).
 */
public class MainActivity extends Activity {
    static final String SITIO = "https://duverney24.github.io/root/";
    static final String COPIA = "file:///android_asset/index.html";

    private WebView web;
    Anuncios anuncios;
    Tienda tienda;
    /** Dirección de la página abierta, para que solo el juego use el puente. */
    volatile String paginaActual = "";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.setBackgroundColor(Color.BLACK);
        web.setWebViewClient(new Cliente());

        tienda = new Tienda(this);
        anuncios = new Anuncios(this);
        web.addJavascriptInterface(new Puente(this), "RootNativo");

        web.loadUrl(SITIO);
        setContentView(web);
        pantallaCompleta();

        tienda.conectar();
        anuncios.iniciar();
    }

    /** Avisa al juego que algo cambió (anuncio cerrado, compra hecha…). */
    void avisar(String evento, String datosJson) {
        runOnUiThread(() -> {
            if (web == null) return;
            String js = "window.rootNativoEvento&&window.rootNativoEvento('" + evento + "'," + datosJson + ")";
            web.evaluateJavascript(js, null);
        });
    }

    static boolean esDelJuego(String url) {
        return url != null && (url.startsWith(SITIO) || url.startsWith("file:///android_asset/"));
    }

    @SuppressWarnings("deprecation")
    private void pantallaCompleta() {
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.systemBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean foco) {
        super.onWindowFocusChanged(foco);
        if (foco) pantallaCompleta();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (web != null) web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
        if (tienda != null) tienda.revisarCompras();
    }

    @Override
    protected void onDestroy() {
        if (tienda != null) tienda.cerrar();
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }

    /** Si la versión en línea no carga, abre la copia que viene dentro de la app. */
    private class Cliente extends WebViewClient {
        private void respaldo(WebView v) {
            String url = v.getUrl();
            if (url == null || !url.startsWith("file:")) v.loadUrl(COPIA);
        }

        @Override
        public void onPageStarted(WebView v, String url, Bitmap icono) {
            paginaActual = url == null ? "" : url;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
            Uri u = r.getUrl();
            if (esDelJuego(u.toString())) return false;
            // Enlaces de fuera (por ejemplo la política de privacidad) se abren en el navegador.
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, u));
            } catch (Exception ignorado) {
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
            if (r.isForMainFrame()) respaldo(v);
        }

        @Override
        public void onReceivedHttpError(WebView v, WebResourceRequest r, WebResourceResponse resp) {
            if (r.isForMainFrame() && resp.getStatusCode() >= 400) respaldo(v);
        }
    }
}
