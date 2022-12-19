package com.example.productpresentation

import android.net.Uri
import com.example.productpresentation.tv.AdminConfig
import com.example.productpresentation.tv.MediaType
import com.example.productpresentation.tv.MediaUri
import java.util.*

var admin: AdminConfig = AdminConfig("code", "password", MediaType.NoSelection, -1, 0, false)
var configurationLocked = false
var wrongPasswordCounter = 0
var lockTimeStart = Calendar.getInstance().timeInMillis
var uriList = mutableListOf<MediaUri>()
var firstStart = false