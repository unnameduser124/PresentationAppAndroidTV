package com.example.productpresentation

import com.example.productpresentation.tv.AdminConfig
import java.util.*

val admin: AdminConfig = AdminConfig("code", "password")
var configurationLocked = false
var wrongPasswordCounter = 0
var lockTimeStart = Calendar.getInstance().timeInMillis