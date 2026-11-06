package com.raiz.juego;

import android.util.Log;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Anuncios de AdMob: primero pide el consentimiento (obligatorio en Europa y
 * otros lugares), luego deja cargados un anuncio entre misiones y uno con
 * recompensa. Si se compró "Quitar anuncios" no carga ni muestra nada.
 */
class Anuncios {
    private static final String TAG = "ROOT-anuncios";
    private final MainActivity app;
    private final ConsentInformation consentimiento;
    private final AtomicBoolean iniciado = new AtomicBoolean(false);
    private volatile InterstitialAd inter;
    private volatile RewardedAd premio;
    private boolean cargandoInter, cargandoPremio;
    private volatile boolean listo;

    Anuncios(MainActivity app) {
        this.app = app;
        this.consentimiento = UserMessagingPlatform.getConsentInformation(app);
    }

    private boolean activos() {
        return BuildConfig.ANUNCIOS && !app.tienda.sinAnuncios();
    }

    void iniciar() {
        if (!BuildConfig.ANUNCIOS) return;
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consentimiento.requestConsentInfoUpdate(app, params,
            () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(app, error -> {
                if (error != null) Log.w(TAG, "Consentimiento: " + error.getMessage());
                arrancarSdk();
            }),
            error -> {
                Log.w(TAG, "Consentimiento: " + error.getMessage());
                arrancarSdk();
            });
        // Si ya había consentimiento de una vez anterior, no hay que esperar.
        arrancarSdk();
    }

    private void arrancarSdk() {
        if (!consentimiento.canRequestAds() || !iniciado.compareAndSet(false, true)) return;
        new Thread(() -> {
            MobileAds.initialize(app, s -> {
                listo = true;
                app.runOnUiThread(() -> {
                    cargar();
                    app.avisar("estado", estadoJson());
                });
            });
        }).start();
    }

    /** Deja cargados los anuncios que falten. */
    void cargar() {
        if (!listo || !activos()) return;
        if (inter == null && !cargandoInter && !BuildConfig.AD_INTERSTICIAL.isEmpty()) {
            cargandoInter = true;
            InterstitialAd.load(app, BuildConfig.AD_INTERSTICIAL, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        cargandoInter = false;
                        inter = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError e) {
                        cargandoInter = false;
                        Log.w(TAG, "Intersticial: " + e.getMessage());
                    }
                });
        }
        if (premio == null && !cargandoPremio && !BuildConfig.AD_RECOMPENSA.isEmpty()) {
            cargandoPremio = true;
            RewardedAd.load(app, BuildConfig.AD_RECOMPENSA, new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedAd ad) {
                        cargandoPremio = false;
                        premio = ad;
                        app.avisar("estado", estadoJson());
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError e) {
                        cargandoPremio = false;
                        Log.w(TAG, "Recompensa: " + e.getMessage());
                    }
                });
        }
    }

    void mostrarIntersticial() {
        InterstitialAd ad = inter;
        if (ad == null || !activos()) {
            app.avisar("intersticial", "{\"visto\":false}");
            cargar();
            return;
        }
        inter = null;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                app.avisar("intersticial", "{\"visto\":true}");
                cargar();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError e) {
                app.avisar("intersticial", "{\"visto\":false}");
                cargar();
            }
        });
        ad.show(app);
    }

    void mostrarRecompensa() {
        RewardedAd ad = premio;
        if (ad == null || !activos()) {
            app.avisar("recompensa", "{\"ok\":false}");
            cargar();
            return;
        }
        premio = null;
        final boolean[] gano = {false};
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                app.avisar("recompensa", "{\"ok\":" + gano[0] + "}");
                cargar();
                app.avisar("estado", estadoJson());
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError e) {
                app.avisar("recompensa", "{\"ok\":false}");
                cargar();
            }
        });
        ad.show(app, item -> gano[0] = true);
    }

    void opcionesPrivacidad() {
        UserMessagingPlatform.showPrivacyOptionsForm(app, error -> {
            if (error != null) Log.w(TAG, "Privacidad: " + error.getMessage());
            arrancarSdk();
            app.avisar("estado", estadoJson());
        });
    }

    /** Lo que el juego necesita saber para decidir qué mostrar. */
    String estadoJson() {
        boolean privacidad = BuildConfig.ANUNCIOS && consentimiento.getPrivacyOptionsRequirementStatus()
            == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
        return "{\"anuncios\":" + activos()
            + ",\"recompensa\":" + (activos() && premio != null)
            + ",\"sinAnuncios\":" + app.tienda.sinAnuncios()
            + ",\"precio\":" + Tienda.json(app.tienda.precio())
            + ",\"privacidad\":" + privacidad + "}";
    }
}
