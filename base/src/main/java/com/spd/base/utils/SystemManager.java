package com.spd.base.utils;

import android.util.Log;

import java.io.DataOutputStream;
import java.lang.reflect.Method;

public class SystemManager {
    /**
     * 应用程序运行命令获取 Root权限,设备必须已破解(获得ROOT权限)
     *
     * @param command 命令:String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "/n");
            os.writeBytes("exit/n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }

    public static String getDeviceSN() {

        String serialNumber = android.os.Build.SERIAL;

        return serialNumber;
    }

    public static String getDeviceSN2() {

        String serial = null;

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }
}
