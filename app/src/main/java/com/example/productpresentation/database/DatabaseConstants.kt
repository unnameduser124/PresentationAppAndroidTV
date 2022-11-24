package com.example.productpresentation.database

import android.provider.BaseColumns

object DatabaseConstants {

    const val DATABASE_NAME = "ConfigurationDatabase"
    const val DATABASE_VERSION = 1

    const val CREATE_CONFIGURATION_TABLE = "CREATE TABLE ${ConfigurationTable.TABLE_NAME} (" +
            "${ConfigurationTable.PASSWORD} TEXT NOT NULL," +
            "${ConfigurationTable.CONFIGURATION_CODE} TEXT NOT NULL," +
            "${ConfigurationTable.PHOTO_DISPLAY_TIME} INTEGER," +
            "${ConfigurationTable.SAVED_MEDIA_TYPE} TEXT NOT NULL," +
            "${ConfigurationTable.LOCK_START_TIME} INTEGER)"

    const val CREATE_URI_TABLE = "CREATE TABLE ${URITable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${URITable.URI_VALUE})"

    object ConfigurationTable: BaseColumns{
        const val TABLE_NAME = "Configuration"
        const val PASSWORD = "password"
        const val CONFIGURATION_CODE = "configurationCode"
        const val PHOTO_DISPLAY_TIME = "photoDisplayTime"
        const val SAVED_MEDIA_TYPE = "savedMediaType"
        const val LOCK_START_TIME = "lockStartTime"
    }

    object URITable: BaseColumns{
        const val TABLE_NAME = "URIs"
        const val URI_VALUE = "URIValue"
    }
}