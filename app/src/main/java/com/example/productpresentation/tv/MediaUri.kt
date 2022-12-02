package com.example.productpresentation.tv

import android.net.Uri

class MediaUri(val id: Long, var path: String){

    fun getUri(): Uri{
        return Uri.parse(path)
    }
}