package com.soling.view.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.soling.R;
import com.soling.service.player.PlayerService;
import com.soling.utils.LightDialogUtil;
import com.soling.view.activity.BluetoothActivity;
import com.soling.view.activity.GexingActivity;
import com.soling.view.activity.ThemeActivity;
import com.soling.view.activity.WifiActivity;
import com.soling.view.adapter.ShakeListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingModuleFragment extends BaseFragment {

    private static final String TAG = SettingModuleFragment.class.getSimpleName();

    private Button btnLight, btnTheme;
    private EditText etTime;
    private DatePickerDialog datePickerDialog;
    private SeekBar sb_hand_mode;
    private CheckBox cb_auto_mode;
    private int MAX_BRIGHTNESS = 255;
    private int MIN_BRIGHTNESS = 30;
    private ShakeListener.OnShakeListener onShakeListener;
    private ShakeListener shakeListener;// 震动监听
    private PlayerFragment playerFragment = new PlayerFragment();
    private PlayerService playerService;
    private Context context;
    private Button btn_wifinet1, btn_bluetoothnet1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        shakeListener = new ShakeListener(context);
//        shakeListener.stop();
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                // qiege
                Intent service = new Intent(context, PlayerService.class);
                service.setAction(PlayerService.ACTION_PLAY_NEXT);
                context.startService(service);
                Toast.makeText(context, "sqiege", Toast.LENGTH_SHORT).show();
            }
        });
        return inflater.inflate(R.layout.fragment_settingmodule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        setListener();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("yyyy年MM月dd日");
        SimpleDateFormat date = new SimpleDateFormat(stringBuilder.toString());
        etTime.setText(date.format(new Date()));
        //System.out.println("------date--"+date.format(new Date()));
    }

    private void initView() {
        etTime = (EditText) findViewById(R.id.et_time);
        btnLight = (Button) findViewById(R.id.btn_light);
        btnTheme = (Button) findViewById(R.id.btn_theme);
        btn_wifinet1= (Button) findViewById(R.id.btn_wifinet1);
        btn_bluetoothnet1= (Button) findViewById(R.id.btn_bluetoothnet1);
    }

    private void setListener() {

        btn_wifinet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WifiActivity.class);
                startActivity(intent);
            }
        });

        btn_bluetoothnet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),BluetoothActivity.class);
                startActivity(intent);
            }
        });

        etTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showTimeDialog();
                    return true;
                }
                return false;
            }
        });

        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimeDialog();
                }
            }
        });

<<<<<<< 1f925ad3a04256c4226ed80d0a94409a8b4bdc3a
=======

>>>>>>> add bluetooth1128
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewDialog = getLayoutInflater().inflate(R.layout.layout_light_dialog, null);
                cb_auto_mode = viewDialog.findViewById(R.id.cb_auto_mode);
                sb_hand_mode = viewDialog.findViewById(R.id.sb_hand_mode);
                final int currBrightness = LightDialogUtil.getSystemBrightness();
                final int currBrightnessMode = LightDialogUtil.getBrightnessMode();

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("调节亮度");
                dialog.setView(viewDialog);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setInfactSystemBrightness(-MAX_BRIGHTNESS);
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        revoverBrightness(currBrightnessMode, currBrightness);
                    }
                });
                dialog.show();

                boolean autoMode = (currBrightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                cb_auto_mode.setChecked(autoMode);
                int progress = currBrightness - MIN_BRIGHTNESS;
                sb_hand_mode.setProgress(progress < 0 ? 0 : progress);
                sb_hand_mode.setMax(MAX_BRIGHTNESS - MIN_BRIGHTNESS);
                //auto
                cb_auto_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            sb_hand_mode.setVisibility(View.GONE);
                            LightDialogUtil.setBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                            Log.d(TAG, "onCheckedChanged: currBrightness  " + currBrightness);
                        } else {
                            //cb_auto_mode.setVisibility(View.GONE);
                            sb_hand_mode.setVisibility(View.VISIBLE);
                            LightDialogUtil.setBrightnessMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        }
                    }
                });

                //hand
                sb_hand_mode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int brightness = progress + MIN_BRIGHTNESS;
                        setInfactSystemBrightness(brightness);
                        LightDialogUtil.setSystemBrightness(brightness);
                        Log.d(TAG, "onCheckedChanged: currBrightness  " + currBrightness);
                        Log.d(TAG, "onProgressChanged: process   " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                if (autoMode) {
                    sb_hand_mode.setVisibility(View.GONE);
                }
            }
        });

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GexingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void fragmentJump(Context context,Class<?> cls){
        Intent intent=new Intent(context,cls);
        startActivity(intent);
    }

    //setting: time dialog
    public void showTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etTime.setText(year + "年" + month + "月" + dayOfMonth + "日");
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    //设置屏幕亮度并反馈到手机屏幕上
    public void setInfactSystemBrightness(final int brightness) {
        final WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / (float) MAX_BRIGHTNESS;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    //点击取消需要还原亮度和模式
    private void revoverBrightness(int currBrightnessMode, int currBrightness) {
        LightDialogUtil.setBrightnessMode(currBrightnessMode);
        setInfactSystemBrightness(-MAX_BRIGHTNESS);
        LightDialogUtil.setSystemBrightness(currBrightness);
    }
}
