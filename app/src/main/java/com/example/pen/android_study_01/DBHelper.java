package com.example.pen.android_study_01;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) instance = new DBHelper(context);
        return instance;
    }

    private DBHelper(Context context) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
                context,
                DBHelper.dbName,
                null,
                DBHelper.dbVersion);
        try {
            db = helper.getWritableDatabase(); //읽고 쓸 수 있는 DB
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(DBHelper.tag, "데이터베이스 얻어올 수 없음");
        }
    }

    public List<Memo> getMemosFromDB() {
        List<Memo> memos = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM myMemoTable;", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String content = c.getString(2);
            String detailContent = c.getString(3);
            memos.add(new Memo(id, name, content, detailContent));
        }

        return memos;
    }

    public void removeMemoFromDB(int id) {
        db.execSQL("DELETE FROM myMemoTable WHERE id = " + id + ";");
    }

    public void clearMemos() {
        db.execSQL("DELETE FROM myMemoTable;");
    }

    public void insertMemo(String name, String content, String detailContent) {
        db.execSQL("INSERT INTO myMemoTable (name,content,detailContent) VALUES(" + name + "," + content + "," + detailContent + ");");
    }


    public void addAllMemos(List<Memo> memos){
        // 로컬DB 에 데이터 옮기기
        for(int i = 0 ; i < memos.size(); i++) {
            String name = "'" + memos.get(i).getName() + "'";
            String content = "'" + memos.get(i).getContent() + "'";
            String detailContent = "'" + memos.get(i).getDetailContent()+ "'";

            db.execSQL("INSERT INTO myMemoTable (name,content,detailContent) VALUES(" + name + "," + content + "," + detailContent + ");");
        }
    }

    private SQLiteDatabase db;
    private final static String dbName = "st_file.db";
    private final static int dbVersion = 2;//데이터베이스 버전
    private static String tag = "SQLite"; //log에서 사용할 태그


    public class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //DDL
        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table myMemoTable(id integer primary key autoincrement, name text, content text, detailContent text)";
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
}
