package com.soling.bt.assemble;

import java.nio.ByteBuffer;

/*
基础命令组装
 */
public class BaseAssemble implements IBaseAssemble {
    protected byte startFlag;
    protected byte[] data;
    protected  byte checkCode;

    @Override
    public void setStartFlag(byte startFlag) {
        this.startFlag = startFlag;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void setCheckCode(byte checkCode) {
        this.checkCode = checkCode;
    }

    @Override
    public byte[] assembleCommand() {
        int length = 0;
        if(data!=null){
            length = 2+data.length;
        }else{
            length = 2;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(startFlag);
        if(data!=null){
            buffer.put(data);
        }else{
            buffer.put(new byte[0]);
        }
        buffer.put(checkCode);
        return buffer.array();
    }

    public static class Builder{
        private IBaseAssemble baseAssemble;
        public Builder(IBaseAssemble baseAssemble){
            this.baseAssemble = baseAssemble;
        }

        public Builder setBaseAssemble(IBaseAssemble baseAssemble) {
            this.baseAssemble = baseAssemble;
            return this;
        }

        public Builder setStartFlag(byte startFlag){
            this.baseAssemble.setStartFlag(startFlag);
            return this;
        }

        public Builder setData(byte[] data){
            this.baseAssemble.setData(data);
            return this;
        }

        public Builder setCheckCode(byte checkCode){
            this.baseAssemble.setCheckCode(checkCode);
            return this;
        }

        public byte[] assemble(){
            return this.baseAssemble.assembleCommand();
        }
    }
}
