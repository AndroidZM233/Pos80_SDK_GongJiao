package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/3/13.
 * Email 741183142@qq.com
 */
@Entity
public class KeysBean {
    /**
     * public_key : LiNTcxSdh91LnjVKa6YiRmhslzbwqOKq3/t0L29oqKGJHNka7Fs2IhwYAyeVe/Dsi92kOEkLizZFE/hpRg83gg==
     * sign_algorithm : 04
     * cert_no : 1001
     * cert_seq : 101181
     * org_id : A0000001
     * publickey_length : 88
     * encrypt_algorithm : 00
     * cert_format : 12
     * cert_sign : Yr42PQ445bdcb/sUFCRdk4wA3mCGEnWQ4izwFp67m91zxpOya7igPInatFD/mhC7V1bHMGVjPuSI10hDB+3nuA==
     * cert_expire_time : 1250
     * parameter_id : 11
     */

    private String public_key;
    private String sign_algorithm;
    private String cert_no;
    private String cert_seq;
    private String org_id;
    private String publickey_length;
    private String encrypt_algorithm;
    private String cert_format;
    private String cert_sign;
    private String cert_expire_time;
    private String parameter_id;
    @Generated(hash = 705401426)
    public KeysBean(String public_key, String sign_algorithm, String cert_no, String cert_seq, String org_id,
            String publickey_length, String encrypt_algorithm, String cert_format, String cert_sign,
            String cert_expire_time, String parameter_id) {
        this.public_key = public_key;
        this.sign_algorithm = sign_algorithm;
        this.cert_no = cert_no;
        this.cert_seq = cert_seq;
        this.org_id = org_id;
        this.publickey_length = publickey_length;
        this.encrypt_algorithm = encrypt_algorithm;
        this.cert_format = cert_format;
        this.cert_sign = cert_sign;
        this.cert_expire_time = cert_expire_time;
        this.parameter_id = parameter_id;
    }
    @Generated(hash = 1081354765)
    public KeysBean() {
    }
    public String getPublic_key() {
        return this.public_key;
    }
    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }
    public String getSign_algorithm() {
        return this.sign_algorithm;
    }
    public void setSign_algorithm(String sign_algorithm) {
        this.sign_algorithm = sign_algorithm;
    }
    public String getCert_no() {
        return this.cert_no;
    }
    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }
    public String getCert_seq() {
        return this.cert_seq;
    }
    public void setCert_seq(String cert_seq) {
        this.cert_seq = cert_seq;
    }
    public String getOrg_id() {
        return this.org_id;
    }
    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }
    public String getPublickey_length() {
        return this.publickey_length;
    }
    public void setPublickey_length(String publickey_length) {
        this.publickey_length = publickey_length;
    }
    public String getEncrypt_algorithm() {
        return this.encrypt_algorithm;
    }
    public void setEncrypt_algorithm(String encrypt_algorithm) {
        this.encrypt_algorithm = encrypt_algorithm;
    }
    public String getCert_format() {
        return this.cert_format;
    }
    public void setCert_format(String cert_format) {
        this.cert_format = cert_format;
    }
    public String getCert_sign() {
        return this.cert_sign;
    }
    public void setCert_sign(String cert_sign) {
        this.cert_sign = cert_sign;
    }
    public String getCert_expire_time() {
        return this.cert_expire_time;
    }
    public void setCert_expire_time(String cert_expire_time) {
        this.cert_expire_time = cert_expire_time;
    }
    public String getParameter_id() {
        return this.parameter_id;
    }
    public void setParameter_id(String parameter_id) {
        this.parameter_id = parameter_id;
    }


}
