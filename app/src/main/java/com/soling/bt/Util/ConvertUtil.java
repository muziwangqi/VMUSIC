package com.soling.bt.Util;
/*
转换工具类
 */
public class ConvertUtil {
/*
int数值转换为byte[]，高位在前
 */
public static byte[] intToBytesHigh(int value,int n){
    byte[] src = new byte[n];
    for(int i=0;i<n;i++){
        src[i] = (byte) ((value>>(8*(n-i-1))) & 0xFF);
    }
    return src;
}

    /**
     * byte数组转换为int，高位在前
     * @param bytes
     * @param offset
     * @return
     */
    public static int bytesToIntHigh(byte[] bytes, int offset){
        int value = 0;
        if(bytes == null || bytes.length == 0){
            return value;
        }
        for(int i = 0; i < bytes.length; i++){
            value += (int) ((bytes[i] & 0xFF) << (8 * (bytes.length - i - 1)));
        }
        return value;
    }
}
