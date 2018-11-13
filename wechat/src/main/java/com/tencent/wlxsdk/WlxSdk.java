package com.tencent.wlxsdk;

import android.text.TextUtils;
import java.util.Locale;

/*
verifyQrCode()函数返回值

EC_SUCCESS			0		成功
EC_FORMAT			-10000	二维码格式错误
EC_CARD_PUBLIC_KEY 	-10001	卡证书公钥错误
EC_CARD_CERT 		-10002	卡证书验签失败
EC_USER_PUBLIC_KEY 	-10003	卡证书用户公钥错误
EC_USER_SIGN 		-10004	二维码验签错误
EC_CARD_CERT_TIME 	-10005	卡证书过期
EC_CODE_TIME 		-10006	二维码过期
EC_FEE 				-10007	超过最大金额
EC_BALANCE 			-10008	余额不足
EC_OPEN_ID 			-10009	输入的open_id不匹配
EC_PARAM_ERR 		-10010	参数错误
EC_MEM_ERR 			-10011	内存申请错误
EC_CARD_CERT_SIGN_ALG_NOT_SUPPORT 	-10012	卡证书签名算法不支持
EC_MAC_ROOT_KEY_DECRYPT_ERR 		-10013	加密的mac根密钥解密失败
EC_MAC_SIGN_ERR 					-10014	mac校验失败
EC_QRCODE_SIGN_ALG_NOT_SUPPORT 		-10015	二维码签名算法不支持
EC_SCAN_RECORD_ECRYPT_ERR 			-10016	扫码记录加密失败
EC_SCAN_RECORD_ECODE_ERR 			-10017	扫码记录编码失败

EC_FAIL 			-20000		其它错误

*/

public class WlxSdk {
	static {
		System.loadLibrary("wlxsdkcore");
	}

	//进出站标识
	private static final int ENTER = 1;
	private static final int EXIT = 2;

	//扫码记录
	private byte[] record = null;
	private byte[] open_id = null;
	private int key_id = -1;
	private byte[] ver_info = null;

	private byte[] mac_root_id = null;

	private byte[] biz_data = null;

	//二维码hh
	private String qrcode;

	private native int getQrCodeElemets(String qrcode);
	private native int verifyQrCode(String qrcode, String open_id, String pub_key, int payfee, byte scene,  byte scantype, String pos_id, String pos_trx_id, String aes_mac_root);
	private native int getVersionInfo();

	public int init(String qrcode) {
		if (TextUtils.isEmpty(qrcode)) {
			return -1;
		}
		this.qrcode = qrcode;
		return getQrCodeElemets(qrcode);
	}

	public int get_key_id() {
		return key_id;
	}

	public String get_mac_root_id() {
		return mac_root_id != null ? new String(mac_root_id) : "";
	}

	public String get_open_id() {
		return open_id != null ? new String(open_id) : "";
	}

	public String get_biz_data() {
		return biz_data != null ? new String(biz_data) : "";
	}

	public String get_biz_data_hex(){
		return biz_data != null ? bytesToHexString(biz_data) : "";
	}

	/**
	 * @param open_id
	 * @param pub_key
	 * @param payfee  实际扣款金额（扣除优惠），单位是分
	 * @param scene 场景：1公交，2地铁
	 * @param scantype  扫码类型：1进站 2出站
	 * @param pos_id 机具ID
	 * @param pos_trx_id  机具流水号
	 * @return
	 */
	public int verify(String open_id, String pub_key,int payfee, byte scene,  byte scantype, String pos_id, String pos_trx_id, String aes_mac_root) {
		if (TextUtils.isEmpty(this.qrcode) || TextUtils.isEmpty(open_id) || TextUtils.isEmpty(pub_key) || payfee < 0 || (scantype != ENTER && scantype != EXIT) ) {
			return -1;
		}

		return verifyQrCode(qrcode, open_id, pub_key, payfee, scene, scantype, pos_id, pos_trx_id,aes_mac_root);
	}

	public String get_record() {
		return record != null ? new String(record) : "";
	}

	public String getVerInfo() {
		if (0 == getVersionInfo()) {
			return ver_info != null ? new String(ver_info) : "";
		}
		return "";
	}

	/**
	 * byte数组转十六进制字符串，大端
	 * @param input
	 * @return
	 */
	public static String bytesToHexString(byte[] input) {
		if (input == null) {
			return "";
		}

		return bytesToHexString(input, 0, input.length);
	}

	/**
	 * byte数组转十六进制字符串，大端
	 * @param input
	 * @param start
	 * @param n
	 * @return
	 */
	public static String bytesToHexString(byte[] input, int start, int n) {
		if (input == null || start < 0 || n < 0 || input.length < start + n) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			sb.append(String.format(Locale.getDefault(), "%02X", input[i]));
		}

		return sb.toString();
	}
}
