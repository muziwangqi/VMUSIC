package com.soling.bt.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.soling.bt.BtThread.AcceptThread;
import com.soling.bt.BtThread.ConnectThread;
import com.soling.bt.BtThread.ConnectedThread;
import com.soling.bt.callback.IChatCallback;
import com.soling.bt.common.ChatConstant;
import com.soling.bt.common.State;

/*
蓝牙消息处理帮助类
 */
public class BluetoothChatHelper {
    private final BluetoothAdapter bluetoothAdapter;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private State state;
    private IChatCallback<byte[]> chatCallback;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.obj == null) {
                return;
            }
            switch (msg.what) {
                case ChatConstant.MESSAGE_STATE_CHANGE:
                    if (chatCallback != null) {
                        chatCallback.connectStateChange((State) msg.obj);
                    }
                    break;
                case ChatConstant.MESSAGE_WRITE:
                    if (chatCallback != null) {
                        chatCallback.writeData((byte[]) msg.obj, 0);
                    }
                    break;
                case ChatConstant.MESSAGE_READ:
                    if (chatCallback != null) {
                        chatCallback.readData((byte[]) msg.obj, 0);
                    }
                    break;
                case ChatConstant.MESSAGE_DEVICE_NAME:
                    if (chatCallback != null) {
                        chatCallback.setDeviceName((String) msg.obj);
                    }
                    break;
                case ChatConstant.MESSAGE_TOAST:
                    if (chatCallback != null) {
                        chatCallback.showMessage((String) msg.obj, 0);
                    }
                    break;
            }
        }
    };

    public BluetoothChatHelper(IChatCallback<byte[]> chatCallback) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = State.STATE_NONE;
        this.chatCallback = chatCallback;
    }

    public synchronized State getState() {
        return state;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public synchronized BluetoothChatHelper setState(State state) {
        this.state = state;
        mHandler.obtainMessage(ChatConstant.MESSAGE_STATE_CHANGE, -1, -1, state).sendToTarget();
        return this;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public BluetoothChatHelper setConnectThread(ConnectThread connectThread) {
        this.connectThread = connectThread;
        return this;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public BluetoothChatHelper setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
        return this;
    }

    public synchronized void start(boolean secure) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(State.STATE_LISTEN);
        if (acceptThread == null) {
            acceptThread = new AcceptThread(this, secure);
            acceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (state == State.STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        connectThread = new ConnectThread(this, device, secure);
        connectThread.start();
        setState(State.STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice, String socketType) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        connectedThread = new ConnectedThread(this, bluetoothSocket, socketType);
        connectedThread.start();
        mHandler.obtainMessage(ChatConstant.MESSAGE_DEVICE_NAME, -1, -1, bluetoothDevice.getName()).sendToTarget();
        setState(State.STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(State.STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (state != State.STATE_CONNECTED) {
                return;
            }
            r = connectedThread;
            r.write(out);
        }
    }

    public void connectionFailed() {
        mHandler.obtainMessage(ChatConstant.MESSAGE_TOAST, -1, -1, "Unable to connect device").sendToTarget();
        this.start(false);
    }

    public void connectionLost() {
        mHandler.obtainMessage(ChatConstant.MESSAGE_TOAST, -1, -1, "Device connection was lost").sendToTarget();
        this.start(false);
    }
}
