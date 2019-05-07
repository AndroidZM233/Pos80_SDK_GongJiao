package com.spd.bus.spdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.configcheck.ConfigCheckActivity;

/**
 * Created by 张明_ on 2019/4/29.
 * Email 741183142@qq.com
 */
public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.v(intent.getAction());
        //新的应用安装
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
            if (packageName.equals(context.getPackageName())) {
                Intent ootStartIntent = new Intent(context, ConfigCheckActivity.class);
                ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(ootStartIntent);
            }

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            //应用替换成功
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName.equals(context.getPackageName())) {
                Intent ootStartIntent = new Intent(context, ConfigCheckActivity.class);
                ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(ootStartIntent);
            }

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            //应用被卸载
            String packageName = intent.getData().getSchemeSpecificPart();
        }

    }
}
