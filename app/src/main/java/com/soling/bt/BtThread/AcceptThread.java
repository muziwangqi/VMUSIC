package com.soling.bt.BtThread;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.soling.bt.common.ChatConstant;
import com.soling.bt.common.State;
import com.soling.bt.helper.BluetoothChatHelper;
import com.soling.service.player.BluetoothChatService;

import java.io.IOException;

public class AcceptThread extends Thread {

    private BluetoothChatHelper mHelper;
    private final BluetoothServerSocket mServerSocket;
    private String mSocketType;

    public AcceptThread(BluetoothChatHelper bluetoothChatHelper, boolean secure) {
        mHelper = bluetoothChatHelper;
        BluetoothServerSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        try {
            if (secure) {
                tmp = mHelper.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(ChatConstant.NAME_SECURE, ChatConstant.UUID_SECURE);
            } else {
                tmp = mHelper.getBluetoothAdapter().listenUsingInsecureRfcommWithServiceRecord(ChatConstant.NAME_INSECURE, ChatConstant.UUID_INSECURE);
            }
        } catch (IOException e) {
        }
        mServerSocket = tmp;
    }

    public void run() {
        setName("AcceptThread" + mSocketType);

        BluetoothSocket socket = null;

        while (mHelper.getState() != com.soling.bt.common.State.STATE_CONNECTED) {
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            if (socket != null) {
                synchronized (this) {
                    if(mHelper.getState() == com.soling.bt.common.State.STATE_LISTEN
                            || mHelper.getState() == com.soling.bt.common.State.STATE_CONNECTING){
                        mHelper.connected(socket, socket.getRemoteDevice(), mSocketType);
                    } else if(mHelper.getState() == com.soling.bt.common.State.STATE_NONE
                            || mHelper.getState() == com.soling.bt.common.State.STATE_CONNECTED){
                        try {
                            socket.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }

    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
        }
    }
}
