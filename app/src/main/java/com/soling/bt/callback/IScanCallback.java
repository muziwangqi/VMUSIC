package com.soling.bt.callback;

import java.util.List;
/*
扫描回调
 */
public interface IScanCallback<T> {
    void discoverDevice(T t);
    void scanTimeout();
    void scanFinish(List<T> list);
}
