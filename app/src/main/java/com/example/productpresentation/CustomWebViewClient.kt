package com.example.productpresentation

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

//custom web view client implemented to override navigation to pages outside domain
class CustomWebViewClient(private val link: String) : WebViewClient() {

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        println("$url ${getDomain()}")
        return if(!url.toString().contains(getDomain())){
            Toast.makeText(view?.context, "not allowed/invalid link", Toast.LENGTH_SHORT).show()
            true
        } else{
            false
        }
    }

    //function extracting domain address from page link
    private fun getDomain(): String {
        var slashCounter = 0
        var domainName = ""
        if(link.contains("http")){
            for(i in link.indices){
                if(slashCounter==2){
                    domainName+=link[i]
                }
                if(link[i]=='/'){
                    slashCounter++
                }
            }
        }
        else{
            for(i in link.indices){
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