package com.example.productpresentation.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.productpresentation.database.DatabaseConstants.CREATE_CONFIGURATION_TABLE
import com.example.productpresentation.database.DatabaseConstants.CREATE_URI_TABLE
import com.example.productpresentation.database.DatabaseConstants.DATABASE_NAME
import com.example.productpresentation.database.DatabaseConstants.DATABASE_VERSION
import com.example.productpresentation.firstStart

open class ConfigurationDatabase(private val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        firstStart = true
        db?.execSQL(CREATE_CONFIGURATION_TABLE)
        db?.execSQL(CREATE_URI_TABLE)
        ConfigurationDBService(context).insertDefaultConfigurationRecord(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

}