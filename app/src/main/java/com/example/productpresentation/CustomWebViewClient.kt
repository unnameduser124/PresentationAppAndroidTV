package com.example.productpresentation

import android.os.Handler
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return !url.toString().contains("https://www.google.com")
    }
}