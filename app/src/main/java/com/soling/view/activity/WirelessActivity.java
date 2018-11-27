package com.soling.view.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.soling.App;
import com.soling.R;
import com.soling.utils.APNUtil;
import com.soling.utils.MatchAPNUtil;
import com.soling.utils.WifiUtil;
import com.soling.view.adapter.WiperSwitch;

import java.util.ArrayList;
import java.util.List;

public class WirelessActivity extends BaseActivity {

//    private WiperSwitch wsWifiNet, wsBluetooth;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private static int code = 1;
    private WifiManager wifiManager;
    private Button btn_wifinet,btn_bluetoothnet;
//    private WiperSwitch wsMobileNet;
//    private String id, type, apn, curr;
//    private WifiUtil wifiManager;
//    private String wifiSsid,wifiPassword;
//    private EditText editText=new EditText(App.getInstance());

    //Uri uri = Uri.parse("content://telephony/carriers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wireless);
        context = this;
        initViews();
        //getMobileNet();
        getWifiNet();
        getBluetooth();
    }

    @Override
    public void initViews() {
        //wsMobileNet = findViewById(R.id.ws_mobilenet);
//        wsWifiNet = findViewById(R.id.ws_wifinet);
//        wsBluetooth = findViewById(R.id.ws_bluetooth);
        btn_wifinet=findViewById(R.id.btn_wifinet);
        btn_bluetoothnet=findViewById(R.id.btn_bluetoothnet);
    }

    private void getBluetooth() {
//        wsBluetooth.setChecked(false);
//        wsBluetooth.setOnChangedListener(new WiperSwitch.IOnChangedListener() {
//            @Override
//            public void onChange(WiperSwitch wiperSwitch, boolean checkStat) {
////                shortToast("hhhhhhhhhhh");
//                if (checkStat) {
//                    intentJump(App.getInstance(), BluetoothActivity.class);
//                } else {
//
//                }
//            }
//        });
        btn_bluetoothnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentJump(App.getInstance(),BluetoothActivity.class);
            }
        });
    }

    private void getWifiNet() {
//        wsWifiNet.setChecked(false);
//        wsWifiNet.setOnChangedListener(new WiperSwitch.IOnChangedListener() {
//            @Override
//            public void onChange(WiperSwitch wiperSwitch, boolean checkStat) {
//                if (checkStat) {
//                    intentJump(App.getInstance(), WifiActivity.class);
//                } else {
//
//                }
//            }
//        });
        btn_wifinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentJump(App.getInstance(), WifiActivity.class);
            }
        });
    }

    /*private void getMobileNet() {
        //判断是否已连接移动网
//        boolean flag;
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
//            flag = true;
//        } else {
//            flag = false;
//        }
        wsMobileNet.setChecked(false);
        wsMobileNet.setOnChangedListener(new WiperSwitch.IOnChangedListener() {
            @Override
            public void onChange(WiperSwitch wiperSwitch, boolean checkStat) {
                if (checkStat) {
                    //openAPN(context);
                    shortToast("移动网络已开启");
                } else {
                    //closeAPN(context);
                    shortToast("移动网络已关闭");
                }
            }
        });
    }

    public void openAPN(Context context) {
        List<APNUtil> apnList = new ArrayList<APNUtil>();
        apnList=getAPNList();
        for (APNUtil apnUtil : apnList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("apn", MatchAPNUtil.matchType(apnUtil.getApn()));
            contentValues.put("type", MatchAPNUtil.matchType(apnUtil.getType()));
            context.getContentResolver().update(uri, contentValues, "id=?", new String[]{apnUtil.getId()});
        }
    }

    public void closeAPN(Context context) {
        List<APNUtil> apnList = new ArrayList<APNUtil>();
        apnList=getAPNList();
        for (APNUtil apnUtil : apnList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("apn", MatchAPNUtil.matchType(apnUtil.getApn())+"mdev");
            contentValues.put("type", MatchAPNUtil.matchType(apnUtil.getType())+"mdev");//通过设置错误的APN关闭移动网
            context.getContentResolver().update(uri, contentValues, "id=?", new String[]{apnUtil.getId()});
        }
    }

    public ArrayList<APNUtil> getAPNList() {
        String[] projection = {id, type, apn, curr};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);//can't
        ArrayList<APNUtil> list = new ArrayList<APNUtil>();
        while (cursor != null && cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("apn")).equals("")) {

            } else {
                APNUtil apnUtil = new APNUtil();
                apnUtil.setId(cursor.getString(cursor.getColumnIndex("id")));
                apnUtil.setApn(cursor.getString(cursor.getColumnIndex("apn")));
                apnUtil.setType(cursor.getString(cursor.getColumnIndex("type")));
                list.add(apnUtil);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }*/
}
