package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.manager.CountenanceMessage;

/**
 * Created by admin on 2017/12/22.
 */

public class CountenanceAdapter extends RecyclerView.Adapter<CountenanceAdapter.MyViewHolder> {
    private Context context;
    private int[] image;

    public CountenanceAdapter(Context context, int[] image) {
        this.context = context;
        this.image = image;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_coun_select_img);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_countenance, parent, false);
        MyViewHolder my = new MyViewHolder(view);
        return my;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.img.setImageResource(image[position]);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置RecycleView的点击事件
                CountenanceMessage.getInstance().getSendListener().setItemOnclistener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return image.length;
    }
}
