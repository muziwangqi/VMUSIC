package com.soling.bt.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class GroupInfo implements Parcelable {
    private int groupId;
    private int onLineNumber;//在线人数
    private String groupName;//组名
    private List<FriendInfo> friendInfoList;//该组好友列表
    public GroupInfo(){}
    protected GroupInfo(Parcel in) {
        groupId = in.readInt();
        onLineNumber = in.readInt();
        groupName = in.readString();
        friendInfoList = in.readArrayList(FriendInfo.class.getClassLoader());

    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    public int getGroupId() {
        return groupId;
    }

    public GroupInfo setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public int getOnLineNumber() {
        return onLineNumber;
    }

    public GroupInfo setOnLineNumber(int onLineNumber) {
        this.onLineNumber = onLineNumber;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupInfo setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public List<FriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public GroupInfo setFriendInfoList(List<FriendInfo> friendInfoList) {
        this.friendInfoList = friendInfoList;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupId);
        dest.writeInt(onLineNumber);
        dest.writeString(groupName);
        dest.writeTypedList(friendInfoList);
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "groupId=" + groupId +
                ",onLineNumber=" + onLineNumber +
                ",groupName='" + groupName + '\'' +
                ",friendInfoList=" + friendInfoList +
                '}';
    }
}
