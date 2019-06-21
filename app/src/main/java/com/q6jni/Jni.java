package com.yht.q6jni;

import android.util.Log;

public class Jni {

	static {
		try {
			System.loadLibrary("Q6lib");
			Log.i("jni", "find Q6lib so!");
		} catch (Throwable e) {
			Log.e("jni", "i can't find Q6lib so!");
			e.printStackTrace();
		}
	}

	// public static native int mytestbarcode(byte[] recvBuf);
	public static native String GetQrcode();

	// 锟斤拷锟节撅拷态锟斤拷锟斤拷锟斤拷锟节讹拷锟斤拷锟斤拷锟斤拷锟斤拷jclass. 锟斤拷示锟斤拷锟斤拷锟洁。
	// public static native String Psamtest(String buf);
	/*
	 * public static native String Rfidtest(String bu f);
	 * 
	 * public static native String Rfidcard2(Stri ng buf);
	 */
	// 椤剁伅鏄剧ず
	// 璁剧疆led
	/**
	 * 1浜紝0鐏�
	 * 
	 * @param buf
	 * @return
	 */
	public static native int SetLed(String buf);

	// 璁剧疆鍙傛暟
	public static native String Parameter();

	// 鍙告満绛惧埌
	public static native String DriverSign();

	// 瀵诲崱
	public static native String Rfidcard();

	// 鑾峰彇鎸夐敭
	public static native int GetButton();

	// psamt
	public static native String Psamtest(String unionpayKey);

	// 娑堣垂
	public static native String RfidDectValue(int isBlock);

	//
	public native int getAmount();

	// 鑳屾樉
	public static native String BackDisplayShow(String buf);

	// 璇诲彇杞﹁締鍙峰拰璁惧鍙�
	public static native String ReadDeviceInfo();

	// 璁剧疆鏃堕棿
	public static native int SetTime(byte[] buf);

	// 鑾峰彇鏃堕棿
	public static native int GetTime(byte[] buf);

	// 鑾峰彇鐗堟湰鍙� May 25 2017 09:48:16
	public static native String GetVesion();

	/*
	 * 签到解包 packString 签到返回字符串 返回值 0解包出错，1解包成功
	 */
	public static native int QapassSignPack(String packString);

	public static native int QapassSignUnPack(String data); // 签到解包

	public static native String QapassDectUnPack(String data); // 消费解包

}
