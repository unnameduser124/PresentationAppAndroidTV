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
        setContentView(binding.root)
        binding.openUrlButton.setOnClickListener {
            val link = binding.urlWebPageInput.text.toString()
            val webView = WebView(this)
            val webViewClient = CustomWebViewClient(link)
            webView.webViewClient  = webViewClient
            webView.loadUrl(link)
            setContentView(webView)
        }
    }
}