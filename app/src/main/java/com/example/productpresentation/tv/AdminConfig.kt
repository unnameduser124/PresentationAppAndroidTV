package com.example.productpresentation.tv


data class AdminConfig(var accessCode: String, var password: String, var mediaType: MediaType, var photoDisplayTime: Int, var lockStartTime: Long)

enum class MediaType{
    Photos,
    Video,
    WebPage,
    NoSelection
}
fun getMediaType(stringMediaType: String): MediaType{
    return if(MediaType.values().find { it.toString() == stringMediaType }!=null){
        MediaType.values().first { it.toString() == stringMediaType }
    }else{
        MediaType.NoSelection
    }
}
