package com.wechat.been;

import java.util.List;

public class WechatPublicKey {
//     {"curVersion":1,"macKeyList":[{"mac_key":"E7BBD0BD7769B1BF73B5AE07A2D5D733","key_id":"1"},{"mac_key":"B431AF7CD446C6221ACF9395B50C8C8E","key_id":"2"},{"mac_key":"C3F6FD41C4A7F558AD5789DE4B474DE0","key_id":"3"},{"mac_key":"EF9BF841012ACDF8D09322873B24EA70","key_id":"4"},{"mac_key":"97CBB4CAB13D91D8AB73B9FA5641A9F6","key_id":"5"},{"mac_key":"7C040960A8794D8D6EA4FCD764FEDB73","key_id":"6"},{"mac_key":"43100ADE5632616F2629070779EB07E5","key_id":"7"},{"mac_key":"C5E2A32E8EC50CFDF1A8721D73C2F625","key_id":"8"}],"pubKeyList":[{"key_id":1,"pub_key":"0480A3D8999F4AC3F3CB2129B1DDD88CACFB1DEB041358F28FD9ECE108648B92878FB199B12FDFE412574D8E5BF664BC29FEF43E781FB4BA2D"},{"key_id":2,"pub_key":"0403142215E227D8B3B8EB9F73A713963C57313E318DD8C9ED81582E12DB8D2087DAA9E93E0E18C73031432842794B42694D57EA5C2FCDB38C"}]}

     private List<MacKeyListBean> macKeyList;
     private List<PubKeyListBean> pubKeyList;

     public List<MacKeyListBean> getMacKeyList() {
          return macKeyList;
     }

     public void setMacKeyList(List<MacKeyListBean> macKeyList) {
          this.macKeyList = macKeyList;
     }

     public List<PubKeyListBean> getPubKeyList() {
          return pubKeyList;
     }

     public void setPubKeyList(List<PubKeyListBean> pubKeyList) {
          this.pubKeyList = pubKeyList;
     }

     public static class MacKeyListBean {
          /**
           * mac_key : D0868DE36E903A637143C0982FBCA2FC
           * key_id : 2
           */

          private String mac_key;
          private String key_id;

          public String getMac_key() {
               return mac_key;
          }

          public void setMac_key(String mac_key) {
               this.mac_key = mac_key;
          }

          public String getKey_id() {
               return key_id;
          }

          public void setKey_id(String key_id) {
               this.key_id = key_id;
          }

          @Override
          public String toString() {
               return "MacKeyListBean{" +
                       "mac_key='" + mac_key + '\'' +
                       ", key_id='" + key_id + '\'' +
                       '}';
          }
     }

     public static class PubKeyListBean {
          /**
           * key_id : 1
           * pub_key : 04535F8AF4CE0B24AB3F6D66D9C662764A994C6262A1CF12F63AC216005D722D12450C1E1C03220DEF8D0A78224A8BEBF1347F5661F009EA4C
           */

          private int key_id;
          private String pub_key;

          public int getKey_id() {
               return key_id;
          }

          public void setKey_id(int key_id) {
               this.key_id = key_id;
          }

          public String getPub_key() {
               return pub_key;
          }

          public void setPub_key(String pub_key) {
               this.pub_key = pub_key;
          }

          @Override
          public String toString() {
               return "PubKeyListBean{" +
                       "key_id=" + key_id +
                       ", pub_key='" + pub_key + '\'' +
                       '}';
          }
     }
}
