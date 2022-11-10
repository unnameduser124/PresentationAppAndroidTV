package com.example.productpresentation

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.productpresentation.databinding.WebViewActivityLayoutBinding

class WebViewActivity: AppCompatActivity() {

    private lateinit var binding: WebViewActivityLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebViewActivityLayoutBinding.inflate(layoutInflater)
        val webView = WebView(this)
        val webViewClient = CustomWebViewClient()
        webView.webViewClient  = webViewClient
        setContentView(webView)
        webView.loadUrl("https://google.com")
    }
}