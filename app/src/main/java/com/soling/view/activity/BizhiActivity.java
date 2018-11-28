package com.soling.view.activity;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.soling.R;
import com.soling.view.adapter.ImageAdapter;

import java.io.IOException;

public class BizhiActivity extends BaseActivity {
    private Button btn_set;
    private ImageSwitcher is_pic;
    private Gallery gal_pic;
    private int[] images=new int[]{R.drawable.bc1,R.drawable.bc2,R.drawable.bc3,R.drawable.bc4,R.drawable.bc5,R.drawable.bc6,R.drawable.bc7,R.drawable.bc8};
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bizhi);
        initView();
        setListener();
    }

    private void setListener() {
        is_pic.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view=new ImageView(BizhiActivity.this);
                view.setBackgroundColor(0xFF000000);
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                view.setLayoutParams(new ImageSwitcher.LayoutParams(ImageSwitcher.LayoutParams.MATCH_PARENT,ImageSwitcher.LayoutParams.MATCH_PARENT));
                return view;
            }
        });

        is_pic.setImageResource(images[0]);
        //设置动画
        is_pic.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
        is_pic.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBizhi();
            }
        });
        ImageAdapter imageAdapter=new ImageAdapter(this,images);
        gal_pic.setAdapter(imageAdapter);
        gal_pic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                is_pic.setImageResource(images[position]);
                index=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setBizhi() {
        WallpaperManager wallpaperManager=WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setResource(images[index]);
            shortToast("设置成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        btn_set=findViewById(R.id.btn_set);
        is_pic=findViewById(R.id.is_pic);
        gal_pic=findViewById(R.id.gal_pic);
    }
}
