package com.soling.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.soling.App;
import com.soling.R;

public class GexingActivity extends BaseActivity {
    private Button btn_zhuti, btn_bizhi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gexinghua);
        btn_zhuti = findViewById(R.id.btn_zhuti);
        btn_bizhi = findViewById(R.id.btn_bizhi);
        btn_zhuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentJump(App.getInstance(), ThemeActivity.class);
            }
        });
        btn_bizhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentJump(App.getInstance(), BizhiActivity.class);
            }
        });
    }
}
