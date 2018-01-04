package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.admin.nantuoappdemo.R;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 个人聊天详情页 下的 点击发送图片 的fragment的适配器(选择图片  复选框)
 */

public class PrivateImageSelectAdapter extends RecyclerView.Adapter<PrivateImageSelectAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> list;
    //存储被选中的图片
    private HashSet<String> checkList = new HashSet<>();

    public PrivateImageSelectAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_image_select_img);
            checkBox = itemView.findViewById(R.id.item_image_select_check);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_privat_image_select, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String path = list.get(position);   //图片路径

        //设置图片
        Glide.with(context).load(path)
                .into(holder.img);
        //设置复选框监听
        //把选中的存入集合中
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    checkList.add(path);
                } else {
                    checkList.remove(path);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //返回被选中的图片集合
    public HashSet<String> getCheckList() {
        return checkList;
    }

}
