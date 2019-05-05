package com.registro.registromars

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


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

    fun setCategory(category: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        try {
            values.put(DBContract.CategoryEntry.COLUMN_CATEGORY, category)

            val newRowId = db.insert(DBContract.CategoryEntry.TABLE_NAME, null, values)
        } catch (e: SQLiteException){
            db.execSQL(SQL_CREATE_ENTRIES)
            values.put(DBContract.CategoryEntry.COLUMN_CATEGORY, category)
            val newRowId = db.insert(DBContract.CategoryEntry.TABLE_NAME, null, values)
            return false

        }
        return true
    }

    fun getCategory() : Int{
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select category from " + DBContract.CategoryEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return -1
        }
        var category : Int = -1
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                category = cursor.getString(cursor.getColumnIndex(DBContract.CategoryEntry.COLUMN_CATEGORY)).toInt()
                return category
                cursor.moveToNext()
            }
        }
        return category
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

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.CategoryEntry.TABLE_NAME + " (" +
                    DBContract.CategoryEntry.COLUMN_CATEGORY + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.CategoryEntry.TABLE_NAME
    }
}