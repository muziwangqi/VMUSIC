package com.soling.view.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.soling.R;
import com.soling.view.adapter.BluetoothDeviceListAdapter;

import android.widget.ListAdapter;

import com.soling.view.adapter.WiperSwitch;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothActivity extends BaseActivity {

    private static final String TAG = BluetoothActivity.class.getSimpleName();
    private TextView tv_bluetooth_name, tv_bluetooth_name1, tv_bluetooth_address, tv_havamatched, tv_connectdevices;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private static int code = 1;
    private WiperSwitch ws_bttop;
    private ListView lv_havamatched, lv_connectdevices;
    private BluetoothDeviceListAdapter havamatchedAdapter, connectdevicesAdapter;
    private ConnectThread connectThread;
    private Button btn_bluetooth_search;
//    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bluetooth2);
        initView();
        initDAta();
        //加载适配器
        lv_havamatched.setAdapter(havamatchedAdapter);
        lv_connectdevices.setAdapter(connectdevicesAdapter);
//        setListviewHeight(listView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        tvbtnIsNotShow();
        initListener();
    }

    private void initDAta() {
        havamatchedAdapter = new BluetoothDeviceListAdapter(this);
        connectdevicesAdapter = new BluetoothDeviceListAdapter(this);
    }

    private void initView() {
        tv_bluetooth_name = findViewById(R.id.tv_bluetooth_name);
        tv_bluetooth_name1 = findViewById(R.id.tv_bluetooth_name1);
        tv_bluetooth_address = findViewById(R.id.tv_bluetooth_address);
        tv_havamatched = findViewById(R.id.tv_havamatched);
        tv_connectdevices = findViewById(R.id.tv_connectdevices);
        ws_bttop = findViewById(R.id.ws_bttop);
//        ws_opensearch = findViewById(R.id.ws_opensearch);
        lv_havamatched = findViewById(R.id.lv_havamatched);
        lv_connectdevices = findViewById(R.id.lv_connectdevices);
        btn_bluetooth_search = findViewById(R.id.btn_bluetooth_search);
    }

    private void initListener() {
        ws_bttop.setChecked(false);
        //open search show close
        ws_bttop.setOnChangedListener(new WiperSwitch.IOnChangedListener() {
            @Override
            public void onChange(WiperSwitch wiperSwitch, boolean checkStat) {
                if (checkStat) {
                    if (bluetoothAdapter != null) {
                        if (!bluetoothAdapter.isEnabled()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, code);
                        }
                        tvbtnIsShow();
                        tv_bluetooth_name.setText(bluetoothAdapter.getName());
                        tv_bluetooth_address.setText(bluetoothAdapter.getAddress());
                    } else {
                        shortToast("该设备不支持蓝牙");
                    }
                } else {
                    allNotShow();
                    //close
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    } else {
                        shortToast("蓝牙未开启，请先打开蓝牙");
                    }
                }
            }
        });

        btn_bluetooth_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_havamatched.setText("已配对设备");
                tv_havamatched.setBackgroundColor(Color.GRAY);
                tv_connectdevices.setText("可用设备");
                tv_connectdevices.setBackgroundColor(Color.GRAY);
                bluetoothAdapter.startDiscovery();
//                setListviewHeight(lv_havamatched);
//                setListviewHeight(lv_connectdevices);
                //获取所有已绑定的蓝牙设备
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                if (devices.size() > 0) {
                    for (BluetoothDevice device : devices) {
                        // scan and add it to adapter
                        havamatchedAdapter.addDevice(device);
                        //update listview
                        havamatchedAdapter.notifyDataSetChanged();
                        System.out.println("已配对设备：" + device);
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

    private void tvbtnIsNotShow() {
        tv_bluetooth_name1.setVisibility(View.GONE);
        tv_bluetooth_name.setVisibility(View.GONE);
        tv_bluetooth_address.setVisibility(View.GONE);
        btn_bluetooth_search.setVisibility(View.GONE);
    }

    private void tvbtnIsShow() {
        tv_bluetooth_name1.setVisibility(View.VISIBLE);
        tv_bluetooth_name.setVisibility(View.VISIBLE);
        tv_bluetooth_address.setVisibility(View.VISIBLE);
        btn_bluetooth_search.setVisibility(View.VISIBLE);
    }

    private void allNotShow() {
        tvbtnIsNotShow();
        tv_havamatched.setVisibility(View.GONE);
        tv_connectdevices.setVisibility(View.GONE);
        tv_havamatched.setBackgroundColor(Color.WHITE);
        tv_connectdevices.setBackgroundColor(Color.WHITE);
        havamatchedAdapter.clear();
        havamatchedAdapter.notifyDataSetChanged();
        connectdevicesAdapter.clear();
        connectdevicesAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("--action-------" + action);
            //获取已搜索到的设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索到的非配对的蓝牙设备
                connectdevicesAdapter.addDevice(device);
//                setListviewHeight();
                connectdevicesAdapter.notifyDataSetChanged();
//                tv_devices.setText(tv_devices.getText() + "\n" + device.getName() + "-->" + device.getAddress());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void setListviewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int allHeight = 0;
        int count = 20;
        if (listView.getCount() < 20) {
            count = listView.getCount();
        }
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            allHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = allHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
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
                Method method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) method.invoke(device, 1);//这里端口为1
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
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
