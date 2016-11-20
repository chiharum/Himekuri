package com.ogc.himekuri;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DatabaseFile = "himekuri_database.db";
    static final int DatabaseVersion = 1;
    static final String DiaryTable = "diary";
    static final String HimekuriRecordTable = "himekuri_record";

    static final String DB_id = "id";
    static final String DB_Record_onDate = "on_date";
    static final String DB_Record_himekuriDate = "himekuri_date";

    public MySQLiteOpenHelper(Context context){
        super(context, DatabaseFile, null, DatabaseVersion);
    }

    public void onCreate(SQLiteDatabase database){

        database.execSQL("create table " + DiaryTable + " (" + DB_id + " integer primary key autoincrement not null, date integer not null, diary string not null");
        database.execSQL("create table " + HimekuriRecordTable + " (" + DB_id + " integer primary key autoincrement not null, " + DB_Record_onDate + " integer not null, " + DB_Record_himekuriDate + " integer not null)");
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

    }
}
