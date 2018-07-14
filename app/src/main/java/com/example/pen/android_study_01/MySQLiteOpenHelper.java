package com.example.pen.android_study_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //DDL
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table myMemoTable(id integer primary key autoincrement, title text, content text, detail text)";
        db.execSQL(sql);
        Log.d("SQLite","SQLite OnCreate 호출");
    }

    //DB 버전이 바뀔 때 호출되는 함수
    //스키마 변경 또는 데이타 마이그레이션
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SQLite","SQLite OnUpgrade 호출");
        String sql = "drop table myMemoTable";
        db.execSQL(sql);
        onCreate(db);//테이블 다시 생성
    }
}
