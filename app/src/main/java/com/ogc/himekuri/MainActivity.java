package com.ogc.himekuri;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    TextView monthText, dateText, diaryText;
    int screenHimekuriDate, todayYear, todayMonth, todayDateOfMonth, todayDate;

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

        final Calendar calendar = Calendar.getInstance();
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH);
        todayDateOfMonth = calendar.get(Calendar.DATE);
        todayDate = todayDateOfMonth + todayMonth * 100 + todayYear * 10000;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirstActivation = sharedPreferences.getBoolean(Prefs_isFirstActivation, true);
        if(isFirstActivation){
            isFirstActivation = true;
            sharedPreferences.edit().putBoolean("isFirstActivation", isFirstActivation).apply();
            //今日の日付を表示
            monthText.setText(String.valueOf(todayMonth));
            dateText.setText(String.valueOf(todayDateOfMonth));
            //今日の日付を保存
            saveHimekuri(todayDate);
        }else{
            screenHimekuriDate = searchRecordAndGetLastDate();
        }
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

    public void saveHimekuri(int date){
        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.DB_Record_onDate, todayDate);
        values.put(MySQLiteOpenHelper.DB_Record_himekuriDate, date);
        database.insert(MySQLiteOpenHelper.HimekuriRecordTable, null, values);
    }
}
