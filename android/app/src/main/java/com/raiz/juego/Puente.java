package com.raiz.juego;

import android.webkit.JavascriptInterface;

/**
 * Lo que el juego (index.html) puede pedirle a la app, como window.RootNativo.
 * Las respuestas llegan al juego con window.rootNativoEvento(evento, datos).
 */
class Puente {
    private final MainActivity app;

    Puente(MainActivity app) {
        this.app = app;
    }

    private boolean permitido() {
        return MainActivity.esDelJuego(app.paginaActual);
    }

    /** Versión del puente, por si el juego necesita saber qué sabe hacer la app. */
    @JavascriptInterface
    public int version() {
        return 1;
    }

    /** Estado actual en JSON: anuncios, compra, precio y privacidad. */
    @JavascriptInterface
    public String estado() {
        if (!permitido()) return "{}";
        return app.anuncios.estadoJson();
    }

    /** Muestra un anuncio entre misiones. Siempre responde con el evento "intersticial". */
    @JavascriptInterface
    public void intersticial() {
        if (!permitido()) return;
        app.runOnUiThread(() -> app.anuncios.mostrarIntersticial());
    }

    /** Muestra un anuncio con recompensa. Responde con el evento "recompensa" {ok}. */
    @JavascriptInterface
    public void recompensa() {
        if (!permitido()) return;
        app.runOnUiThread(() -> app.anuncios.mostrarRecompensa());
    }

    /** Abre la compra "Quitar anuncios" de Google Play. */
    @JavascriptInterface
    public void comprar() {
        if (!permitido()) return;
        app.runOnUiThread(() -> app.tienda.comprar());
    }

    /** Vuelve a pedirle a Google Play las compras hechas (por ejemplo en otro teléfono). */
    @JavascriptInterface
    public void restaurar() {
        if (!permitido()) return;
        app.runOnUiThread(() -> app.tienda.revisarCompras());
    }

    /** Abre las opciones de privacidad de los anuncios (consentimiento). */
    @JavascriptInterface
    public void privacidad() {
        if (!permitido()) return;
        app.runOnUiThread(() -> app.anuncios.opcionesPrivacidad());
    }
}
