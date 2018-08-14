package com.wpos.sdkdemo.readcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wpos.sdkdemo.R;

/**
 * Created by Administrator on 2018/1/19.
 */

public class ReadCardManager  extends Activity implements View.OnClickListener{

    private Context context;
    private Button read_wait,read_loop,read_ID,exit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readcard_manager);
        context = this;

        read_wait = (Button) findViewById(R.id.read_wait);
        read_loop = (Button) findViewById(R.id.read_cycle);
        read_ID = (Button) findViewById(R.id.read_ID);
        exit = (Button) findViewById(R.id.exit);


        read_wait.setOnClickListener(this);
        read_loop.setOnClickListener(this);
        read_ID.setOnClickListener(this);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.read_wait:
                intent = new Intent(context, CardReader.class);
                break;
            case R.id.read_cycle:
                intent = new Intent(context, CardReaderNew.class);
                break;
            case R.id.read_ID:
                intent = new Intent(context, IDCardMana.class);
                break;
        }
        startActivity(intent);
    }
}
