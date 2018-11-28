package com.soling.bt.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FriendInfo implements Parcelable{
    private int friendId;
    private String friendNickName;
    private String friendIconUri;
    private String deviceAddress;
    private String identificationName;
    private String joinTime;
    private boolean isOnline;
    private Parcelable bluetoothDevice;
    public FriendInfo(){}
    protected FriendInfo(Parcel in) {
        friendId = in.readInt();
        friendNickName = in.readString();
        friendIconUri = in.readString();
        deviceAddress = in.readString();
        identificationName = in.readString();
        joinTime = in.readString();
        isOnline = in.readByte()!=0;
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }


    public static final Creator<FriendInfo> CREATOR = new Creator<FriendInfo>() {
        @Override
        public FriendInfo createFromParcel(Parcel in) {
            return new FriendInfo(in);
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };

    public int getFriendId() {
        return friendId;
    }

    public FriendInfo setFriendId(int friendId) {
        this.friendId = friendId;
        return this;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public FriendInfo setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
        return this;
    }

    public String getFriendIconUri() {
        return friendIconUri;
    }

    public FriendInfo setFriendIconUri(String friendIconUri) {
        this.friendIconUri = friendIconUri;
        return this;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public FriendInfo setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }

    public Parcelable getBluetoothDevice() {
        return bluetoothDevice;
    }

    public FriendInfo setBluetoothDevice(Parcelable bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        return this;
    }

    public String getIdentificationName() {
        return identificationName;
    }

    public FriendInfo setIdentificationName(String identificationName) {
        this.identificationName = identificationName;
        return this;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public FriendInfo setJoinTime(String joinTime) {
        this.joinTime = joinTime;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public FriendInfo setOnline(boolean online) {
        isOnline = online;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(friendId);
        dest.writeString(friendNickName);
        dest.writeString(friendIconUri);
        dest.writeString(deviceAddress);
        dest.writeString(identificationName);
        dest.writeString(joinTime);
        dest.writeByte((byte) (isOnline? 1:0));
        dest.writeParcelable(bluetoothDevice,flags);
    }

   @Override
    public String toString() {
        return "FriendInfo{"+
                "friendId="+friendId+
                ",friendNickName='"+friendNickName+'\''+
                ",friendIconUri='"+friendIconUri+'\''+
                ",deviceAddress='"+deviceAddress+'\''+
                ",identificationName='"+identificationName+'\''+
                ",joinTime='"+joinTime+'\''+
                ",isOnline='"+isOnline+'\''+
                ",bluetoothDevice"+bluetoothDevice+
                "}";
    }
}
