package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
//银联公钥
@Table(name = "unionkey", id = "Id")
public class UnionQrKey extends Model {
	@Column(name = "cert_format")
	private String cert_format;
	@Column(name = "org_id")
	private String org_id;
	@Column(name = "cert_expire_time")
	private String cert_expire_time;
	@Column(name = "cert_no")
	private String cert_no;
	@Column(name = "sign_algorithm")
	private String sign_algorithm;
	@Column(name = "encrypt_algorithm")
	private String encrypt_algorithm;
	@Column(name = "parameter_id")
	private String parameter_id;
	@Column(name = "public_key")
	private String public_key;
	@Column(name = "cert_sign")
	private String cert_sign;
	@Column(name = "cert_seq")
	private String cert_seq;
	@Column(name = "publickey_length")
	private String publickey_length;
  
	public String getCert_format() {
		return cert_format;
	}

	public void setCert_format(String cert_format) {
		this.cert_format = cert_format;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getCert_expire_time() {
		return cert_expire_time;
	}

	public void setCert_expire_time(String cert_expire_time) {
		this.cert_expire_time = cert_expire_time;
	}

	public String getCert_no() {
		return cert_no;
	}

	public void setCert_no(String cert_no) {
		this.cert_no = cert_no;
	}

	public String getSign_algorithm() {
		return sign_algorithm;
	}

	public void setSign_algorithm(String sign_algorithm) {
		this.sign_algorithm = sign_algorithm;
	}

	public String getEncrypt_algorithm() {
		return encrypt_algorithm;
	}

	public void setEncrypt_algorithm(String encrypt_algorithm) {
		this.encrypt_algorithm = encrypt_algorithm;
	}

	public String getParameter_id() {
		return parameter_id;
	}

	public void setParameter_id(String parameter_id) {
		this.parameter_id = parameter_id;
	}

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public String getCert_sign() {
		return cert_sign;
	}

	public void setCert_sign(String cert_sign) {
		this.cert_sign = cert_sign;
	}

	public String getCert_seq() {
		return cert_seq;
	}

	public void setCert_seq(String cert_seq) {
		this.cert_seq = cert_seq;
	}

	public String getPublickey_length() {
		return publickey_length;
	}

	public void setPublickey_length(String publickey_length) {
		this.publickey_length = publickey_length;
	}

}
