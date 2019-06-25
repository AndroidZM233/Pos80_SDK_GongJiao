package com.spd.bus.util;

import android.util.Log;

public class HzjString {

	// **
	public static final String bytesToHexString1(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
	String sTemp;
	for (int i = 0; i < bArray.length; i++) {
	sTemp = Integer.toHexString(0xFF & bArray[i]);
	if (sTemp.length() < 2) {
		sb.append(0);
	}
	sb.append(sTemp.toUpperCase());
	}
	return sb.toString();
	}

	// 将字节数组转换为16进制字符串
	public static String BinaryToHexString(byte[] bytes) {
	String hexStr = "0123456789ABCDEF";
	String result = "";
	String hex = "";
	for (byte b : bytes) {
	hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
	hex += String.valueOf(hexStr.charAt(b & 0x0F));
	result += hex + "";
	}
	return result;
	}

	public static byte[] hexStringToBytes(String hexString) {
	if (hexString == null || hexString.equals("")) {
	return null;
	}
	// toUpperCase将字符串中的所有字符转换为大写
	hexString = hexString.toUpperCase();
	int length = hexString.length() / 2;
	// toCharArray将此字符串转换为一个新的字符数组。
	char[] hexChars = hexString.toCharArray();
	byte[] d = new byte[length];
	for (int i = 0; i < length; i++) {
	int pos = i * 2;
	d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
	}
	return d;
	}

	// charToByte返回在指定字符的第一个发生的字符串中的索引，即返回匹配字符
	private static byte charToByte(char c) {
	return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 十六进制转换字符串
	 *
	 * @param String
	 *            str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */

	public static String hexString2String(String src) {
		String temps = "";
		for (int i = 2; i < src.length() / 2; i++) {
			temps = temps
					+ (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
							16).byteValue();
		}
		Log.i("16进制转换:" + temps, "hzj");
		return temps;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr2(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			// sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param String
	 *            str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * bytes转换成十六进制字符串
	 * 
	 * @param byte[] b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m)
					+ src.substring(m, n));
		}
		return ret;
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String
	 *            strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128) {
				str.append("\\u" + strHex);
			} else
				// 低位在前面补00
			{
				str.append("\\u00" + strHex);
			}
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String
	 *            hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	public static boolean panDuan(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i+1) != "0") {
				return false;
			}
		}
		return true;
	}
}
