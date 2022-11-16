package com.example.productpresentation

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class CustomWebViewClient(val link: String) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        println("$url ${getDomain()}")
        if(!url.toString().contains(getDomain())){
            Toast.makeText(view?.context, "not allowed/invalid link", Toast.LENGTH_SHORT).show()
            return true
        }
        else{
            return false
        }
    }

    private fun getDomain(): String {
        var slashCounter = 0
        var domainName = ""
        if(link.contains("http")){
            for(i in 0..link.length-1){
                if(slashCounter==2){
                    domainName+=link[i]
                }
                if(link[i]=='/'){
                    slashCounter++
                }
            }
        }
        else{
            for(i in 0..link.length-1){
                if(link[i]!='/'){
                    domainName+=link[i]
                }
                else{
                    break
                }
            }
        }
        if(domainName.contains("www")){
            domainName = domainName.substring(4, domainName.length)
        }
        return domainName
    }
}