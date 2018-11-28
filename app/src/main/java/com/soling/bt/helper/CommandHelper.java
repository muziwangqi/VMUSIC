package com.soling.bt.helper;

import android.provider.MediaStore;

import com.soling.bt.Util.CRCUtil;
import com.soling.bt.Util.ConvertUtil;
import com.soling.bt.assemble.ViseAssemble;
import com.soling.bt.common.ChatConstant;
import com.soling.bt.model.BaseMessage;
import com.soling.bt.model.FileMessage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/*
蓝牙针对消息收发命令处理帮助类
 */
public class CommandHelper {
    public static byte[] packMsg(String msg) throws UnsupportedEncodingException {
        if (msg == null || msg.isEmpty()) {
            return null;
        }
        return new ViseAssemble.Builder()
                .setStartFlag(ChatConstant.VISE_COMMAND_TYPE_TEXT)
                .setData(msg.getBytes("UTF-8"))
                .assembleCommand();
    }

    public static byte[] packFile(File file) throws UnsupportedEncodingException {
        if (file == null || file.getName() == null || file.getName().isEmpty()) {
            return null;
        }
        byte[] fNameBytes = file.getName().getBytes("UTF-8");
        ByteBuffer byteBuffer = ByteBuffer.allocate(fNameBytes.length + 6);
        byteBuffer.put(ConvertUtil.intToBytesHigh((int) file.length(), 4));
        byteBuffer.put(ConvertUtil.intToBytesHigh(fNameBytes.length, 2));
        byteBuffer.put(fNameBytes);
        return new ViseAssemble.Builder()
                .setStartFlag(ChatConstant.VISE_COMMAND_TYPE_FILE)
                .setData(byteBuffer.array())
                .assembleCommand();
    }

    public static BaseMessage unpackData(byte[] data) {
        BaseMessage message = null;
        if (data == null) {
            return null;
        }
        if (isRightCommand(data)) {
            if(data[4] == ChatConstant.VISE_COMMAND_TYPE_TEXT){
                message = new BaseMessage();
                int dataLength = ConvertUtil.bytesToIntHigh(new byte[]{data[1],data[2]},0);
                byte[] msgData = new byte[dataLength];
                System.arraycopy(data,5,msgData,0,dataLength);
                if(msgData.length<1){
                    return message;
                }
                message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_TEXT);
                message.setMsgLength(dataLength);
                message.setMsgContent(new String(msgData));
            }else if(data[4] == ChatConstant.VISE_COMMAND_TYPE_FILE){
                message = new FileMessage();
                int dataLength = ConvertUtil.bytesToIntHigh(new byte[]{data[1],data[2]},0);
                byte[] fileInfoData = new byte[dataLength];
                System.arraycopy(data,5,fileInfoData,0,dataLength);
                if(fileInfoData.length<7){
                    return message;
                }
                byte[] fileLength = new byte[]{fileInfoData[0],fileInfoData[1],fileInfoData[2],fileInfoData[3]};
                byte[] fileNameLength = new byte[]{fileInfoData[4],fileInfoData[5]};
                byte[] fileName = new byte[fileInfoData.length-6];
                System.arraycopy(fileInfoData,6,fileName,0,fileName.length);
                message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_FILE);
                ((FileMessage) message).setFileLength(ConvertUtil.bytesToIntHigh(fileLength,4));
                ((FileMessage) message).setFileName(new String(fileName));
                ((FileMessage) message).setFileLength(ConvertUtil.bytesToIntHigh(fileNameLength,2));
            }else{
                message = new BaseMessage();
                message.setMsgType(ChatConstant.VISE_COMMAND_TYPE_NONE);
                message.setMsgLength(0);
                message.setMsgContent("");
            }
        }
        return  message;
    }



    private static boolean isRightCommand(byte[] data) {
        if(data == null || data.length<6){
            return false;
        }
        byte[] lengthData = new byte[]{data[1],data[2]};
        byte[] lengthCurrent = ConvertUtil.intToBytesHigh(data.length-6,2);
        byte[] checkData = new byte[data.length-2];
        System.arraycopy(data,1,checkData,0,data.length-2);
        byte checkCode = CRCUtil.calcCrc8(checkData);
        return (data[0] == (byte) 0xFF) && (Arrays.equals(lengthData, lengthCurrent)) && (data[data.length-1] == checkCode);
    }
}
