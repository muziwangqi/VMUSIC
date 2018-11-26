package com.soling.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.soling.R;
import com.soling.service.player.BluetoothChatService;

public class BluetoothChatActivity extends Activity {
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private String mConnectedDeviceName;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) {
            Log.e(TAG, "+++ ON CREATE +++");
        }
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.chat_bluetooth);
        mTitle = findViewById(R.id.bluetooth_name);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            ;
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) {
            Log.e(TAG, "+++ ON START +++");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) {
                setupChat();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (D) {
            Log.e(TAG, "+++ ON RESUME +++");
        }
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat");

        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.chat_get);
        mConversationView = findViewById(R.id.bluetooth_chat_list);
        mConversationView.setAdapter(mConversationArrayAdapter);

        mOutEditText = findViewById(R.id.bluetooth_chat_content);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        mSendButton = findViewById(R.id.bluetooth__bottom_sendbutton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = findViewById(R.id.bluetooth_chat_content);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });

        mChatService = new BluetoothChatService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    protected synchronized void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatService!=null){
            mChatService.stop();
        }
    }

    private void ebsureDiscoverable(){
        if(mBluetoothAdapter.getScanMode()!=BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            return;
        }
    if(message.length()>0){
        byte[] send = message.getBytes();
        mChatService.write(send);

        mOutStringBuffer.setLength(0);
        mOutEditText.setText(mOutStringBuffer);
    }



        }

    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                String message = v.getText().toString();
                sendMessage(message);
            }
            if (D) {
                Log.i(TAG, "END onEditorAction");
            }
            return true;
        }
    };



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) {
                        Log.i(TAG, "MESSAGE_STATE_CHANGE:" + msg.arg1);
                    }
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText("");
                            mTitle.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText("");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText("");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me: " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMressage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ": " + readMressage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to" + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(BluetoothDevicesListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
                    mChatService.connect(bluetoothDevice);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }


}
