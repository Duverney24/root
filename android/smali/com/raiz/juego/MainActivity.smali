.class public Lcom/raiz/juego/MainActivity;
.super Landroid/app/Activity;

# ROOT · pantalla única que muestra el juego.
# Carga la versión publicada en GitHub Pages (así recibe actualizaciones);
# si no hay conexión, Cliente abre la copia que viene dentro de la app.

.field private web:Landroid/webkit/WebView;

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V
    return-void
.end method

.method private immersive()V
    .registers 3
    invoke-virtual {p0}, Lcom/raiz/juego/MainActivity;->getWindow()Landroid/view/Window;
    move-result-object v0
    invoke-virtual {v0}, Landroid/view/Window;->getDecorView()Landroid/view/View;
    move-result-object v0
    const/16 v1, 0x1706
    invoke-virtual {v0, v1}, Landroid/view/View;->setSystemUiVisibility(I)V
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .registers 6
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V
    invoke-virtual {p0}, Lcom/raiz/juego/MainActivity;->getWindow()Landroid/view/Window;
    move-result-object v0
    const/16 v1, 0x80
    invoke-virtual {v0, v1}, Landroid/view/Window;->addFlags(I)V
    new-instance v0, Landroid/webkit/WebView;
    invoke-direct {v0, p0}, Landroid/webkit/WebView;-><init>(Landroid/content/Context;)V
    iput-object v0, p0, Lcom/raiz/juego/MainActivity;->web:Landroid/webkit/WebView;
    invoke-virtual {v0}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;
    move-result-object v1
    const/4 v2, 0x1
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setDomStorageEnabled(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setDatabaseEnabled(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setAllowFileAccess(Z)V
    const/4 v3, 0x0
    invoke-virtual {v1, v3}, Landroid/webkit/WebSettings;->setMediaPlaybackRequiresUserGesture(Z)V
    const/high16 v3, -0x1000000
    invoke-virtual {v0, v3}, Landroid/webkit/WebView;->setBackgroundColor(I)V
    new-instance v3, Lcom/raiz/juego/Cliente;
    invoke-direct {v3}, Lcom/raiz/juego/Cliente;-><init>()V
    invoke-virtual {v0, v3}, Landroid/webkit/WebView;->setWebViewClient(Landroid/webkit/WebViewClient;)V
    const-string v3, "https://duverney24.github.io/root/"
    invoke-virtual {v0, v3}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V
    invoke-virtual {p0, v0}, Lcom/raiz/juego/MainActivity;->setContentView(Landroid/view/View;)V
    invoke-direct {p0}, Lcom/raiz/juego/MainActivity;->immersive()V
    return-void
.end method

.method public onWindowFocusChanged(Z)V
    .registers 2
    invoke-super {p0, p1}, Landroid/app/Activity;->onWindowFocusChanged(Z)V
    if-eqz p1, :cond_0
    invoke-direct {p0}, Lcom/raiz/juego/MainActivity;->immersive()V
    :cond_0
    return-void
.end method

.method protected onPause()V
    .registers 2
    invoke-super {p0}, Landroid/app/Activity;->onPause()V
    iget-object v0, p0, Lcom/raiz/juego/MainActivity;->web:Landroid/webkit/WebView;
    if-eqz v0, :cond_0
    invoke-virtual {v0}, Landroid/webkit/WebView;->onPause()V
    :cond_0
    return-void
.end method

.method protected onResume()V
    .registers 2
    invoke-super {p0}, Landroid/app/Activity;->onResume()V
    iget-object v0, p0, Lcom/raiz/juego/MainActivity;->web:Landroid/webkit/WebView;
    if-eqz v0, :cond_0
    invoke-virtual {v0}, Landroid/webkit/WebView;->onResume()V
    :cond_0
    return-void
.end method

.method protected onDestroy()V
    .registers 2
    iget-object v0, p0, Lcom/raiz/juego/MainActivity;->web:Landroid/webkit/WebView;
    if-eqz v0, :cond_0
    invoke-virtual {v0}, Landroid/webkit/WebView;->destroy()V
    :cond_0
    invoke-super {p0}, Landroid/app/Activity;->onDestroy()V
    return-void
.end method
