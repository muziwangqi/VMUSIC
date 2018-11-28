package com.soling.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.soling.R;
import com.soling.service.player.BluetoothChatService;
import com.soling.view.adapter.MusicAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothDevicesListActivity extends BaseActivity {
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private BluetoothChatService bluetoothChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_devices_list);
        setResult(Activity.RESULT_CANCELED);
        Button button = findViewById(R.id.search_bluetooth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(onItemClickListener);
        ListView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(onItemClickListener1);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver,filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver,filter);
        refreshPairedDevicesList();
    }

    public void refreshPairedDevicesList(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device:pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }else{
            String noDevices ="没有已匹配设备";
            //String noDevices = getResources().getText().toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBtAdapter != null){
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        setSupportProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scan);
        //findViewById(R.id.)
        if(mBtAdapter.isDiscovering()){
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView)view).getText().toString();
            String address = info.substring(info.length()-17);
            Intent intent = new Intent(BluetoothDevicesListActivity.this,BluetoothChatActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, info);
            setResult(Activity.RESULT_OK,intent);
            startActivity(intent);
            finish();
        }
    };
    private AdapterView.OnItemClickListener onItemClickListener1 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView)view).getText().toString();
            String address = info.substring(info.length()-17);
            BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(address);
            Handler handler = new Handler();
            bluetoothChatService = new BluetoothChatService(getBaseContext(),handler);
            Boolean returnValue = false;
            Method createBondMethod;
            try {
                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    //反射方法调用
                    createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    System.out.println("开始配对");
                    returnValue = (Boolean) createBondMethod.invoke(bluetoothDevice);
                    shortToast("要连接的设备是：" + bluetoothDevice.getName() + "   " + bluetoothDevice.getAddress());
                } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    bluetoothChatService.connect(bluetoothDevice);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(BluetoothDevicesListActivity.this,BluetoothChatActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK,intent);
            startActivity(intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                    mNewDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setProgressBarIndeterminateVisibility(false);
                setTitle("");
                if(mNewDevicesArrayAdapter.getCount()==0){
                    String noDevicesArrayAdapter = "";
                   // String noDevicesArrayAdapter = getResources().getText(R.id.).toString();
                    mNewDevicesArrayAdapter.add(noDevicesArrayAdapter);
                }
            }
        }
    };
}
