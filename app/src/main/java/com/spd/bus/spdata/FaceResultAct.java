package com.spd.bus.spdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.spd.bus.R;
import com.spd.bus.spdata.utils.PlaySound;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import speedata.com.face.Contants;

import static com.spd.bus.spdata.utils.PlaySound.dang;
import static com.spd.bus.spdata.utils.PlaySound.xiaofeiSuccse;

public class FaceResultAct extends BaseActivity {

    private ImageView imgState;
    private TextView tvDebugMsg;
    private BroadcastReceiver broadcastReceiver = new BootBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            if (action.equals(Contants.ACTION_RECE_FACE)) {
                boolean isSuccess = intent.getBooleanExtra("issuccess", false);
                if (isSuccess) {
                    imgState.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pass));
                    starttimeTask();
                    PlaySound.play(xiaofeiSuccse, 0);
                }
                tvDebugMsg.setText(intent.getStringExtra("msg"));
            } else if (action.equals(Contants.ACTION_UPLOAD_STATUS)) {
                boolean isSuccess = intent.getBooleanExtra("issuccess", false);
                if (isSuccess) {
                    PlaySound.play(xiaofeiSuccse, 0);
                } else {
                    PlaySound.play(dang, 0);
                }
                tvDebugMsg.append("\n" + intent.getStringExtra("msg"));
            }
        }
    };

    private  void starttimeTask(){

        Flowable.interval(5,  TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        imgState.setImageDrawable(getResources().getDrawable(R.mipmap.ic_face));
                        tvDebugMsg.setText("");
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_result);
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.ACTION_RECE_FACE);
        filter.addAction(Contants.ACTION_UPLOAD_STATUS);
        registerReceiver(broadcastReceiver, filter);
        PlaySound.initSoundPool(this);
    }

    private void initView() {
        imgState = findViewById(R.id.imageView);
        tvDebugMsg = findViewById(R.id.textView3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }
}
