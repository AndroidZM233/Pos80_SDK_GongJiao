/**  
 * Project Name:Q6  
 * File Name:libtest.java  
 * Package Name:com.szxb.jni  
 * Date:Apr 13, 20177:37:45 PM  
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.  
 *  
 */

package com.szxb.jni;

import android.content.res.AssetManager;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * ClassName:libtest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: Apr 13, 2017 7:37:45 PM <br/>
 * 
 * @author lilei
 * @version
 * @since JDK 1.6
 * @see
 */
public class libtest {

//	static {
//		try {
//			System.loadLibrary("business");
//		} catch (Throwable e) {
//			Log.e("jni", "i can't find business so!");
//			e.printStackTrace();
//		}
//	}

	static {
		try {
			System.loadLibrary("ymodem");
		} catch (Throwable e) {
			Log.e("jni", "i can't find ymodem so!");
			e.printStackTrace();
		}
	}

	public static native String mytestrfid();

	public static native String mytestbarcode();

	public static native String mytestpsam();

	public static native int mytestkey(byte[] recv);

	public static native int mytestno();

	public static native int mytesttrans(byte[] recv);

	public static native int mytime(byte[] buf, boolean flag);

	public static int settime(int year, int month, int date, int hour, int min,
			int sec) {

		byte[] settime = new byte[8];

		settime[0] = (byte) ((year >> 8) & 0xff);
		settime[1] = (byte) (year & 0xff);
		settime[2] = (byte) (month & 0xff);
		settime[3] = (byte) (date & 0xff);
		settime[4] = (byte) (hour & 0xff);
		settime[5] = (byte) (min & 0xff);
		settime[6] = (byte) (sec & 0xff);

		return libtest.mytime(settime, true);
	}

	public static Calendar gettime() {
		int ret;
		byte[] gettime = new byte[8];

		ret = libtest.mytime(gettime, false);
		if (0 == ret) {
			int year = ((gettime[0] << 8) & 0xff00) | (gettime[1] & 0xff);

			return new GregorianCalendar(year, gettime[2], gettime[3],
					gettime[4], gettime[5], gettime[6]);
		}
		return null;

	}

	public static native int ymodemUpdate(AssetManager ass, String filename);

	// public static void setDateTime(int year, int month, int day, int hour,
	// int minute) throws IOException, InterruptedException {
	//
	// Calendar c = Calendar.getInstance();
	//
	// c.set(Calendar.YEAR, year);
	// c.set(Calendar.MONTH, month-1);
	// c.set(Calendar.DAY_OF_MONTH, day);
	// c.set(Calendar.HOUR_OF_DAY, hour);
	// c.set(Calendar.MINUTE, minute);
	//
	//
	// long when = c.getTimeInMillis();
	//
	// if (when / 1000 < Integer.MAX_VALUE) {
	// SystemClock.setCurrentTimeMillis(when);
	// }
	//
	// long now = Calendar.getInstance().getTimeInMillis();
	// //Log.d(TAG, "set tm="+when + ", now tm="+now);
	//
	// if(now - when > 1000)
	// throw new IOException("failed to set Date.");
	//
	// }

	public native static String MifareGetSNR(byte[] cardType);

	public native static String TypeA_RATS();

	public native static String[] RFID_APDU(String sendApdu);

//	static {
//		try {
//			System.loadLibrary("halcrypto");
//		} catch (Throwable e) {
//			Log.e("jni", "i can't find halcrypto so!");
//			e.printStackTrace();
//		}
//	}

	public static native String RSA_public_decrypt(String strN, String sInput,
                                                   int e);

	public static native String Hash1(String inputStr);

	public static native String Hash224(String inputStr);

}
