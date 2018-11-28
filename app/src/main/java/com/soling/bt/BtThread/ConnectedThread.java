package com.soling.bt.BtThread;

import android.bluetooth.BluetoothSocket;


import com.soling.bt.common.ChatConstant;
import com.soling.bt.helper.BluetoothChatHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {

    private final BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;

    public ConnectedThread(BluetoothChatHelper bluetoothChatHelper, BluetoothSocket socket, String socketType) {
       mHelper = bluetoothChatHelper;
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        int bytes;
        byte[] buffer = new byte[1024];

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                bytes = mInStream.read(buffer);
                byte[] data = new byte[bytes];
                System.arraycopy(buffer, 0, data, 0, data.length);
                mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_READ, bytes, -1, data).sendToTarget();
            } catch (IOException e) {
                mHelper.start(false);
                break;
            }
        }
    }

    public void write(byte[] buffer) {
        if(mSocket.isConnected()){
            try {
                mOutStream.write(buffer);
                mHelper.getHandler().obtainMessage(ChatConstant.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
            }
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }
}