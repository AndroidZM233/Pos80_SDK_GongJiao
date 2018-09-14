package com.example.test.yinlianbarcode.entity;

import java.util.List;

/**
 * @author :Reginer in  2018/7/25 14:27.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
public class PubKeyEntity {


    /**
     * success : true
     * errcode : 0000
     * errmsg : 成功
     * result : {"cert_num":5,"last_update_time":"20171204075400","cert":[{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1001","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"R87PEPeFJXnjgMyipeWdYUENO97c/yY9fLG8yJUtboexkx5sXpta4pcwVkz6NY+7OFPSqyFM+srzyuz6P6yFAA==","cert_sign":"vPdbG8UigLlveVF36fKLaUo2Szf3mJiLHNTVY6hZUsXvk/d0HGcjDLt0E4DfV662QSJ9J7H0snxId28QVp6KeQ==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1002","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"JiFlaK2xamc8a3q8xsSEn68zRY8QHwGlu6vUgf1zuhIfJOctoUkL8I+17ZCdMZlJ1BuRACKP4cVRHRq969joRA==","cert_sign":"PhyJSlqk2sTSOln0nhREIG5nPsZXq1jI2vHsrH9Kmku6P8cmqno+0fs2s5LDehGsGT+gfTZCcshaers5fyP3EQ==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1003","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"kF488BkwO1eFHfY5hIKugMDhkqrp8nLWHXw1Q1jER7tlv/wJ3lmTaZKCN/C88DtKVOXSlXYF4mmSd+PE4EKzRA==","cert_sign":"ifc99tBp112gAgOYXmSeITN+wcBThKJukkpRsQcqCFu6aSBQ09c82t0mSytKDWK/ivg3IV/WQ/eD8kA7IqzOgg==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1004","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"VsGN01Kv+7TJJghwwTKwK/OPF8dIDrMQSzcrFa/Q3INjxoN5DjdLn9xUeEZotvrj3DTwBWNcLdebz5F+uDE1eA==","cert_sign":"eInOaUtKFujWjwgQbIs9TuTtpF8z7gUevMIsxz41aJU+ebwP/XFEOFGlYi/gm8xJwE8Fn1sVIgFvee8yslgDDA==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1005","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"L9zrBePqubtzSOTC5+k74zpGAaUG7XurIZH6T4A+knub8Tftwa1ashn/3pGs3PiwQFCnOA1y5QG6JxP+0OkkYg==","cert_sign":"58Zld8YDIrpaW7NzoPmPe+0LB4XKsZPcDEu2U5Xnsc17nO/+65gDEzO2k5bIOlEu3fB78YBp1wY9EmE06xKWGQ==","cert_seq":"100028","publickey_length":"88"}]}
     */

    private boolean success;
    private String errcode;
    private String errmsg;
    private ResultEntity result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public static class ResultEntity {
        /**
         * cert_num : 5
         * last_update_time : 20171204075400
         * cert : [{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1001","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"R87PEPeFJXnjgMyipeWdYUENO97c/yY9fLG8yJUtboexkx5sXpta4pcwVkz6NY+7OFPSqyFM+srzyuz6P6yFAA==","cert_sign":"vPdbG8UigLlveVF36fKLaUo2Szf3mJiLHNTVY6hZUsXvk/d0HGcjDLt0E4DfV662QSJ9J7H0snxId28QVp6KeQ==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1002","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"JiFlaK2xamc8a3q8xsSEn68zRY8QHwGlu6vUgf1zuhIfJOctoUkL8I+17ZCdMZlJ1BuRACKP4cVRHRq969joRA==","cert_sign":"PhyJSlqk2sTSOln0nhREIG5nPsZXq1jI2vHsrH9Kmku6P8cmqno+0fs2s5LDehGsGT+gfTZCcshaers5fyP3EQ==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1003","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"kF488BkwO1eFHfY5hIKugMDhkqrp8nLWHXw1Q1jER7tlv/wJ3lmTaZKCN/C88DtKVOXSlXYF4mmSd+PE4EKzRA==","cert_sign":"ifc99tBp112gAgOYXmSeITN+wcBThKJukkpRsQcqCFu6aSBQ09c82t0mSytKDWK/ivg3IV/WQ/eD8kA7IqzOgg==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1004","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"VsGN01Kv+7TJJghwwTKwK/OPF8dIDrMQSzcrFa/Q3INjxoN5DjdLn9xUeEZotvrj3DTwBWNcLdebz5F+uDE1eA==","cert_sign":"eInOaUtKFujWjwgQbIs9TuTtpF8z7gUevMIsxz41aJU+ebwP/XFEOFGlYi/gm8xJwE8Fn1sVIgFvee8yslgDDA==","cert_seq":"100028","publickey_length":"88"},{"cert_format":"12","org_id":"A0000001","cert_expire_time":"1250","cert_no":"1005","sign_algorithm":"04","encrypt_algorithm":"00","parameter_id":"11","public_key":"L9zrBePqubtzSOTC5+k74zpGAaUG7XurIZH6T4A+knub8Tftwa1ashn/3pGs3PiwQFCnOA1y5QG6JxP+0OkkYg==","cert_sign":"58Zld8YDIrpaW7NzoPmPe+0LB4XKsZPcDEu2U5Xnsc17nO/+65gDEzO2k5bIOlEu3fB78YBp1wY9EmE06xKWGQ==","cert_seq":"100028","publickey_length":"88"}]
         */

        private int cert_num;
        private String last_update_time;
        private List<CertEntity> cert;

        public int getCert_num() {
            return cert_num;
        }

        public void setCert_num(int cert_num) {
            this.cert_num = cert_num;
        }

        public String getLast_update_time() {
            return last_update_time;
        }

        public void setLast_update_time(String last_update_time) {
            this.last_update_time = last_update_time;
        }

        public List<CertEntity> getCert() {
            return cert;
        }

        public void setCert(List<CertEntity> cert) {
            this.cert = cert;
        }

        public static class CertEntity {
            /**
             * cert_format : 12
             * org_id : A0000001
             * cert_expire_time : 1250
             * cert_no : 1001
             * sign_algorithm : 04
             * encrypt_algorithm : 00
             * parameter_id : 11
             * public_key : R87PEPeFJXnjgMyipeWdYUENO97c/yY9fLG8yJUtboexkx5sXpta4pcwVkz6NY+7OFPSqyFM+srzyuz6P6yFAA==
             * cert_sign : vPdbG8UigLlveVF36fKLaUo2Szf3mJiLHNTVY6hZUsXvk/d0HGcjDLt0E4DfV662QSJ9J7H0snxId28QVp6KeQ==
             * cert_seq : 100028
             * publickey_length : 88
             */

            private String cert_format;
            private String org_id;
            private String cert_expire_time;
            private String cert_no;
            private String sign_algorithm;
            private String encrypt_algorithm;
            private String parameter_id;
            private String public_key;
            private String cert_sign;
            private String cert_seq;
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

            @Override
            public String toString() {
                return "CertEntity{" +
                        "cert_format='" + cert_format + '\'' +
                        ", org_id='" + org_id + '\'' +
                        ", cert_expire_time='" + cert_expire_time + '\'' +
                        ", cert_no='" + cert_no + '\'' +
                        ", sign_algorithm='" + sign_algorithm + '\'' +
                        ", encrypt_algorithm='" + encrypt_algorithm + '\'' +
                        ", parameter_id='" + parameter_id + '\'' +
                        ", public_key='" + public_key + '\'' +
                        ", cert_sign='" + cert_sign + '\'' +
                        ", cert_seq='" + cert_seq + '\'' +
                        ", publickey_length='" + publickey_length + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "cert_num=" + cert_num +
                    ", last_update_time='" + last_update_time + '\'' +
                    ", cert=" + cert +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PubKeyEntity{" +
                "success=" + success +
                ", errcode='" + errcode + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", result=" + result +
                '}';
    }
}
