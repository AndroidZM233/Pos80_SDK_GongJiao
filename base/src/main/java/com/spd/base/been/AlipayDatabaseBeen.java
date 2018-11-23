package com.spd.base.been;

import android.support.annotation.Keep;
import android.text.TextUtils;

import com.spd.base.beenali.AlipayQrcodekey;
import com.spd.base.beenbosi.BosiQrcodeKey;
import com.spd.base.beenwechat.WechatQrcodeKey;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.converter.PropertyConverter;

@Entity
public class AlipayDatabaseBeen {
    @Id
    long id = 0;

    private String keyType;

    @Index
    private int version;

    @Index int keyId;

    @Index
    private String key;


    public AlipayDatabaseBeen() {
    }

    public AlipayDatabaseBeen(String keyType, int version, int keyId, String key) {
        this.keyType = keyType;
        this.version = version;
        this.keyId = keyId;
        this.key = key;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //    @Convert(converter = StringConverter.class, dbType = String.class)
//    private List<String> aliKeyListBeans;
//
//    public List<String> getAliKeyListBeans() {
//        return aliKeyListBeans;
//    }
//
//    public void setAliKeyListBeans(List<String> aliKeyListBeans) {
//        this.aliKeyListBeans = aliKeyListBeans;
//    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

//    public static class RoleConverter implements PropertyConverter<List<AlipayQrcodekey.PublicKeyListBean>, String> {
//
//        @Override
//        public List<AlipayQrcodekey.PublicKeyListBean> convertToEntityProperty(String databaseValue) {
//            if (databaseValue == null) {
//                return null;
//            } else {
//////                List<String> list = Arrays.asList(databaseValue.split(","));
//                List<AlipayQrcodekey.PublicKeyListBean> publicKeyListBeans = new ArrayList<>();
//
//                for (int i = 0; i < publicKeyListBeans.size(); i++) {
//                    publicKeyListBeans.get(i).setKey_id(i);
//                    publicKeyListBeans.get(i).setPub_key(databaseValue);
//                }
//                return publicKeyListBeans;
//            }
//
//        }
//
//        @Override
//        public String convertToDatabaseValue(List<AlipayQrcodekey.PublicKeyListBean> entityProperty) {
//            if (entityProperty == null) {
//                return null;
//            } else {
//                for (int i = 0; i < entityProperty.size(); i++) {
//                    return entityProperty.get(i).getPub_key();
//                }
//                return "";
//            }
//        }
//    }
//
//    //    {
////        @Override
////        public Role convertToEntityProperty(Integer databaseValue) {
////            if (databaseValue == null) {
////                return null;
////            }
////            for (Role role : Role.values()) {
////                if (role.id == databaseValue) {
////                    return role;
////                }
////            }
////            return Role.DEFAULT;
////        }
////​
////        @Override
////        public Integer convertToDatabaseValue(Role entityProperty) {
////            return entityProperty == null ? null : entityProperty.id;
////        }
////    }
//    public static class StringConverter implements PropertyConverter<List<String>, String> {
//
//        @Override
//        public List<String> convertToEntityProperty(String databaseValue) {
//            if (databaseValue == null) {
//                return null;
//            } else {
//                List<String> list = Arrays.asList(databaseValue.split(","));
//                return list;
//            }
//        }
//
//        @Override
//        public String convertToDatabaseValue(List<String> entityProperty) {
//            if (entityProperty == null) {
//                return null;
//            } else {
//                StringBuilder sb = new StringBuilder();
//                for (String link : entityProperty) {
//                    sb.append(link);
//                    sb.append(",");
//                }
//                return sb.toString();
//            }
//        }
//    }

//    @Convert(converter = ServicesConverter.class, dbType = String.class)
//    private String[] aliKeyListBeans;
//
//    public String[] getAliKeyListBeans() {
//        return aliKeyListBeans;
//    }
//
//    public void setAliKeyListBeans(String[] aliKeyListBeans) {
//        this.aliKeyListBeans = aliKeyListBeans;
//    }
//
//    public static class ServicesConverter implements PropertyConverter<String[], String> {
//
//        @Override
//        public String[] convertToEntityProperty(String s) {
//            if (TextUtils.isEmpty(s)) {
//                return null;
//            } else {
//                return s.split(",");
//            }
//        }
//
//        @Override
//        public String convertToDatabaseValue(String[] strings) {
//            if (strings != null && strings.length > 0) {
//                StringBuilder builder = new StringBuilder();
//                for (int i = 0; i < strings.length; i++) {
//                    if (i > 0) {
//                        builder.append(",");
//                    }
//                    builder.append(strings[i]);
//                }
//                return builder.toString();
//            }
//            return null;
//        }
//    }


}
//@Entity
//class WechatDatabaseBeen {
//    @Id
//    long id = 0;
//    private String keyType;
//    @Index
//    private int version;
//
////    private List<WechatQrcodeKey.MacKeyListBean> wechatMacKeyListBeans;
////    private List<WechatQrcodeKey.PubKeyListBean> wechatPubKeyListBeans;
//
//
//}
//@Entity
//class BosiDatabaseBeen{
//    @Id
//    long id = 0;
//    private String keyType;
//    @Index
//    private int version;
////    private List<BosiQrcodeKey.KeyListBean> bosiKeyListBeen;
//
//}
