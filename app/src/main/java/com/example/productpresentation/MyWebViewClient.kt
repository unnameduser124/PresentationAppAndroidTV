package com.example.productpresentation

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity

class MyWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        println(url)
        if (url.toString() == "https://www.tutorialspoint.com/index.htm") {
            // This is my web site, so do not override; let my WebView load the page
            return false
        }
        return true
    }
}