package com.spd.bus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spd.bus.data.DataEnDecrypt;
import com.spd.bus.data.Mac;
import com.spd.bus.dock.DockTest;
import com.spd.bus.dukpt.DUKPT;
import com.spd.bus.emv.EmvActivity;
import com.spd.bus.key.KeyManager;
import com.spd.bus.pin.Pin;
import com.spd.bus.print.PrinterManager;
import com.spd.bus.readcard.ReadCardManager;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.system.SystemActivity;

/**
 * Created by Administrator on 2018/1/19.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private Context context;
    private TextView app_title;
    private Button system, card, key, pin, en_decrypt, mac, print, dukpt, dock, emv, btnPsamIC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        context = this;

        app_title = (TextView) findViewById(R.id.txt_app_title);
        system = (Button) findViewById(R.id.sys_Id);
        btnPsamIC = (Button) findViewById(R.id.btn_psam_ic);
        card = (Button) findViewById(R.id.card_Id);
        key = (Button) findViewById(R.id.key_Id);
        pin = (Button) findViewById(R.id.pin_Id);
        en_decrypt = (Button) findViewById(R.id.en_de_Id);
        mac = (Button) findViewById(R.id.mac_Id);
        print = (Button) findViewById(R.id.print_Id);
        dukpt = (Button) findViewById(R.id.dukpt_Id);
        dock = (Button) findViewById(R.id.dock_Id);
        emv = (Button) findViewById(R.id.emv_Id);

        btnPsamIC.setOnClickListener(this);
        system.setOnClickListener(this);
        card.setOnClickListener(this);
        key.setOnClickListener(this);
        pin.setOnClickListener(this);
        en_decrypt.setOnClickListener(this);
        mac.setOnClickListener(this);
        print.setOnClickListener(this);
        dukpt.setOnClickListener(this);
        dock.setOnClickListener(this);
        emv.setOnClickListener(this);

        try {
            app_title.setText(getString(R.string.app_title) + getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String device = Build.MODEL;
        if (!device.endsWith("TAB")) {
            dock.setVisibility(View.GONE);
        }
        if (device.endsWith("TAB") || device.endsWith("MINI")) {
            print.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_psam_ic:
                //思必拓demo PSAM+IC 消费流程
                intent = new Intent(context, PsamIcActivity.class);
//                intent = new Intent(context,Main2Activity.class);
                break;
            case R.id.sys_Id:

                intent = new Intent(context, SystemActivity.class);
                break;
            case R.id.card_Id:
                intent = new Intent(context, ReadCardManager.class);
                break;
            case R.id.key_Id:
                intent = new Intent(context, KeyManager.class);
                break;
            case R.id.pin_Id:
                intent = new Intent(context, Pin.class);
                break;
            case R.id.en_de_Id:
                intent = new Intent(context, DataEnDecrypt.class);
                break;
            case R.id.mac_Id:
                intent = new Intent(context, Mac.class);
                break;
            case R.id.print_Id:
                intent = new Intent(context, PrinterManager.class);
                break;
            case R.id.dukpt_Id:
                intent = new Intent(context, DUKPT.class);
                break;
            case R.id.dock_Id:
                intent = new Intent(context, DockTest.class);
                break;
            case R.id.emv_Id:
                intent = new Intent(context, EmvActivity.class);
                break;
        }
        startActivity(intent);
    }

}
