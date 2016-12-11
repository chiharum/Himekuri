package com.ogc.himekuri;

import android.app.AlertDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView diaryList;

    int todayDate;
    List<DiaryListItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // TODO: 2016/12/11 ListViewに全ての日記を表示(最初に1ヶ月分表示→下についたら次の1ヶ月分を読み込む)

        todayDate = getIntent().getIntExtra(MainActivity.Intent_todayDate, 0);

        diaryList = (ListView)findViewById(R.id.listView);
        items = new ArrayList<>();
    }

    public void setDiaryList(){

    }

    public void searchDiary(){

        Cursor cursor = null;

    }

    public void errorDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage("今日の日付が取得できませんでした。もう一度お試しください。");
    }
}
