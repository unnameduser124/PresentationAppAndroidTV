package com.example.productpresentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StartActivityOnBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent) {
        if(Intent.ACTION_BOOT_COMPLETED == p1.action){
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}