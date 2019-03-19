package com.spd.base.been.tianjin;

import java.util.List;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
public class UnqrkeyBackBean {

    /**
     * code : 00
     * last_time : 20171201012500
     * keys : [{"public_key":"LiNTcxSdh91LnjVKa6YiRmhslzbwqOKq3/t0L29oqKGJHNka7Fs2IhwYAyeVe/Dsi92kOEkLizZFE/hpRg83gg==","sign_algorithm":"04","cert_no":"1001","cert_seq":"101181","org_id":"A0000001","publickey_length":"88","encrypt_algorithm":"00","cert_format":"12","cert_sign":"Yr42PQ445bdcb/sUFCRdk4wA3mCGEnWQ4izwFp67m91zxpOya7igPInatFD/mhC7V1bHMGVjPuSI10hDB+3nuA==","cert_expire_time":"1250","parameter_id":"11"},{"public_key":"jOiWwkTAIERZPfuOHpMsn3N+ms8SXPnWzLoDfQ+XWPqfaoAR2XpIgN3MP6XMShNdpcSOst7YLm9BI/a5u7zj+Q==","sign_algorithm":"04","cert_no":"1002","cert_seq":"101177","org_id":"A0000001","publickey_length":"88","encrypt_algorithm":"00","cert_format":"12","cert_sign":"Fk51Keu8H/1xgaDm5ey1gGMPrrUP9MLHtn2QE+Kfm1crRgo2dwKF5CLDIfL0uID4ov9oCVsYhVpxi2qw1uPfIg==","cert_expire_time":"1250","parameter_id":"11"},{"public_key":"SqXm4wkf21r7QLxPCHBvwmsuHAYqtBXPER6QgCT8nELi0YJzSEWCsXHEFHJZf3YBDDzlAwoe6b8vsPHPqvea3g==","sign_algorithm":"04","cert_no":"1003","cert_seq":"101189","org_id":"A0000001","publickey_length":"88","encrypt_algorithm":"00","cert_format":"12","cert_sign":"lmBWeMrDkvqvhJs/wOFBiI24hHmCWFxLo5AHFowkThY12A7wuSG+4ihjWJhMmZLx6Ms+AaMEWdp0l9d93RO/kQ==","cert_expire_time":"1250","parameter_id":"11"},{"public_key":"d7ZcFOoOrS7krskxi9D2un+Fh5DxvrY54jTUAbeYZwNN2CerSgvRPyPE9uJAv/SA7PhOhxR9iHhr1fBEC2X3bg==","sign_algorithm":"04","cert_no":"1004","cert_seq":"101185","org_id":"A0000001","publickey_length":"88","encrypt_algorithm":"00","cert_format":"12","cert_sign":"2QEJMqTDMp02+3lFkC7lWiaoQp2gmlhF1t8rBh7GYMqzn0WaWC3KDdYSaG2HbFRPx5Hf/LHaDMNRyyIpgIvAjA==","cert_expire_time":"1250","parameter_id":"11"},{"public_key":"GqVIh+X299DlV6MGGTbwGwEvp0Ofh2y52gzjACKXHp/E5wvaDa2OeX3qvTIetEe3Za/ZFpqHZAWCl+6lJBBmig==","sign_algorithm":"04","cert_no":"1005","cert_seq":"101197","org_id":"A0000001","publickey_length":"88","encrypt_algorithm":"00","cert_format":"12","cert_sign":"ED/oSxeeZSKdEyNx90PrBAUc/CvRBA8n2w/u80aCmDfyMDQsyYKziqNxOo2x8S/ogbBZoNEBWtzxqhHtBTPtOw==","cert_expire_time":"1250","parameter_id":"11"}]
     */

    private String code;
    private String last_time;
    private List<KeysBean> keys;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public List<KeysBean> getKeys() {
        return keys;
    }

    public void setKeys(List<KeysBean> keys) {
        this.keys = keys;
    }

}
