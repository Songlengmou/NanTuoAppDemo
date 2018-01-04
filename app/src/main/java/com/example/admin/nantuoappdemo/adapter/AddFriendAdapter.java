package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.nantuoappdemo.R;

import java.util.List;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 好友添加适配器
 */

public class AddFriendAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public AddFriendAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_addfriend_adapter, parent, false);
            myHolder = new MyHolder();
            myHolder.name = convertView.findViewById(R.id.item_addFriend_name);
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }

        myHolder.name.setText(list.get(position));
        return convertView;
    }

    public void upData(List<String> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    class MyHolder {
        TextView name;
    }
}
