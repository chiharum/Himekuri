package com.ogc.himekuri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class DiaryListCustomAdapter extends ArrayAdapter<DiaryListItem> {

    LayoutInflater layoutInflater;

    public DiaryListCustomAdapter(Context context, int resource, List<DiaryListItem> items){
        super(context, resource, items);
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.diary_list_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        DiaryListItem item = getItem(position);

        if(item != null){
            viewHolder.diaryListDateText.setText(String.valueOf(item.date));
            viewHolder.diaryListContentText.setText(item.content);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView diaryListDateText;
        TextView diaryListContentText;
        public ViewHolder(View view){
            diaryListDateText = (TextView)view.findViewById(R.id.diaryListDateText);
            diaryListContentText = (TextView)view.findViewById(R.id.diaryListContentText);
        }
    }
}
