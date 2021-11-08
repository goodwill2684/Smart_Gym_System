package com.jicode.smartgymsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jicode.smartgymsystem.Database.Todo;
import com.jicode.smartgymsystem.R;
import com.jicode.smartgymsystem.VO.DataList;

import java.util.ArrayList;
import java.util.List;

public class DataListAdapter2 extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Todo> data_list;


    public DataListAdapter2(Context context, ArrayList<Todo> data) {
        mContext = context;
        data_list = data;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return data_list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Todo getItem(int position) {
        return data_list.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.list_item_datalist, null);

        TextView title = view.findViewById(R.id.title);
        TextView value = view.findViewById(R.id.value);
        TextView count = view.findViewById(R.id.count);
        TextView id = view.findViewById(R.id.id);

        title.setText(data_list.get(position).getTitle());
        value.setText(data_list.get(position).getValue());
        count.setText(data_list.get(position).getCount());
//        id.setText(data_list.get(position).getId());

        return view;

    }
}

