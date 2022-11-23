package com.example.productpresentation

import android.net.Uri
import android.webkit.*
import android.widget.Toast


//custom web view client implemented to override navigation to pages outside domain
class CustomChromeWebViewClient(private val link: String) : WebChromeClient() {

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
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