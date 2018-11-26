package com.soling.view.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.soling.R;
import com.soling.view.adapter.BluetoothDeviceListAdapter;
import com.soling.view.adapter.WiperSwitch;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends BaseActivity {

    private static final String TAG = BluetoothActivity.class.getSimpleName();
    private TextView tv_bluetooth_name, tv_bluetooth_address, tv_havamatched, tv_connectdevices;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private static int code = 1;
    //    public static final String UUID_CONTENT = "00001101-0000-1000-8000-00805F9B34FB";
    private WiperSwitch ws_bttop;
    private ListView lv_havamatched, lv_connectdevices;
    private BluetoothDeviceListAdapter havamatchedAdapter, connectdevicesAdapter;
    private ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bluetooth2);
        initView();
        initDAta();
        //加载适配器
        lv_havamatched.setAdapter(havamatchedAdapter);
        lv_connectdevices.setAdapter(connectdevicesAdapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initListener();
    }

    private void initDAta() {
        havamatchedAdapter = new BluetoothDeviceListAdapter(this);
        connectdevicesAdapter = new BluetoothDeviceListAdapter(this);
    }

    private void initView() {
        tv_bluetooth_name = findViewById(R.id.tv_bluetooth_name);
        tv_bluetooth_address = findViewById(R.id.tv_bluetooth_address);
        tv_havamatched = findViewById(R.id.tv_havamatched);
        tv_connectdevices = findViewById(R.id.tv_connectdevices);
        ws_bttop = findViewById(R.id.ws_bttop);
        lv_havamatched = findViewById(R.id.lv_havamatched);
        lv_connectdevices = findViewById(R.id.lv_connectdevices);
    }

    private void initListener() {
        ws_bttop.setChecked(false);
        //open search show close
        ws_bttop.setOnChangedListener(new WiperSwitch.IOnChangedListener() {
            @Override
            public void onChange(WiperSwitch wiperSwitch, boolean checkStat) {
//                shortToast("lllllllllllllllllllllllllllllllll");
                if (checkStat) {
                    if (bluetoothAdapter == null) {
                        shortToast("该设备不支持蓝牙");
                        finish();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, code);
                        if (bluetoothAdapter!=null){
                            bluetoothAdapter.startDiscovery();
                        }
                        tv_bluetooth_name.setText(bluetoothAdapter.getName());
                        tv_bluetooth_address.setText(bluetoothAdapter.getAddress());

                        //获取所有已绑定的蓝牙设备
                        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                        if (devices.size() > 0) {
                            for (BluetoothDevice device : devices) {
                                // scan and add it to adapter
                                havamatchedAdapter.addDevice(device);
                                //update listview
                                havamatchedAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    if (bluetoothAdapter.isEnabled()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothActivity.this);
                        dialog.setMessage("确定关闭蓝牙吗？");
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (bluetoothAdapter != null || bluetoothAdapter.isEnabled()) {
                                    bluetoothAdapter.disable();//close
                                    tv_havamatched.setText("");
                                    tv_connectdevices.setText("");
                                    tv_havamatched.setBackgroundResource(R.color.tv_background);
                                    tv_connectdevices.setBackgroundResource(R.color.tv_background);
                                    havamatchedAdapter.clear();
                                    connectdevicesAdapter.clear();
                                }
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        dialog.show();
                    } else {
                        shortToast("蓝牙未开启，请先打开蓝牙");
                    }
                }
            }
        });

        //register broadcast
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter);

        //已配对的点击事件
        lv_havamatched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = havamatchedAdapter.getDevice(position);
                connectThread = new ConnectThread(device);
                System.out.println("连接中...");
                connectThread.start();
                shortToast("要连接的设备是" + device.getName() + "  " + device.getAddress());
            }
        });

        //可用设备点击事件
        lv_connectdevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = connectdevicesAdapter.getDevice(position);
                Boolean returnValue = false;
                Method createBondMethod;
                try {
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        //反射方法调用
                        createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        System.out.println("开始配对");
                        returnValue = (Boolean) createBondMethod.invoke(device);
                        shortToast("要连接的设备是：" + device.getName() + "   " + device.getAddress());
                    } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        connectThread = new ConnectThread(device);
                        connectThread.start();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //获取已搜索到的设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (address == null) {
//                    address = device.getAddress();
//                }
                //搜索到的非配对的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    connectdevicesAdapter.addDevice(device);
                    connectdevicesAdapter.notifyDataSetChanged();
                }
//                tv_devices.setText(tv_devices.getText() + "\n" + device.getName() + "-->" + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("搜索蓝牙设备");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //利用反射进行客户端连接
    public class ConnectThread extends Thread {

        private final BluetoothDevice mmdevice;
        private final BluetoothSocket mmSocket;
        private Boolean isConnect = false;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            //赋值给设备
            mmdevice = device;
            try {
                Method method = connectedDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) method.invoke(connectedDevice, 1);//这里端口为1
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //赋值给BluetoothSocket
            mmSocket = tmp;
        }

        @Override
        public void run() {
            try {
                System.out.println("连接中...");
                mmSocket.connect();
                isConnect = true;
                shortToast("连接成功");
                System.out.println("连接成功");
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            //在线程中管理连接；
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //使用UUID，客户端进行连接，服务端接收连接
    //客户端
    /*public class ConnectThread extends Thread {

        private BluetoothDevice btDevice;
        private BluetoothSocket btSocket;
        private String s = "00001101-0000-1000-8000-00805F9B34FB";
        private boolean isConnect = false;

        public ConnectThread(BluetoothDevice device) {
            try {
                BluetoothSocket temSocket = null;
                btDevice = device;
                temSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(s));
                btSocket = temSocket;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                btSocket.connect();
                isConnect = true;
                shortToast("连接成功");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }

        public void cancle() {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //服务端
    class ReceiveThread extends Thread {
        private BluetoothServerSocket bluetoothServerSocket;

        public ReceiveThread() {
            BluetoothServerSocket temSocket = null;
            try {
                BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord("蓝牙连接", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothServerSocket = temSocket;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = bluetoothServerSocket.accept();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (socket == null) {
                    try {
                        bluetoothServerSocket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cancle() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
