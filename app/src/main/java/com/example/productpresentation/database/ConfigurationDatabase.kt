package com.example.productpresentation.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.productpresentation.admin
import com.example.productpresentation.database.DatabaseConstants.CREATE_CONFIGURATION_TABLE
import com.example.productpresentation.database.DatabaseConstants.CREATE_URI_TABLE
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.CONFIGURATION_CODE
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.LOCK_START_TIME
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.PASSWORD
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.PHOTO_DISPLAY_TIME
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.SAVED_MEDIA_TYPE
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.TABLE_NAME
import com.example.productpresentation.database.DatabaseConstants.DATABASE_NAME
import com.example.productpresentation.database.DatabaseConstants.DATABASE_VERSION
import com.example.productpresentation.tv.AdminConfig
import com.example.productpresentation.tv.getMediaType

class ConfigurationDatabase(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CONFIGURATION_TABLE)
        db?.execSQL(CREATE_URI_TABLE)
        insertDefaultConfigurationRecord(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    private fun insertDefaultConfigurationRecord(db: SQLiteDatabase?){

        val contentValues = ContentValues().apply {
            put(PASSWORD, "password")
            put(CONFIGURATION_CODE, "code")
            put(PHOTO_DISPLAY_TIME, 30)
            put(SAVED_MEDIA_TYPE, "NoSelection")
            put(LOCK_START_TIME, 0)
        }

        db?.insert(ConfigurationTable.TABLE_NAME, null, contentValues)
    }

    fun getConfiguration(){
        val db = this.readableDatabase

        val projection = arrayOf(PASSWORD, CONFIGURATION_CODE, PHOTO_DISPLAY_TIME, SAVED_MEDIA_TYPE, LOCK_START_TIME)


        val cursor = db.query(
            ConfigurationTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val password = getString(getColumnIndexOrThrow(PASSWORD))
                val configCode = getString(getColumnIndexOrThrow(CONFIGURATION_CODE))
                val photoDisplayTime = getInt(getColumnIndexOrThrow(PHOTO_DISPLAY_TIME))
                val mediaType = getString(getColumnIndexOrThrow(SAVED_MEDIA_TYPE))
                val lockStartTime = getLong(getColumnIndexOrThrow(LOCK_START_TIME))
                println("$password $configCode $photoDisplayTime $mediaType $lockStartTime")

                admin = AdminConfig(configCode, password, getMediaType(mediaType), photoDisplayTime, lockStartTime)
            }
        }
        cursor.close()
    }
}