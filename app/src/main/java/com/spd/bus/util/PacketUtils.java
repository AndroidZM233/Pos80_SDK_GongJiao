package com.spd.bus.util;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.spd.base.utils.LogUtils;
import com.spd.bus.Info;
import com.spd.bus.entity.TransportCard;
import com.spd.bus.sql.SqlStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: PacketUtils
 * package：com.yihuatong.tjgongjiaos.kpregister
 * Created by hzjst on 2018/3/5.
 * E_mail：hzjstning@163.com
 * Description：对请求信息进行封包
 */
public class PacketUtils {
    private static final String LOG = "Packet.RegisMachineUrl:";
    private Context context;
    private String deviceid = "";
    // 注册参数
    String reqData = "{}";
    String deviceId = "0";
    String companyId = "0";// 公司号
    String companyName = "0";// 公司名称
    String lineId = "0";// 线路号
    String lineName = "0";// 线路名称
    String lineCode = "0";//
    String lineShort = "0";// 简称
    String carryId = "0";//
    String carryCode = "0";// 车辆号
    String carryNo = "0";// 自编号
    String price = "0";// 票价
    String type = "0";// 类型 如：0是一票制，1是多票制
    String gprsType = "0";// 流量卡类型
    String gprsCode = "0";// 流量卡类型
    String supplier = "0";// posid
    String headShow = "0";// 显示头信息
    String lineShow = "0";// 显示线路信息
    String originalPrice = "0";// 原始票价
    String localCompanyName = "0";// 运营公司
    String localCompanyCode = "0";// 运营公司编号
    String localDepartmentName = "0";// 分公司名称
    String localDepartmentCode = "0";// 分公司编号
    String appKey = "6FF5BB054CE88470";
    EN_CH_NumberDate linew;

