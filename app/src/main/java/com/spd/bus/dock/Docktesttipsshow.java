package com.spd.bus.dock;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.R;

import wangpos.sdk4.libbasebinder.Dock;

public class Docktesttipsshow extends Activity {
    private Dock mDock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dock_test);
        ((Button)findViewById(R.id.dock_testn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                try {
                    i = mDock.test();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.v("----zhangjinglog----","DockTest");
                ((TextView)findViewById(R.id.dock_test_tip)).setText(i+"");
            }
        });
        ((Button)findViewById(R.id.docktestexit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread(){
            @Override
            public void run() {
                mDock = new Dock(getApplicationContext());
            }
        }.start();
    }
}
