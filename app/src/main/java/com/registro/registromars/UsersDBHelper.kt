package com.registro.registromars

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class UsersDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun setCategory(cat : Int) {
        val db = writableDatabase
        createCategory()

        if (getCategory() == -1) {
            db.execSQL("INSERT INTO $TABLE_NAME ($COLUMN_CATEGORY) VALUES ($cat)")
            Log.d("DB", "New row $COLUMN_CATEGORY $cat")
            Log.d("DB", "New row get $COLUMN_CATEGORY "+getCategory())
        } else {
            db.execSQL("UPDATE $TABLE_NAME SET $COLUMN_CATEGORY = $cat")
            Log.d("DB", "Updated $COLUMN_CATEGORY "+ getCategory())
        }
    }

    fun getCategory() : Int{
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT $COLUMN_CATEGORY FROM $TABLE_NAME", null)
        } catch (e: SQLiteException) {
            Log.d("DB", "NO DB?? $e")
            db.execSQL(SQL_CREATE_ENTRIES)
        }
        Log.d("DB", "Cursor: "+cursor!!.moveToFirst())
        var row: Int = -1
        if (cursor.moveToFirst()) {
            Log.d("DB", "Cursor-last: "+cursor.isAfterLast)
            try {
                while (cursor.isAfterLast == false) {
                     row = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)).toInt()
                    Log.d("DB", "FOUND ROW : " + row)

//                    return row
                    cursor.moveToNext()
                }
                return row
                Log.d("DB", "FOUND ROW : " + row)
            } catch (e : IllegalStateException){
                Log.d("DB", "ERROR DB $e")
            }
        }
        return -1
    }

    fun createCategory() {
        val db = writableDatabase
        db.execSQL(SQL_CREATE_ENTRIES)
    }


    fun deleteTable(): Int{
        val db = writableDatabase
        try{
            db.execSQL(SQL_DELETE_ENTRIES)
            return 0
        } catch(e: SQLiteException){

        }
        return 1
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val TABLE_NAME = "users"
        private val COLUMN_CATEGORY = "category"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_CATEGORY + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME

    }
}