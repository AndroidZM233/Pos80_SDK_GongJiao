package com.spd.bus.util;

import java.util.Date;

/**
 * Class: EN_CH_NumberDate
 * package：com.yihuatong.tjgongjiaos.utils
 * Created by hzjst on 2018/5/22.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class EN_CH_NumberDate {


    public String showline = "";
    public String line;// 线路号

    public EN_CH_NumberDate(String line) {
        this.line = line;
    }

    /***
     * 根据线路号换算展示线路名称
     */

    /**
     * 0XXX：直接显示线路号（去掉前面的所有“0”）.; 1XXX：显示K+3位线路号（去掉XXX前面所有的“0”）;
     * 80XX：显示观+2位线路号（去掉XXX前面所有的“0”）; 2XXX：显示 专+3位线路号（去掉XXX前面所有的“0”）; 旅游专线：3XXX;
     * 通勤快车：4XXX; 显示 游XXX路和通XXX路; 快186：8186 观3路：8203（8003被一公司占用） 旅20路：8920（一公司）
     */
    public String setShowLine() {
        String line_first = line.substring( 0, 1 );
        int lineno = Integer.parseInt( line_first );
        String line_second = line.substring( 1, 2 );
        String line_third = line.substring( 2, 3 );
        String line_fourth = line.substring( 3, 4 );
        if (line.equals( "8186" )) {
            showline = "快" + line.substring( 1, 4 ) + "路";
        } else if (line.equals( "8203" )) {
            showline = "观" + line.substring( 3, 4 ) + "路";
        } else if (line.equals( "8920" )) {
            showline = "旅" + line.substring( 2, 4 ) + "路";
        } else {

            switch (lineno) {
                case 0:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = line.substring( 3, 4 ) + "路";
                        } else {
                            showline = line.substring( 2, 4 ) + "路";
                        }
                    } else {
                        showline = line.substring( 1, 4 ) + "路";
                    }

                    break;
                case 1:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "K" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "K" + line.substring( 2, 4 ) + "路";
                        }
                    } else {
                        showline = "K" + line.substring( 1, 4 ) + "路";
                    }
                    break;
                case 2:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "专" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "专" + line.substring( 2, 4 ) + "路";
                        }
                    } else {
                        showline = "专" + line.substring( 1, 4 ) + "路";
                    }
                    break;
                case 3:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "游" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "游" + line.substring( 2, 4 ) + "路";
                        }
                    } else {
                        showline = "游" + line.substring( 1, 4 ) + "路";
                    }
                    break;
                case 4:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "通" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "通" + line.substring( 2, 4 ) + "路";
                        }
                    } else {
                        showline = "通" + line.substring( 1, 4 ) + "路";
                    }
                    break;
                case 6:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "测试线路";
                        }
                    } else {
                        showline = line.substring( 0, 4 ) + "路";
                    }
                    break;
                case 8:
                    if (line_second.equals( "0" )) {
                        if (line_third.equals( "0" )) {
                            showline = "观" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "观" + line.substring( 2, 4 ) + "路";
                        }
                    } else if (line_second.equals( "1" )) {
                        if (line_third.equals( "0" )) {
                            showline = "快" + line.substring( 3, 4 ) + "路";
                        } else {
                            showline = "快" + line.substring( 2, 4 ) + "路";
                        }
                    }
                    break;
                default:

                    showline = line.substring( 0, 4 ) + "路";

                    break;
            }

        }
        return showline;
    }

    /**
     * jni版本以及k21版本转换显示
     */

    // 按指定位置截取，获得固件版本和jni版本
    static String monthed = "";

    public static String Intercep(String string) {
        String month = string.substring( 0, 3 );
        String day = string.substring( 4, 6 );
        String year = string.substring( 7, 11 );
        if (month.equals( "Jan" )) {
            monthed = "01";
        } else if (month.equals( "Feb" )) {
            monthed = "02";
        } else if (month.equals( "Mar" )) {
            monthed = "03";
        } else if (month.equals( "Apr" )) {
            monthed = "04";
        } else if (month.equals( "May" )) {
            monthed = "05";
        } else if (month.equals( "Jun" )) {
            monthed = "06";
        } else if (month.equals( "Jul" )) {
            monthed = "07";
        } else if (month.equals( "Aug" )) {
            monthed = "08";
        } else if (month.equals( "Sep" )) {
            monthed = "09";
        } else if (month.equals( "Oct" )) {
            monthed = "10";
        } else if (month.equals( "Nov" )) {
            monthed = "11";
        } else if (month.equals( "Dec" )) {
            monthed = "12";
        }

        return (year + "-" + monthed + "-" + day);

    }

    // 获取毫秒值，将其转换为String
    public static String getDatetime() {
        // 获取当前时间的毫秒值
        Date dt = new Date();
        Long Millisecond = dt.getTime();
        String datatime = String.valueOf( Millisecond );
        return datatime;
    }
}
