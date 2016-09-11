package com.example.administrator.a2048;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/10.
 */
public class MySqlite extends SQLiteOpenHelper {
    public MySqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table fenshu(_id integer primary key autoincrement, name char(10), fs integer(20))");
        db.execSQL("insert into fenshu (name,fs)values(?,?)",new Object[]{"null",0});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}