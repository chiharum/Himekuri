package com.ogc.himekuri;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView diaryList;

    MySQLiteOpenHelper mySQLiteOpenHelper;
    SQLiteDatabase database;

    int todayDate, todayYear, todayMonth, todayDateOfMonth, screenYear, screenMonth, screenDate, screenDaysCountOfTheMonth;
    List<DiaryListItem> items;
    DiaryListCustomAdapter adapter;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // TODO: 2016/12/11 ListViewに全ての日記を表示(最初に1ヶ月分表示→下についたら次の1ヶ月分を読み込む)

        //SQLite設定
        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        database = mySQLiteOpenHelper.getWritableDatabase();

        //今日の日付を取得
        todayDate = getIntent().getIntExtra(MainActivity.Intent_todayDate, 0);
        if(todayDate == 0){
            errorDialog();
        }else{
            todayYear = todayDate / 10000;
            todayMonth = todayDate / 100 - todayYear * 100;
            todayDateOfMonth = todayDate % 100;
        }
        screenDate = todayDate;
        screenYear = todayYear;
        screenMonth = todayMonth;
        screenDaysCountOfTheMonth = getDaysCountOfTheMonth(screenYear, screenMonth, screenDate);

        //ListView設定
        diaryList = (ListView)findViewById(R.id.listView);

        //StackOverFlowでます！

        setDiaryList(screenYear, screenMonth, true);
    }

    public void setDiaryList(final int year, final int month, boolean isFirst){

        int dateOfMonth = 1;
        items = new ArrayList<>();

        while(dateOfMonth <= screenDaysCountOfTheMonth){
            int date = makeDate(year, month, dateOfMonth);
            DiaryListItem item = new DiaryListItem(date, searchDiary(date));
            items.add(item);
            dateOfMonth++;
        }

        adapter = new DiaryListCustomAdapter(getApplicationContext(), R.layout.diary_list_layout, items);

        diaryList.setAdapter(adapter);
        diaryList.addFooterView(getFooterView());

        diaryList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//                Log.i("total-visible==first", String.valueOf(totalItemCount - visibleItemCount == firstVisibleItem));

                if(totalItemCount - visibleItemCount == firstVisibleItem){

                    int dayOfMonth = 1;

                    int[] nextDate = getNextMonthsDate(screenYear, screenMonth);
                    screenYear = nextDate[0];
                    screenMonth = nextDate[1];

//                    items.clear();

                    int itemCount = totalItemCount;
                    for (int i = itemCount; i < itemCount + screenDaysCountOfTheMonth; i++){

                        int date = makeDate(screenYear, screenMonth, dayOfMonth);
                        DiaryListItem item = new DiaryListItem(date, searchDiary(date));
                        items.add(item);
                        dayOfMonth++;
                    }

                    adapter = new DiaryListCustomAdapter(getApplicationContext(), R.layout.diary_list_layout, items);
                }
            }
        });
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

    public int getDaysCountOfTheMonth(int year, int month, int date){
        final Calendar calendar = Calendar.getInstance();

        Log.i("month instance", String.valueOf(calendar.get(Calendar.MONTH)));

        month = 4;

        calendar.set(year, month - 1, date);
        //日数が変

        Log.i("month + days", String.valueOf(month) + " " + String.valueOf(calendar.getActualMaximum(Calendar.DATE)));

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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

    private View getFooterView(){
        if(footerView == null){
            footerView = getLayoutInflater().inflate(R.layout.footer_view, null);
        }
        return footerView;
    }

    public void errorDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage("今日の日付が取得できませんでした。もう一度お試しください。");
        alertDialog.show();
    }
}
