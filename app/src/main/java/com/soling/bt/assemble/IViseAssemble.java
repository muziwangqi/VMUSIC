package com.soling.bt.assemble;
/*
针对消息发送的组装接口
 */
public interface IViseAssemble extends IBaseAssemble {
    //设置数据长度
    void setDataLength(byte[] dataLength);
    //设置协议版本
    void setProtocolVersion(byte protocolVersion);
    //设置发送类型
    void setCommandType(byte commandType);
}
