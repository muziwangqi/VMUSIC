package com.soling.utils;

import android.content.Intent;
import android.net.Uri;

import com.soling.App;

import java.io.File;

public class BluetoothUtil {

    public static void share(String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooser  = Intent.createChooser(intent, "share app");
        App.getInstance().startActivity(chooser);
    }

}
