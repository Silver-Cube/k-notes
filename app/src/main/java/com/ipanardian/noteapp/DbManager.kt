package com.ipanardian.noteapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

/**
 * Created by ipan on 9/16/17.
 */

class DbManager {
    val dbName = "NoteApp"
    val dbTable = "Notes"
    val colID = "ID"
    val colTitle = "Title"
    val colDes = "Description"
    val dbVersion = 1

    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS "+ dbTable +" ("+ colID +" INTEGER PRIMARY KEY, "+
                          colTitle +" TEXT, "+ colDes +" TEXT);"

    var sqlDB: SQLiteDatabase? = null

    constructor(context: Context) {
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes: SQLiteOpenHelper {
        var context: Context? = null

        constructor(context: Context): super(context, dbName, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(context, " Database is created", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS "+ dbTable)
        }
    }

    fun Insert(values: ContentValues): Long {
        return sqlDB!!.insert(dbTable, "", values)
    }

    fun Query(projection: Array<String>, selection: String, selectionArgs: Array<String>, order: String): Cursor {
        val builder = SQLiteQueryBuilder()
        builder.tables = dbTable

        val cursor = builder.query(sqlDB, projection, selection, selectionArgs, null, null, order)
        return cursor
    }

    fun Delete(selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.delete(dbTable, selection, selectionArgs)
    }

    fun Update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.update(dbTable, values, selection, selectionArgs)
    }
}