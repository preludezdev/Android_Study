package com.example.pen.android_study_01;

import android.database.sqlite.SQLiteDatabase;

public class C {
    public static MySQLiteOpenHelper helper;
    public static String dbName = "st_file.db";
    public static int dbVersion = 2;//데이터베이스 버전
    public static SQLiteDatabase db;
    public static String tag = "SQLite"; //log에서 사용할 태그

    public boolean dataChanged = false;


}
