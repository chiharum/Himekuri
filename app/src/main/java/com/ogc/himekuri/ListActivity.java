package com.ogc.himekuri;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView diaryList;

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    int todayDate, todayYear, todayMonth, todayDateOfMonth, screenYear, screenMonth, screenDate, screenDaysOfTheMonth;
    List<DiaryListItem> items;
    DiaryListCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // TODO: 2016/12/11 ListViewに全ての日記を表示(最初に1ヶ月分表示→下についたら次の1ヶ月分を読み込む)

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        database = mySQLiteOpenHelper.getWritableDatabase();

        todayDate = getIntent().getIntExtra(MainActivity.Intent_todayDate, 0);
        if(todayDate == 0){
            errorDialog();
        }else{
            todayYear = todayDate / 10000;
            todayMonth = todayDate / 100 - todayYear * 100 + 1;
            todayDateOfMonth = todayDate % 100;
        }
        screenDate = todayDate;
        screenYear = todayYear;
        screenMonth = todayMonth;
        screenDaysOfTheMonth = getDaysOfTheMonth(screenYear, screenMonth, screenDate);

        diaryList = (ListView)findViewById(R.id.listView);

        //StackOverFlowでます！

        setDiaryList(screenYear, screenMonth);
    }

    public void setDiaryList(int year, int month){

        int dateOfMonth = 1;
        items = new ArrayList<>();

        while(dateOfMonth <= screenDaysOfTheMonth){
            int date = makeDate(year, month, dateOfMonth);
            DiaryListItem item = new DiaryListItem(date, searchDiary(date));
            items.add(item);
            dateOfMonth++;
        }

        adapter = new DiaryListCustomAdapter(this, R.layout.diary_list_layout, items);
        diaryList.setAdapter(adapter);

//        diaryList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if (firstVisibleItem + visibleItemCount > totalItemCount) {
//                    screenYear = getNextMonthsDate(screenYear, screenMonth)[0];
//                    screenMonth = getNextMonthsDate(screenYear, screenMonth)[1];
//                    setDiaryList(screenYear, screenMonth);
//                } else if (firstVisibleItem == 0) {
//                    screenYear = getPreviousMonthsDate(screenYear, screenMonth)[0];
//                    screenMonth = getPreviousMonthsDate(screenYear, screenMonth)[1];
//                    setDiaryList(screenYear, screenMonth);
//                }
//                screenDate = makeDate(screenYear, screenMonth, 1);
//            }
//        });
    }

    public String searchDiary(int date){

        Cursor cursor = null;
        String result = "";

        try{
            cursor = database.query(MySQLiteOpenHelper.DiaryTable, new String[]{MySQLiteOpenHelper.DB_Diary_diary}, MySQLiteOpenHelper.DB_Diary_date + " = ?", new String[]{String.valueOf(date)}, null, null, null);
            int indexDiary = cursor.getColumnIndex(MySQLiteOpenHelper.DB_Diary_diary);

            while(cursor.moveToNext()){
                result = cursor.getString(indexDiary);
            }
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return result;
    }

    public int getDaysOfTheMonth(int year, int month, int date){
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public int[] getNextMonthsDate(int year, int month){

        if(month == 11){
            month = 0;
            year++;
        }else{
            month++;
        }

        int[] date = new int[2];
        date[0] = year;
        date[1] = month;
        return date;
    }

    public int[] getPreviousMonthsDate(int year, int month){

        if (month == 0){
            month = 11;
            year--;
        }else{
            month--;
        }
        int[] date = new int[2];
        date[0] = year;
        date[1] = month;
        return date;
    }

    public int makeDate(int year, int month, int dateOfMonth){
        return dateOfMonth + month * 100 + year * 10000;
    }

    public void errorDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage("今日の日付が取得できませんでした。もう一度お試しください。");
        alertDialog.show();
    }
}
