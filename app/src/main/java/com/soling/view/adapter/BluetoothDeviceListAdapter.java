package com.soling.view.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soling.R;

import java.util.ArrayList;

public class BluetoothDeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> bluetoothDevices;
    private LayoutInflater layoutInflater;
    private Activity context;

    public BluetoothDeviceListAdapter(Activity context) {
        super();
        this.context = context;
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        layoutInflater = context.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if (!bluetoothDevices.contains(device)) {
            bluetoothDevices.add(device);
            System.out.println("name:  " + device.getName() + "   address:  " + device.getAddress());
        }
    }

    public BluetoothDevice getDevice(int position){
        return bluetoothDevices.get(position);
    }

    public void clear() {
        bluetoothDevices.clear();
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_bluetooth_devices, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = convertView.findViewById(R.id.tv_device_name);
            viewHolder.deviceAddress = convertView.findViewById(R.id.tv_device_address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = bluetoothDevices.get(position);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText("未知设备");
        }
        viewHolder.deviceAddress.setText(device.getAddress());
        return convertView;
    }
}

final class ViewHolder {
    TextView deviceName;
    TextView deviceAddress;
}
