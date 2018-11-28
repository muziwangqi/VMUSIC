package com.soling.bt.BtThread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.soling.bt.common.ChatConstant;
import com.soling.bt.helper.BluetoothChatHelper;
import com.soling.service.player.BluetoothChatService;

import java.io.IOException;

public class ConnectThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private String mSocketType;

    public ConnectThread(BluetoothChatHelper bluetoothChatHelper, BluetoothDevice device, boolean secure) {
        mHelper = bluetoothChatHelper;
        mDevice = device;
        BluetoothSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = device.createRfcommSocketToServiceRecord(ChatConstant.UUID_SECURE);
            } else {
                tmp = device.createInsecureRfcommSocketToServiceRecord(ChatConstant.UUID_INSECURE);
            }
        } catch (IOException e) {

           }
        mSocket = tmp;
    }

    public void run() {
        setName("ConnectThread" + mSocketType);

        mHelper.getBluetoothAdapter().cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e2) {
            }
            mHelper.connectionFailed();
            return;
        }

        synchronized (this) {
            mHelper.setConnectThread(null);
        }

        mHelper.connected(mSocket, mDevice, mSocketType);
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }
}
