package com.example.admin.nantuoappdemo.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.social.AddFriendActivity;
import com.example.admin.nantuoappdemo.activity.social.CreateGroupActivity;
import com.example.admin.nantuoappdemo.othermanager.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 好友列表页
 */

public class LinkManFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.tv_new_friend)
    TextView tv_new_friend;
    @Bind(R.id.tv_group_chat)
    TextView tv_group_chat;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_link_man;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_new_friend, R.id.tv_group_chat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new_friend:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.tv_group_chat:
                startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                break;
        }
    }
}
