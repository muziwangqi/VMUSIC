package com.soling.bt.Util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class BluetoothUtil {
    public static void enableBluetooth(Activity activity, int requestCode){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(intent);
    }

    public static boolean isSupportBle(Context context){
        if(context==null||context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            return false;
        }
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return manager.getAdapter()!=null;
    }

    public static boolean isBleEnable(Context context){
        if(!isSupportBle(context)){
            return false;
        }
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return manager.getAdapter().isEnabled();
    }

    public static void printServices(BluetoothGatt gatt){
        if(gatt !=null){
            for(BluetoothGattService service :gatt.getServices()){
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                    for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()){

                    }
                }
            }
        }
    }
}
