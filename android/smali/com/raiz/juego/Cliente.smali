.class public Lcom/raiz/juego/Cliente;
.super Landroid/webkit/WebViewClient;

# Si la versión en línea no carga (sin internet o error del servidor),
# abre la copia del juego que viene dentro de la app.

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Landroid/webkit/WebViewClient;-><init>()V
    return-void
.end method

.method private respaldo(Landroid/webkit/WebView;)V
    .registers 4
    invoke-virtual {p1}, Landroid/webkit/WebView;->getUrl()Ljava/lang/String;
    move-result-object v0
    if-eqz v0, :cond_load
    const-string v1, "file:"
    invoke-virtual {v0, v1}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z
    move-result v1
    if-nez v1, :cond_end
    :cond_load
    const-string v0, "file:///android_asset/index.html"
    invoke-virtual {p1, v0}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V
    :cond_end
    return-void
.end method

.method public onReceivedError(Landroid/webkit/WebView;Landroid/webkit/WebResourceRequest;Landroid/webkit/WebResourceError;)V
    .registers 5
    invoke-interface {p2}, Landroid/webkit/WebResourceRequest;->isForMainFrame()Z
    move-result v0
    if-eqz v0, :cond_0
    invoke-direct {p0, p1}, Lcom/raiz/juego/Cliente;->respaldo(Landroid/webkit/WebView;)V
    :cond_0
    return-void
.end method

.method public onReceivedHttpError(Landroid/webkit/WebView;Landroid/webkit/WebResourceRequest;Landroid/webkit/WebResourceResponse;)V
    .registers 6
    invoke-interface {p2}, Landroid/webkit/WebResourceRequest;->isForMainFrame()Z
    move-result v0
    if-eqz v0, :cond_0
    invoke-virtual {p3}, Landroid/webkit/WebResourceResponse;->getStatusCode()I
    move-result v1
    const/16 v0, 0x190
    if-lt v1, v0, :cond_0
    invoke-direct {p0, p1}, Lcom/raiz/juego/Cliente;->respaldo(Landroid/webkit/WebView;)V
    :cond_0
    return-void
.end method
