package com.example.productpresentation.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.productpresentation.admin
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.PASSWORD
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.CONFIGURATION_CODE
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.FILL_SCREEN
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.PHOTO_DISPLAY_TIME
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.SAVED_MEDIA_TYPE
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.LOCK_START_TIME
import com.example.productpresentation.database.DatabaseConstants.ConfigurationTable.TABLE_NAME
import com.example.productpresentation.tv.AdminConfig
import com.example.productpresentation.tv.getMediaType

class ConfigurationDBService(val context: Context): ConfigurationDatabase(context) {
    fun insertDefaultConfigurationRecord(db: SQLiteDatabase?){
        val contentValues = ContentValues().apply {
            put(PASSWORD, "")
            put(CONFIGURATION_CODE, "")
            put(PHOTO_DISPLAY_TIME, 10)
            put(SAVED_MEDIA_TYPE, "NoSelection")
            put(LOCK_START_TIME, 0)
            put(FILL_SCREEN, 0)
        }

        db?.insert(TABLE_NAME, null, contentValues)
    }

    fun getConfiguration(){
        val db = this.readableDatabase

        val projection = arrayOf(
            PASSWORD,
            CONFIGURATION_CODE,
            PHOTO_DISPLAY_TIME,
            SAVED_MEDIA_TYPE,
            LOCK_START_TIME,
            FILL_SCREEN
        )

        val cursor = db.query(
            TABLE_NAME,
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
                val fillScreen = getInt(getColumnIndexOrThrow(FILL_SCREEN))

                admin = AdminConfig(configCode, password, getMediaType(mediaType), photoDisplayTime, lockStartTime, fillScreen==1)
            }
        }
        cursor.close()
    }

    fun updateConfiguration(config: AdminConfig){
        val fillScreen = if(config.fillScreen) { 1 } else { 0 }

        val contentValues = ContentValues().apply {
            put(PASSWORD, config.password)
            put(CONFIGURATION_CODE, config.accessCode)
            put(PHOTO_DISPLAY_TIME, config.photoDisplayTime)
            put(SAVED_MEDIA_TYPE, config.mediaType.toString())
            put(LOCK_START_TIME, config.lockStartTime)
            put(FILL_SCREEN, fillScreen)
        }
        println(config.mediaType)

        this.writableDatabase.update(TABLE_NAME, contentValues, null, null)
    }
}