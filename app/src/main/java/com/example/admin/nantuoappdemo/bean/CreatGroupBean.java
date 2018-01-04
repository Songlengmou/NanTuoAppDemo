package com.example.admin.nantuoappdemo.bean;

import com.hyphenate.chat.EMGroupOptions;

import java.io.Serializable;

/**
 * Created by admin on 2017/12/12.
 * <p>
 * 创建群组的信息类
 */

public class CreatGroupBean implements Serializable {
    private String groupName;// 群组名称
    private String desc;// 群组简介
    private String[] allMembers; // 群组初始成员，如果只有自己传空数组即可
    private String reason;// 邀请成员加入的reason
    private EMGroupOptions option;// 群组类型选项，可以设置群组最大用户数(默认200)及群组类型

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String[] getAllMembers() {
        return allMembers;
    }

    public void setAllMembers(String[] allMembers) {
        this.allMembers = allMembers;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public EMGroupOptions getOption() {
        return option;
    }

    public void setOption(EMGroupOptions option) {
        this.option = option;
    }
}
