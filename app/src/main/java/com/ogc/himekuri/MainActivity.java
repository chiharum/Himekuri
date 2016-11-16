package com.ogc.himekuri;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    TextView monthText, dateText, diaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        database = mySQLiteOpenHelper.getWritableDatabase();

        monthText = (TextView)findViewById(R.id.monthText);
        dateText = (TextView)findViewById(R.id.dateText);
        diaryText = (TextView)findViewById(R.id.diaryText);
    }

    public void searchRecordAndGetLastDate(){

    }
}
