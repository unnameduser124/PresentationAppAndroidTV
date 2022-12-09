package com.example.productpresentation.database

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import com.example.productpresentation.database.DatabaseConstants.URITable.TABLE_NAME
import com.example.productpresentation.database.DatabaseConstants.URITable.URI_VALUE
import com.example.productpresentation.tv.MediaUri

class UriDBService(context: Context): ConfigurationDatabase(context) {

    fun addUri(uri: Uri){
        val db = this.writableDatabase

        val contentValues = ContentValues().apply{
            put(URI_VALUE, uri.path)
        }

        db.insert(TABLE_NAME, null, contentValues)
    }

    fun addUriList(uriList: List<MediaUri>){
        val db = this.writableDatabase
        db.beginTransaction();
        try {
            uriList.forEach {
                if(it.path!=""){
                    val contentValues = ContentValues().apply{
                        println(it.path)
                        put(URI_VALUE, it.path)
                    }

                    db.insert(TABLE_NAME, null, contentValues)
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    fun removeUri(id: Long){
        val db = this.writableDatabase

        val selection = BaseColumns._ID

        val selectionArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun clearTable(){
        val db = this.writableDatabase

        db.delete(TABLE_NAME, null, null)
    }

    fun updateUri(uri: Uri, id: Long){
        val db = this.writableDatabase

        val selection = BaseColumns._ID

        val selectionArgs = arrayOf(id.toString())

        val contentValues = ContentValues().apply{
            put(URI_VALUE, uri.path)
        }

        db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun getAllUris(): List<MediaUri>{
        val db = this.writableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            URI_VALUE
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

        val uriList = mutableListOf<MediaUri>()

        with(cursor){
            while(cursor.moveToNext()){
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val path = getString(getColumnIndexOrThrow(URI_VALUE))
                println(path)
                uriList.add(MediaUri(id, path))
            }
        }

        return uriList
    }

    fun getUri(id: Long): MediaUri?{
        val db = this.writableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            URI_VALUE
        )

        val selection = BaseColumns._ID

        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var uri: MediaUri? = null

        with(cursor){
            while(cursor.moveToNext()){
                val uriID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val path = getString(getColumnIndexOrThrow(URI_VALUE))
                uri = MediaUri(uriID, path)
            }
        }

        return uri
    }
}