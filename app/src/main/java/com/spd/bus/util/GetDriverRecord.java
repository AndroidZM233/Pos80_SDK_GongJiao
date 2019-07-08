package com.spd.bus.util;

import com.activeandroid.util.Log;
import com.spd.base.utils.LogUtils;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.sql.SqlStatement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetDriverRecord {
    private String driverRecord = "0";

    public String getDriverRecord() {
        // 获取当前司机记录

        List<Payrecord> listdriverid = new ArrayList<Payrecord>();
        try {
            listdriverid = SqlStatement.ReciprocalDriverRecord();
            if (listdriverid.size() != 0) {
                driverRecord = listdriverid.get( 0 ).getRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
            driverRecord = "0";

        }
        return driverRecord;
    }

    public String getDrivertime() {

        try {
            List<Payrecord> listdriverid = new ArrayList<Payrecord>();
            listdriverid = SqlStatement.ReciprocalDriverRecord();
            if (listdriverid.size() != 0) {
                driverRecord = listdriverid.get( 0 ).getDatetime();
                Date date = new Date( Long.parseLong( driverRecord ) );
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMddHHmmss" );
                driverRecord = formatter.format( date );
                LogUtils.i( "司机签到时间：==" + driverRecord );
            }
        } catch (Exception e) {
            e.printStackTrace();
            driverRecord = "0";

        }
        return driverRecord;
    }

    public String getDrivertime3() {

        try {
            List<Payrecord> listdriverid = new ArrayList<Payrecord>();
            listdriverid = SqlStatement.ReciprocalDriverRecord();
            if (listdriverid.size() != 0) {
                driverRecord = listdriverid.get( 0 ).getDatetime();
                Date date = new Date( Long.parseLong( driverRecord ) );
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                driverRecord = formatter.format( date );
                LogUtils.i( "司机签到时间：==" + driverRecord );
            }
        } catch (Exception e) {
            e.printStackTrace();
            driverRecord = "0";

        }
        return driverRecord;
    }

    public long getDrivertime2() {
        long time = 0;
        try {
            List<Payrecord> listdriverid = new ArrayList<Payrecord>();
            listdriverid = SqlStatement.ReciprocalDriverRecord();
            if (listdriverid.size() != 0) {
                driverRecord = listdriverid.get( 0 ).getDatetime();
                time = Long.valueOf( driverRecord ) / 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
            time = 0;
        }
        // }
        return time;
    }

    public long getlimtId() {
        List<Payrecord> listId = new ArrayList<Payrecord>();
        listId = SqlStatement.getRecordID();
        if (listId.size() != 0) {
            long id = listId.get( 0 ).getTradingflow();
            Log.i( "id==", id + "" );
            id = (id + 1) & 0xFFFF;
            Log.i( "id==", id + "" );
            return id;
        } else {
            return 1;
        }

    }

    // 获取当前司机记录
    public static String dirverRecordData() {
        List<Payrecord> listdriverid = new ArrayList<Payrecord>();
        listdriverid = SqlStatement.selectConsumptionRecord();
        if (listdriverid.size() != 0) {
            // String record = listdriverid.toString();
            String recordriver = listdriverid.get( 0 ).getRecord();
            String id = recordriver.substring( 124, 128 );
            Log.i( "id=" + id, "hzj" + id );
            int ids = Integer.parseInt( id, 16 );
            ids = ids & 0xFFFF;
            listdriverid = SqlStatement.busrecordTradingFlow( (int) ids );
            if (listdriverid.size() == 0) {
                Log.i( "获取包头信息1--listdriverid.size() != 0" + ids, "hzj" );
                listdriverid = SqlStatement.selectDriverRecord( ids );
                if (listdriverid.size() == 0) {
                    listdriverid = SqlStatement.ReciprocalDriverRecord();
                    if (listdriverid.size() != 0) {
                        Log.i( "司机记录=" + listdriverid.get( 0 ).getRecord(), "hzj" );
                        return listdriverid.get( 0 ).getRecord();

                    } else {
                        return listdriverid.get( 0 ).getRecord();
                    }
                } else {
                    return listdriverid.get( 0 ).getRecord();
                }
            } else {
                return listdriverid.get( 0 ).getRecord();
            }
        } else {
            listdriverid = SqlStatement.ReciprocalDriverRecord();
            Log.i( "listdriverid(tag=0)=" + listdriverid, "hzj" );
            if (listdriverid.size() != 0) {
                Log.i( "司机记录=" + listdriverid.get( 0 ).getRecord(), "hzj" );
                return listdriverid.get( 0 ).getRecord();
            } else {
                return "0000";
            }

        }
    }



    // 获取当前司机记录
    public static String dirverRecordData2(String recordriver) {
        List<Payrecord> listdriverid = new ArrayList<Payrecord>();

        String id = recordriver.substring( 124, 128 );
        Log.i( "id=" + id, "hzj" + id );
        int ids = Integer.parseInt( id, 16 );
        ids = ids & 0xFFFF;
        listdriverid = SqlStatement.busrecordTradingFlow( (int) ids );
        if (listdriverid.size() == 0) {
            Log.i( "获取包头信息1--listdriverid.size() != 0" + ids, "hzj" );
            listdriverid = SqlStatement.selectDriverRecord( ids );
            if (listdriverid.size() == 0) {
                listdriverid = SqlStatement.ReciprocalDriverRecord();
                if (listdriverid.size() != 0) {
                    Log.i( "司机记录=" + listdriverid.get( 0 ).getRecord(), "hzj" );
                    return listdriverid.get( 0 ).getRecord();

                } else {
                    return listdriverid.get( 0 ).getRecord();
                }
            } else {
                return listdriverid.get( 0 ).getRecord();
            }
        } else {
            return listdriverid.get( 0 ).getRecord();
        }

    }

    public static long getdriverId() {
        List<Payrecord> listId = new ArrayList<Payrecord>();
        listId = SqlStatement.driverRecord();
        if (listId.size() != 0) {
            long id = listId.get( 0 ).getId();
            return id;
        } else {
            return 0;
        }

    }
}
