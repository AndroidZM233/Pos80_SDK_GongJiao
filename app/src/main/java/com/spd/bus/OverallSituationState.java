package com.spd.bus;
/**
 * 状态码描述
 * @author hzjst
 *
 */
public class OverallSituationState {
	//双免验证成功与否
  public  static final String SM_PAYRECODE_SUCCESS="00";//双免验证成功 00
  public  static final String SM_PAYRECODE_FAILED ="01";//双免验证失败 01
  public  static final String SM_PAYRECODE_UNPAID ="02";//双免未进行上送支付 02(默认)
  //type ---记录种类
  public  static final String SM_TYPE ="03";//03 ---双免记录类型（type）
  public  static final String ODA_TYPE ="04"; //04 ---ODA记录类型(type)
  public  static final String MOREN_TYPE ="11"; //11 ---ODA默认记录类型(type)
  //oda是否启用 
  public  static final String ODA_PAYRDCODE_NOENABLE ="05";// ODA记录暂未启用 05
  public  static final String ODA_PAYRDCODE_ENABLE ="06";// ODA记录启用 06,
//PAYSTATUS ---付款状态
  public  static final String SM_PAYSTATUS_FAILED="07";   //双免支付失败---07
  public  static final String SM_PAYSTATUS_SUCCESS="08";   //双免支付成功---08
//上传状态TAG
  public  static final String TAG_UPLOAD_SUCCESS="09";//上传成功 09
  public  static final String TAG_UPLOAD_FAILED="10";  //上传失败10，默认也是10
  //isPay  0未支付  1--已支付
  public  static final String ISPAY_SUCESS="1";//已支付
  public  static final String ISPAY_FAILED="0";//未支付
}
