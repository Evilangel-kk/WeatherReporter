package com.example.thirdexperiment

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MyDataBaseHelper(context: AppCompatActivity, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    private val createCity = ("create table City (" +
            " id integer primary key," +
            "name text)")

    //存储未来天气状况的数据库格式:
    private val createWeather = ("create table weather("
            + "dayOfWeek text,"
            + "date text,"
            + "iconDay text,"
            + "iconNight text,"
            + "maxTemp text,"
            + "minTemp text,"
            + "textDay text,"
            + "textNight text,"
            + "windSpeedDay text,"
            + "windSpeedNight text,"
            + "windDirDay text,"
            + "windDirNight text,"
            + "humidity text,"
            + "pressure text)")
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createCity)
        db.execSQL(createWeather)
        Log.d("DataBase", "Created")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}