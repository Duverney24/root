package com.raiz.juego;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.Collections;
import java.util.List;

/**
 * Compra única "Quitar anuncios" con Google Play Billing.
 * El producto se crea en Play Console → Monetizar → Productos integrados en la
 * aplicación, con el ID {@link #PRODUCTO}. La compra se recuerda en el
 * teléfono para que tampoco haya anuncios sin internet.
 */
class Tienda {
    static final String PRODUCTO = "quitar_anuncios";
    private static final String TAG = "ROOT-tienda";
    private final MainActivity app;
    private final SharedPreferences prefs;
    private final BillingClient cliente;
    private volatile ProductDetails producto;
    private volatile boolean conectado;

    Tienda(MainActivity app) {
        this.app = app;
        this.prefs = app.getSharedPreferences("root-tienda", 0);
        this.cliente = BillingClient.newBuilder(app)
            .setListener(this::alActualizarCompras)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableAutoServiceReconnection()
            .build();
    }

    boolean sinAnuncios() {
        return prefs.getBoolean("sin_anuncios", false);
    }

    /** Precio con su moneda (por ejemplo "COP 8.000"), o "" si la tienda no está disponible. */
    String precio() {
        if (sinAnuncios()) return "";
        ProductDetails p = producto;
        if (p == null || p.getOneTimePurchaseOfferDetails() == null) return "";
        return p.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    void conectar() {
        cliente.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult r) {
                if (r.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    // Pasa si la app no se instaló desde Google Play: la compra no se ofrece.
                    Log.w(TAG, "Google Play Billing no disponible: " + r.getDebugMessage());
                    return;
                }
                conectado = true;
                revisarCompras();
                buscarProducto();
            }

            @Override
            public void onBillingServiceDisconnected() {
                conectado = false;
            }
        });
    }

    private void buscarProducto() {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(Collections.singletonList(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCTO)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()))
            .build();
        cliente.queryProductDetailsAsync(params, (r, resultado) -> {
            List<ProductDetails> lista = resultado.getProductDetailsList();
            if (r.getResponseCode() == BillingClient.BillingResponseCode.OK && !lista.isEmpty()) {
                producto = lista.get(0);
                app.avisar("estado", app.anuncios.estadoJson());
            } else {
                Log.w(TAG, "No se encontró el producto " + PRODUCTO + ": " + r.getDebugMessage());
            }
        });
    }

    /** Pregunta a Google Play qué compras tiene esta cuenta (también recupera compras hechas antes). */
    void revisarCompras() {
        if (!conectado) return;
        cliente.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
            (r, compras) -> {
                if (r.getResponseCode() != BillingClient.BillingResponseCode.OK) return;
                boolean tiene = false;
                for (Purchase c : compras) tiene |= procesar(c);
                guardar(tiene);
            });
    }

    void comprar() {
        ProductDetails p = producto;
        if (!conectado || p == null || sinAnuncios()) {
            app.avisar("compra", "{\"ok\":false}");
            return;
        }
        BillingFlowParams params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(Collections.singletonList(
                BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(p).build()))
            .build();
        cliente.launchBillingFlow(app, params);
    }

    private void alActualizarCompras(BillingResult r, List<Purchase> compras) {
        boolean tiene = false;
        if (r.getResponseCode() == BillingClient.BillingResponseCode.OK && compras != null) {
            for (Purchase c : compras) tiene |= procesar(c);
        } else if (r.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            revisarCompras();
            return;
        }
        if (tiene) guardar(true);
        app.avisar("compra", "{\"ok\":" + tiene + "}");
    }

    /** true si la compra es "Quitar anuncios" y ya está pagada. La confirma ante Google si hace falta. */
    private boolean procesar(Purchase c) {
        if (!c.getProducts().contains(PRODUCTO)) return false;
        if (c.getPurchaseState() != Purchase.PurchaseState.PURCHASED) return false;
        if (!c.isAcknowledged()) {
            // Google devuelve el dinero si la compra no se confirma en tres días.
            cliente.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(c.getPurchaseToken()).build(),
                ar -> Log.i(TAG, "Compra confirmada: " + ar.getResponseCode()));
        }
        return true;
    }

    private void guardar(boolean tiene) {
        if (tiene == sinAnuncios()) return;
        prefs.edit().putBoolean("sin_anuncios", tiene).apply();
        app.avisar("estado", app.anuncios.estadoJson());
    }

    void cerrar() {
        cliente.endConnection();
    }

    static String json(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char ch : s.toCharArray()) {
            if (ch == '"' || ch == '\\') b.append('\\').append(ch);
            else if (ch < 0x20) b.append(String.format("\\u%04x", (int) ch));
            else b.append(ch);
        }
        return b.append('"').toString();
    }
}
