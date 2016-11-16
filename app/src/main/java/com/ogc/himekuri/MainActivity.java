package com.ogc.himekuri;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    TextView monthText, dateText, diaryText;

    int screenHimekuriDate;
    boolean isFirstActivation;

    static final String Prefs_isFirstActivation = "isFirstActivation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        database = mySQLiteOpenHelper.getWritableDatabase();

        monthText = (TextView)findViewById(R.id.monthText);
        dateText = (TextView)findViewById(R.id.dateText);
        diaryText = (TextView)findViewById(R.id.diaryText);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirstActivation = sharedPreferences.getBoolean(Prefs_isFirstActivation, true);
        if(isFirstActivation){

        }

        screenHimekuriDate = searchRecordAndGetLastDate();
    }

    public int searchRecordAndGetLastDate(){

        Cursor cursor = null;
        int resultDate = 0;

        try{
            cursor = database.query(MySQLiteOpenHelper.HimekuriRecordTable, new String[]{"himekuri_date"}, null, null, null, null, null);
            int indexHimekuri_date = cursor.getColumnIndex("himekuri_date");

            while(cursor.moveToNext()){
                resultDate = cursor.getInt(indexHimekuri_date);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }

        return resultDate;
    }
}
