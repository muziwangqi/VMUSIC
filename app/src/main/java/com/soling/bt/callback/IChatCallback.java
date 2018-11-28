package com.soling.bt.callback;

import com.soling.bt.common.State;

/*
消息回掉
 */
public interface IChatCallback<T> {
    void connectStateChange(State state);
    void writeData(T data,int type);
    void readData(T data ,int type);
    void setDeviceName(String name);
    void showMessage(String message, int code);
}
