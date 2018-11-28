package com.soling.bt.assemble;
/*
基础组装接口
 */
public interface IBaseAssemble {
    //设置起始标志
    void setStartFlag(byte startFlag);
    //设置数据
    void setData(byte[] data);
    //设置校验码
    void setCheckCode(byte checkCode);
    //组装命令
    byte[] assembleCommand();
}
