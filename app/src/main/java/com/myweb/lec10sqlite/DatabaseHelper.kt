package com.myweb.lec10sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_YEAR INTEGER)"
        db?.execSQL(CREATE_TABLE)
        /// Test Insert Movie
        val  sqlInsert :String = "INSERT INTO $TABLE_NAME VALUES(1,'Up',2016)"

        db?.execSQL(sqlInsert);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    //get all Movies
    fun getAllMovies(): ArrayList<Movie> {
        val movie = ArrayList<Movie>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from $TABLE_NAME", null)
        } catch (e: SQLiteException) {
            onCreate(db)
            return ArrayList()
        }
        var id : Int
        var title: String
        var year: Int
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                year = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR))

                movie.add(Movie(id, title, year))
                cursor.moveToNext()
            }
        }
        db.close()
        return movie
    }
    ///// Insert Movie
    fun insertMovie(movie: Movie): Long{
        // Gets the data repository in write mode
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
       // values.put(COLUMN_ID, movie.id)
        values.put(COLUMN_TITLE, movie.title)
        values.put(COLUMN_YEAR, movie.year)
        // Insert the new row
        val success  = db.insert(TABLE_NAME, null, values)
        db.close()
        return success
    }
    ///// Update Movie
    fun updateMovie(movie: Movie):Int{
        val db = writableDatabase
        val values  = ContentValues()
        values.put(COLUMN_TITLE, movie.title)
        values.put(COLUMN_YEAR, movie.year)
        // Updating Row
        val success = db.update(TABLE_NAME, values,"$COLUMN_ID = ?",arrayOf(movie.id.toString()))
        db.close()
        return success
    }

    ///// Delete Movie
    fun deleteMovie(movie_id: Int):Int {
        val db = writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(movie_id.toString()))
        db.close()
        return success
    }
    companion object {
        private val DB_NAME = "MovieDB"
        private val DB_VERSION = 1
        private val TABLE_NAME = "Movie"
        private val COLUMN_ID = "id"
        private val COLUMN_TITLE = "title"
        private val COLUMN_YEAR = "year"
    }
}