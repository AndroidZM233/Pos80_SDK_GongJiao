package com.spd.bus.key;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.spd.bus.R;

/**
 * Created by Administrator on 2018/1/19.
 */

public class KeyManager extends Activity implements View.OnClickListener{

    private Context context;
    private Button import_key,check_Id,eraser_Id,exit,kcv_Id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_manager);
        context = this;

        import_key = (Button) findViewById(R.id.import_key);
        kcv_Id = (Button) findViewById(R.id.kcv_Id);
        check_Id = (Button) findViewById(R.id.check_Id);
        eraser_Id = (Button) findViewById(R.id.eraser_Id);
        exit = (Button) findViewById(R.id.exit);


        import_key.setOnClickListener(this);
        kcv_Id.setOnClickListener(this);
        check_Id.setOnClickListener(this);
        eraser_Id.setOnClickListener(this);
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
            case R.id.import_key:
                intent = new Intent(context, KeyEx.class);
                break;
            case R.id.kcv_Id:
                intent = new Intent(context, EraseKeys.class);
                intent.putExtra("flag","KCV");
                break;
            case R.id.check_Id:
                intent = new Intent(context, CheckkeyExist.class);
                break;
            case R.id.eraser_Id:
                intent = new Intent(context, EraseKeys.class);
                break;
        }
        startActivity(intent);
    }
}
