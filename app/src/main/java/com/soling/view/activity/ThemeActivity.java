package com.soling.view.activity;

import android.os.Bundle;
import android.view.View;

import com.soling.App;
import com.soling.R;

public class ThemeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        if (App.getInstance().isTHEMEC()){
//            App.getInstance().setTHEMEC(true);
//            setTheme(R.style.dayTheme);
//        }else {
//            App.getInstance().setTHEMEC(false);
//            setTheme(R.style.nightTheme);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_theme);
        findViewById(R.id.ibtn_theme_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isThemec()){
                    shortToast("当前已为主题粉");
                }else{
                    App.getInstance().setTHEMEC(true);
                    setTheme(R.style.nightTheme);
                    shortToast("换肤成功");
                }
                intentJump(App.getInstance(),MainActivity.class);
                finish();
                overridePendingTransition(0,0);
            }
        });
        findViewById(R.id.ibtn_theme_night).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isThemec()==false){
                    shortToast("当前已为主题蓝");
                }else{
                    App.getInstance().setTHEMEC(false);
                    setTheme(R.style.dayTheme);
                    shortToast("换肤成功");
                }
                intentJump(App.getInstance(),MainActivity.class);
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

}
