package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.nantuoappdemo.R;
import com.hyphenate.chat.EMGroup;

import java.util.List;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 群聊适配器
 */

public class CreateGroupAdapter extends BaseAdapter {
    private Context context;
    private List<EMGroup> list;

    // 构造方法 接收 上下文 和 数据源
    public CreateGroupAdapter(Context context, List<EMGroup> list) {
        this.context = context;
        this.list = list;
    }

    //--------------------------------------------------------------
    //生命周期
    public void refAlls(List<EMGroup> list) {
        this.list = list;
        this.notifyDataSetChanged();
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
// 声明内部类
        ViewHolder vh;
        // 防止重复,避免资源浪费
        // 判断View是否为空
        if (convertView == null) {
            // 实例化内部类
            vh = new ViewHolder();
            // 加载布局给View
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_creat_adapter, parent, false);
            // 初始化控件
            vh.getViews(convertView);
            // 把内部类的对象set给View 当下次View不等于null时,
            // 可以直接取得已经初始化好的控件
            convertView.setTag(vh);
        } else {
            // 获取之前set的初始化完成的控件对象
            vh = (ViewHolder) convertView.getTag();
        }
        // 获取当前item的数据(当前是position)
        EMGroup group = (EMGroup) getItem(position);
        // 给控件设置数据
        vh.groupName.setText(group.getGroupName());
        vh.groupContent.setText("");

        // 把View return回去
        return convertView;
    }

    // 存放控件对象的内部类
    private class ViewHolder {
        private ImageView img;
        private TextView groupName, groupContent;

        // 初始化控件的方法
        void getViews(View view) {
            img = view.findViewById(R.id.item_group_img);
            groupName = view.findViewById(R.id.item_group_name);
            groupContent = view.findViewById(R.id.item_group_content);

        }
    }
}
