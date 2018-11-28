package com.soling.bt.assemble;

import com.soling.bt.Util.CRCUtil;
import com.soling.bt.Util.ConvertUtil;
import com.soling.bt.common.ChatConstant;

import java.nio.ByteBuffer;

/*
针对消息发送的命令组装
 */
public class ViseAssemble extends BaseAssemble implements IViseAssemble {
    private byte startFlag = ChatConstant.VISE_COMMAND_START_FLAG;
    private byte[] dataLength;
    private byte protocolVersion = ChatConstant.VISE_COMMAND_PROTOCOL_VERSION;
    private byte commandType;
    @Override
    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }

    @Override
    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public void setCommandType(byte commandType) {
        this.commandType = commandType;
    }

    @Override
    public byte[] assembleCommand() {
        int length = 0;
        if(data!=null){
            length = 6+data.length;
            dataLength = ConvertUtil.intToBytesHigh(data.length,2);

        }else{
            length = 6;
            dataLength = ConvertUtil.intToBytesHigh(0,2);
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(startFlag);
        buffer.put(dataLength);
        buffer.put(protocolVersion);
        buffer.put(commandType);
        if(data!=null){
            buffer.put(data);
        }else{
            buffer.put(new byte[0]);
        }
        byte[] checkData = new byte[length - 2];
        System.arraycopy(buffer.array(),1,checkData,0,length-2);
        checkCode = CRCUtil.calcCrc8(checkData);
        buffer.put(checkCode);
        return buffer.array();
    }

    public static class Builder{
        private IViseAssemble viseAssemble;
        public Builder(){
            this.viseAssemble = new ViseAssemble();
        }
        public Builder setBaseAssemble(IViseAssemble viseAssemble) {
            this.viseAssemble = viseAssemble;
            return this;
        }

        public Builder setStartFlag(byte startFlag){
            this.viseAssemble.setStartFlag(startFlag);
            return this;
        }

        public Builder setData(byte[] data){
            this.viseAssemble.setData(data);
            return this;
        }

        public Builder setCheckCode(byte checkCode){
            this.viseAssemble.setCheckCode(checkCode);
            return this;
        }

        public byte[] assembleCommand(){
            return this.viseAssemble.assembleCommand();
        }
    }
    }

