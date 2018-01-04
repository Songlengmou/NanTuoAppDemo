package com.example.admin.nantuoappdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.social.PrivateMessageActivity;
import com.example.admin.nantuoappdemo.adapter.PrivateImageSelectAdapter;
import com.example.admin.nantuoappdemo.utils.FuilUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by admin on 2017/12/11.
 * <p>
 * 个人聊天详情页 下的 点击发送图片 的Fragment
 */

public class PrivateImageSelectFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Button send;
    private ArrayList<String> list = new ArrayList<>();
    PrivateImageSelectAdapter privateImageSelectAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.private_fragment_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = FuilUtils.getAllImg(getActivity());
        initView(view);

    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.fragment_imageselect_list);
        send = view.findViewById(R.id.fragment_imageselect_send_btn);

        privateImageSelectAdapter = new PrivateImageSelectAdapter(getActivity(), list);

        //线性布局管理器  设置 水平滚动
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(privateImageSelectAdapter);

        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送图片按钮 点击事件
            case R.id.fragment_imageselect_send_btn:
                //获取选中的所有图片途径
                HashSet<String> checkList = privateImageSelectAdapter.getCheckList();
                //获取容器activity对象
                PrivateMessageActivity pma = (PrivateMessageActivity) getActivity();

                for (String str :
                        checkList) {
                    Log.e("checkList", str);
                    //调用activity中的发送图片方法
                    pma.sendImage(str, false);
                }
                break;
        }
    }
}