    /**
     * methed:RegisMachineUrl
     * 说明：注册机具所需参数封包
     *
     * @return
     */
    public String RegisMachineUrl() {

        List<TransportCard> listPram = new ArrayList<TransportCard>();
        listPram = SqlStatement.getParameterAll();
        String info = listPram.get(0).getInfo();
        if (!info.equals("00")) {
            deviceId = listPram.get(0).getDevice_number();
            companyId = info.substring(40, 42);
            lineId = info.substring(46, 50);
            linew = new EN_CH_NumberDate(lineId);
            lineName = linew.setShowLine();
            lineShort = lineId;// 简称
            carryId = listPram.get(0).getBus_number();//
            carryCode = carryId;// 车辆号
            carryNo = info.substring(42, 46);// 自编号
            String per = info.substring(64, 68);
            price = Integer.parseInt(per, 16) + "";// 票价
        }
        type = "0";// 类型 如：0是一票制，1是多票制
        supplier = deviceId;// posid
        headShow = "";// 显示头信息
        lineShow = lineName;// 显示线路信息
        originalPrice = price;// 原始票价
        LogUtils.v("注册==================开始json=====================");
        try {
            reqData = creatJson(deviceId, companyId, lineId, carryId,
                    companyName, lineName, lineCode, lineShort, carryCode,
                    carryNo, price, gprsCode, type, gprsType, supplier,
                    headShow, lineShow, originalPrice, localCompanyName,
                    localCompanyCode, localDepartmentName, localDepartmentCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL_REGIS = Info.REGIDTER_URL + reqData + "&appKey=" + appKey;
        Log.i(LOG, "注册机具URL_REGIS==" + URL_REGIS);

        return URL_REGIS;
    }

    /**
     * 将参数封装json
     *
     * @param deviceId
     * @param companyId
     * @param lineId
     * @param carryId
     * @param companyName
     * @param lineName
     * @param lineCode
     * @param lineShort
     * @param carryCode
     * @param carryNo
     * @param price
     * @param gprsCode
     * @param type
     * @param gprsType
     * @param supplier
     * @param headShow
     * @param lineShow
     * @param originalPrice
     * @param localCompanyName
     * @param localCompanyCode
     * @param localDepartmentName
     * @param localDepartmentCode
     * @return
     * @throws JSONException
     */
    private String creatJson(String deviceId, String companyId, String lineId,
                             String carryId, String companyName, String lineName,
                             String lineCode, String lineShort, String carryCode,
                             String carryNo, String price, String gprsCode, String type,
                             String gprsType, String supplier, String headShow, String lineShow,
                             String originalPrice, String localCompanyName,
                             String localCompanyCode, String localDepartmentName,
                             String localDepartmentCode) throws JSONException {
        JSONObject jsObject = new JSONObject();
        jsObject.put("deviceId", deviceId);
        jsObject.put("companyId", companyId);
        jsObject.put("lineId", lineId);
        jsObject.put("carryId", carryId);
        jsObject.put("companyName", companyName);
        jsObject.put("lineName", lineName);
        jsObject.put("lineCode", lineCode);
        jsObject.put("lineShort", lineShort);
        jsObject.put("carryCode", carryCode);
        jsObject.put("carryNo", carryNo);
        jsObject.put("price", price);
        jsObject.put("gprsCode", gprsCode);
        jsObject.put("type", type);
        jsObject.put("gprsType", gprsType);
        jsObject.put("supplier", supplier);
        jsObject.put("headShow", headShow);
        jsObject.put("lineShow", lineShow);
        jsObject.put("originalPrice", originalPrice);
        jsObject.put("localCompanyName", localCompanyName);
        jsObject.put("localCompanyCode", localCompanyCode);
        jsObject.put("localDepartmentName", localDepartmentName);
        jsObject.put("localDepartmentCode", localDepartmentCode);
        String datajson = jsObject.toString();
        return datajson;
    }

    /**
     * 获取秘钥版本URL
     *
     * @return
     */
    public String getSercetVersion() {
        String URL_KEY_VERSION = Info.URL_KEYVERSION + appKey;
        return URL_KEY_VERSION;
    }

    /**
     * 获取秘钥URL
     *
     * @param keyVersion
     * @return
     */
    public String getAppKey(String keyVersion) {
        String reqData = "{\"version\":\"" + keyVersion + "\"}";
        String URLKEYLIST = Info.URL_KEYLIST + reqData + "&appKey="
                + appKey;
        return URLKEYLIST;
    }

    /**
     * methed:RegisMachineUrl
     * 说明：金码注册机具所需参数封包
     *
     * @return
     */
    public String RegisMachineUrlJM() {

        List<TransportCard> listPram = new ArrayList<TransportCard>();
        listPram = SqlStatement.getParameterAll();
        String info = listPram.get(0).getInfo();
        if (!info.equals("00")) {
            deviceId = listPram.get(0).getDevice_number();
            companyId = info.substring(40, 42);
            lineId = info.substring(46, 50);
            linew = new EN_CH_NumberDate(lineId);
            lineName = linew.setShowLine();
            lineShort = lineId;// 简称
            carryId = listPram.get(0).getBus_number();//
            carryCode = carryId;// 车辆号
            carryNo = info.substring(42, 46);// 自编号
            String per = info.substring(64, 68);
            price = Integer.parseInt(per, 16) + "";// 票价
        }
        type = "0";// 类型 如：0是一票制，1是多票制
        supplier = deviceId;// posid
        headShow = "";// 显示头信息
        lineShow = lineName;// 显示线路信息
        originalPrice = price;// 原始票价
        LogUtils.v("注册JM==================开始json=====================");
        try {
            reqData = getJsonRegis(deviceId, companyId, lineId, carryId,
                    companyName, lineName, lineCode, lineShort, carryCode,
                    carryNo, price, gprsCode, type, gprsType, supplier,
                    headShow, lineShow, originalPrice, localCompanyName,
                    localCompanyCode, localDepartmentName, localDepartmentCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String URL_REGIS = Info.JM_REGISER_DATA+ reqData;
        Log.i(LOG, "注册机具URL_REGIS==" + URL_REGIS);

        return URL_REGIS;
    }

    /**
     * 金码注册机具
     */
    // http://112.74.41.206:8086/transfor/gate?sid=10202&encode=utf-8&appKey=9A174E6CF9AAD017&reqData={sid:"10202",deviceId:"18161165",companyId:"01",lineId:"0123",carryId:"",companyName:"01",lineName:"023",lineCOde:"0123",lineShort:"123",carryCode:"",carryNo:"012365",price:"300",gprsCode:"",type:"",gprsType:"",supplier:"",headShow:"",lineShow:"123",originalPrice:"",localCompanyCode:"01",localDepartmentName:"01",localDepartmentCode:"01"}
    private String getJsonRegis(String deviceId, String companyId, String lineId,
                                String carryId, String companyName, String lineName,
                                String lineCode, String lineShort, String carryCode,
                                String carryNo, String price, String gprsCode, String type,
                                String gprsType, String supplier, String headShow, String lineShow,
                                String originalPrice, String localCompanyName,
                                String localCompanyCode, String localDepartmentName,
                                String localDepartmentCode) {
//           sid:"10202",deviceId:"18161165",companyId:"01",lineId:"0123",
//                carryId:"",companyName:"01",lineName:"023",lineCOde:"0123",
//                lineShort:"123",carryCode:"",carryNo:"012365",price:"300",
//                gprsCode:"",type:"",gprsType:"",supplier:"",headShow:"",
//                lineShow:"123",originalPrice:"",localCompanyCode:"01",
//                localDepartmentName:"01",localDepartmentCode:"01"

        JSONObject jsonObjectJM = new JSONObject();
        jsonObjectJM.put("sid", 10202);
        jsonObjectJM.put("deviceId",deviceId );
        jsonObjectJM.put("companyId", companyId);
        jsonObjectJM.put("lineId",lineId );
        jsonObjectJM.put("carryId",carryId );
        jsonObjectJM.put("companyName",companyName );
        jsonObjectJM.put("lineName",lineName );
        jsonObjectJM.put("lineCode", lineCode);
        jsonObjectJM.put("lineShort", lineShort);
        jsonObjectJM.put("carryCode",carryCode );
        jsonObjectJM.put("carryNo",carryNo );//车辆号
        jsonObjectJM.put("price", price);
        jsonObjectJM.put("gprsCode", gprsCode);
        jsonObjectJM.put("type",type );
        jsonObjectJM.put("gprsType", gprsType);
        jsonObjectJM.put("supplier", supplier);
        jsonObjectJM.put("headShow",headShow );
        jsonObjectJM.put("lineShow", lineShow);
        jsonObjectJM.put("originalPrice", originalPrice);
        jsonObjectJM.put("localCompanyCode", localCompanyCode);
        jsonObjectJM.put("localDepartmentName",localDepartmentName );
        jsonObjectJM.put("localDepartmentCode", localDepartmentCode);
        return jsonObjectJM.toJSONString();
    }

}


