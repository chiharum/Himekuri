package com.ogc.himekuri;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    TextView monthText, dateText, diaryText;
    EditText diaryEditText;
    int screenHimekuriDate,screenYear, screenMonth, screenDateOfMonth, todayYear, todayMonth, todayDateOfMonth, todayDate, screenMaxDateOfMonth;
    String diary;

    boolean isFirstActivation;

    static final String Prefs_isFirstActivation = "isFirstActivation";
    static final String Prefs_lastVersion = "lastVersion";
    static final int PresentVersion = 1;
    static final String Intent_todayDate = "todayDate";

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
        todayMonth = calendar.get(Calendar.MONTH) + 1;
        todayDateOfMonth = calendar.get(Calendar.DATE);
        todayDate = todayDateOfMonth + todayMonth * 100 + todayYear * 10000;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //ユーザー別バージョン管理
        sharedPreferences.edit().putInt(Prefs_lastVersion, PresentVersion).apply();

        isFirstActivation = sharedPreferences.getBoolean(Prefs_isFirstActivation, true);
        if(isFirstActivation) {
            isFirstActivation = false;
            sharedPreferences.edit().putBoolean("isFirstActivation", isFirstActivation).apply();
            //今日の日付を表示
            screenYear = todayYear;
            screenMonth = todayMonth;
            screenDateOfMonth = todayDateOfMonth;
            //今日の日付を保存
            if(searchRecordAndGetLastDate() != todayDate){
                saveHimekuri(todayDate);
            }
        }else{
            screenHimekuriDate = searchRecordAndGetLastDate();
            screenYear = screenHimekuriDate / 10000;
            screenMonth = (screenHimekuriDate -screenYear * 10000) / 100;
            screenDateOfMonth = screenHimekuriDate % 100;
        }
        setDateTexts(screenYear, screenMonth, screenDateOfMonth);

        diary = searchFromDiary(screenHimekuriDate);
        diaryText.setText(diary);
    }

    public int searchRecordAndGetLastDate(){

        Cursor cursor = null;
        int resultDate = 0;

        try{
            cursor = database.query(MySQLiteOpenHelper.HimekuriRecordTable, new String[]{MySQLiteOpenHelper.DB_Record_himekuriDate}, null, null, null, null, null);
            int indexHimekuri_date = cursor.getColumnIndex(MySQLiteOpenHelper.DB_Record_himekuriDate);

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

    public String searchFromDiary(int date){

        Cursor cursor = null;
        String result = "";

        try{
            cursor = database.query(MySQLiteOpenHelper.DiaryTable, new String[]{MySQLiteOpenHelper.DB_Diary_diary}, MySQLiteOpenHelper.DB_Diary_date + " = ?", new String[]{String.valueOf(date)}, null, null, null);
            int indexDiary = cursor.getColumnIndex(MySQLiteOpenHelper.DB_Diary_diary);

            while(cursor.moveToNext()){
                result = cursor.getString(indexDiary);
            }
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }

        return result;
    }

    public void setDateTexts(int year, int month, int dateOfMonth){
        monthText.setText(year + ". " + (month + 1));
        dateText.setText(String.valueOf(dateOfMonth));
        setMaxDateOfMonth(year, month, dateOfMonth);
    }

    public void setMaxDateOfMonth(int year, int month, int date){
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        screenMaxDateOfMonth = calendar.getActualMaximum(Calendar.DATE);
    }

    public void setHimekuriDate(int year, int month, int dateOfMonth){
        screenHimekuriDate = dateOfMonth + month * 100 + year * 10000;
    }

    public void saveDiary(String diaryText){

        ContentValues values = new ContentValues();
        values.put(MySQLiteOpenHelper.DB_Diary_date, screenHimekuriDate);
        values.put(MySQLiteOpenHelper.DB_Diary_diary, diaryText);
        database.insert(MySQLiteOpenHelper.DiaryTable, null, values);
    }

    public void editDiary(){

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.edit_diary_dialog_layout, null);

        diaryEditText = (EditText)layout.findViewById(R.id.diaryEditText);
        Button saveButton = (Button)layout.findViewById(R.id.saveDiaryButton);
        diaryEditText.setText(searchFromDiary(screenHimekuriDate));

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        View.OnClickListener listener = new View.OnClickListener(){
            public void onClick(View view){

                String text = null;
                SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder)diaryEditText.getText();
                if(spannableStringBuilder == null){
                    text = null;
                }else{
                    text = spannableStringBuilder.toString();
                }
                saveDiary(text);
                diaryText.setText(searchFromDiary(screenHimekuriDate));
                alertDialog.dismiss();
            }
        };

        layout.findViewById(R.id.saveDiaryButton).setOnClickListener(listener);

        alertDialog.setView(layout);
        alertDialog.show();
    }

    public void startSettingActivity(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void startListActivity(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ListActivity.class);
        intent.putExtra(Intent_todayDate, todayDate);
        startActivity(intent);
    }

    public void addDate(View view){
        screenDateOfMonth++;
        if(screenDateOfMonth == screenMaxDateOfMonth + 1){
            screenMonth++;
            screenDateOfMonth = 1;
            if (screenMonth == 12){
                screenYear++;
                screenMonth = 1;
                screenDateOfMonth = 1;
            }
        }
        setHimekuriDate(screenYear, screenMonth, screenDateOfMonth);
        setDateTexts(screenYear, screenMonth, screenDateOfMonth);
        saveHimekuri(screenHimekuriDate);
        diaryText.setText(searchFromDiary(screenHimekuriDate));
    }

    public void decreaseDate(View view){
        screenDateOfMonth--;
        if(screenDateOfMonth == 0){
            screenMonth--;
            setMaxDateOfMonth(screenYear, screenMonth, 1);
            screenDateOfMonth = screenMaxDateOfMonth;
            if(screenMonth == 1){
                screenYear--;
                screenMonth = 12;
                screenDateOfMonth = 31;
            }
        }
        setHimekuriDate(screenYear, screenMonth, screenDateOfMonth);
        setDateTexts(screenYear, screenMonth, screenDateOfMonth);
        saveHimekuri(screenHimekuriDate);
        diaryText.setText(searchFromDiary(screenHimekuriDate));
    }

    public void setTodayDate(View view){
        //今日の日付を表示
        screenYear = todayYear;
        screenMonth = todayMonth;
        screenDateOfMonth = todayDateOfMonth;
        //今日の日付を保存
        if(searchRecordAndGetLastDate() != todayDate){
            saveHimekuri(todayDate);
        }
        setDateTexts(screenYear, screenMonth, screenDateOfMonth);
    }

    public void menu(View view){

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.menu_dialog_layout, null);

//        ImageView settingImage = (ImageView)layout.findViewById(R.id.settingImage);
//        ImageView listImage = (ImageView)layout.findViewById(R.id.listImage);



        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = v.getId();
                if(id == R.id.settingImage){
                    startSettingActivity();
                }else if(id == R.id.listImage){
                    startListActivity();
                }
            }
        };

        layout.findViewById(R.id.settingImage).setOnClickListener(listener);
        layout.findViewById(R.id.listImage).setOnClickListener(listener);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("メニュー");
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.show();
    }

    public void edit(View view){
        editDiary();
    }
}
