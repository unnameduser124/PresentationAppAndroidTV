package com.example.productpresentation.bootstart

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.productpresentation.MainActivity

//BroadcastReceiver that responds to ACTION_BOOT_COMPLETED intent
//doesn't work on android 10+
class StartActivityOnBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent) {
        if(Intent.ACTION_BOOT_COMPLETED == p1.action){
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(intent)
        }
    }
}