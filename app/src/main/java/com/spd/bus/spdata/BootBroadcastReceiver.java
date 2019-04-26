package com.spd.bus.spdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.spd.bus.spdata.configcheck.ConfigCheckActivity;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2018/2/28   17:25.
 * 联系方式:QQ:420401567
 * 功能描述:
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    static final String app_action = "android.intent.action.PACKAGE_ADDED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent ootStartIntent = new Intent(context, ConfigCheckActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }

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
