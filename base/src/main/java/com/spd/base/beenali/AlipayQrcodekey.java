package com.spd.base.beenali;

import android.support.annotation.NonNull;

import com.spd.base.db.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class AlipayQrcodekey {


    /**
     * keyType : AliPay
     * publicKeyList : [{"key_id":22,"pub_key":"02DF95E6C7491E0F90A2322075BD973FBCB2D163B92623BBE153F65814583F17C9"},{"key_id":14,"pub_key":"0366B8E3A4BBE12ED11C54B48A9E9B724A07CF8E98F78522B23EFD95D6A2EC00DD"},{"key_id":3,"pub_key":"03B41B517513E03EBEE3DB4D57594E5A0F19688E9A5067AB23B6C61366634AF572"},{"key_id":5,"pub_key":"0275FE4E714229B1732E2CFDA44011F6718E9E2261B74AE3886C667D23AB21EB7F"},{"key_id":19,"pub_key":"039CB571D5C1398F340D01C380BE676B51B7BA4DACED0D22879A27403BB3F49D59"},{"key_id":26,"pub_key":"029AD06148C81E0025CB1685591513BE657A6A9A9BE9E83B2EE110221B26306EFC"},{"key_id":9,"pub_key":"0230866CB7FA9800E779E873B8E9786295E851CF327905FF290123B91F2E4E4DE3"},{"key_id":24,"pub_key":"03CA2BFD5C6B52D0E8D826378EF23A01839D36C60F76ECDB1BCC0B2E55E04251F0"},{"key_id":10,"pub_key":"03B469F0764B2CB93FAEDD604FA1CECB7AA33AAC6E217E06A4D6FA86749A51E6CC"},{"key_id":21,"pub_key":"028DC1E334FA617A711B9E5A5060E29DADA03AF0DB642B9264903224A3A92509A2"},{"key_id":29,"pub_key":"034BFD4A1E3E39C51BCC60E62DD3F80D8392A4418155309E50F2CDD94985FAD026"},{"key_id":6,"pub_key":"02D4ABD6277C42125EC93811FB75F02E8D29CE19F17B0118A06FB64DDDAD8F151D"},{"key_id":17,"pub_key":"02C06EA706FCC49F200BAE60FE5F85C519EE21620821B370CB815AB849BE22A9D5"},{"key_id":28,"pub_key":"0379BE1176150C83D256F020D5B0A409A7615C99EBC7070F27E762900DA760F32F"},{"key_id":23,"pub_key":"0399F5E924A3C5B8ECFE6F3D1BE9B7C176BEBE6857F428849CB8E8BCDCE689D827"},{"key_id":1,"pub_key":"02EA95A096BB5BE9693635DCD2231D210E15B8803C10FFE5293B29A67251C3605B"},{"key_id":25,"pub_key":"02557D5AF16A345815C2F3896535A7F969AA7BE9F0A300386FF0D637A7891001B0"},{"key_id":4,"pub_key":"0282ABB881685EC8082B816C84F0AF7BA714674B027617AA9C0BEA540F50B61245"},{"key_id":2,"pub_key":"038824D92AEFA2B1EE8F349FA2C38DBB0D0EAB057B99FF2F1899BBFA1F29F1162B"},{"key_id":7,"pub_key":"03C833BCD6FE464F2F923D3B87C4945E7F661D686A379B454306C79000ED3778B1"},{"key_id":27,"pub_key":"03871839999D6003D907E9884DC18928261845480C213E480D530E03CE46084087"},{"key_id":18,"pub_key":"03383F60535555C5277BC5163F087C7EAE0D79CCC86B9DB96565D9A333404D638C"},{"key_id":0,"pub_key":"02AB2FDB0AB23506C48012642E1A572FC46B5D8B8EE3B92A602CC1109921F84B0E"},{"key_id":12,"pub_key":"0310109AED9B06D38AF10FA1729119E05C465578A3ADBCDFBEDDA723112E2DB13C"},{"key_id":8,"pub_key":"0270E69E1719C835C307D16050141E0527DD5B99E025F7D5A91AEAB6DF95DC7606"},{"key_id":20,"pub_key":"03F6677074424BF61A9EF90663D91D9CC97A02E5462D0386FBCBED7FC111ECF12A"},{"key_id":11,"pub_key":"0365C74E6B3D53D2742CB7EF60DA78F3931BE28AC12E25DF05C449CD04D0AE096F"},{"key_id":30,"pub_key":"03999A50E628878F0670DBBFE20F98919E4FEEBB00CFED98F0867C5313546693D4"},{"key_id":13,"pub_key":"034CB34A4D15F9114A3868CC6B6C9003F89682A8C45D8BBE44BD388CC0BDA2C1CE"},{"key_id":15,"pub_key":"02E7079580CE071A928446E81428EC4873A2F8C879311687A466FDACD13F9DD29E"},{"key_id":31,"pub_key":"03079B27692B20A315D95A48DA2F7F2CCC0B80723F8A65DD13B3999706DE75F559"},{"key_id":16,"pub_key":"02396D50BBCF15A40D3BBD06F7D7763D3E5795128F3FB1FEC0C95085CC1FE636E1"}]
     * version : 1
     */
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String keyType;
    @Unique
    private int version;
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> pubkeyDbList;
    //不添加数据库
    @Transient
    private List<PublicKeyListBean> publicKeyList;

    @Generated(hash = 2121149846)
    public AlipayQrcodekey(Long id, String keyType, int version, List<String> pubkeyDbList) {
        this.id = id;
        this.keyType = keyType;
        this.version = version;
        this.pubkeyDbList = pubkeyDbList;
    }

    @Generated(hash = 849140770)
    public AlipayQrcodekey() {
    }

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

    public List<PublicKeyListBean> getPublicKeyList() {
        return publicKeyList;
    }

    public void setPublicKeyList(List<PublicKeyListBean> publicKeyList) {
        this.publicKeyList = publicKeyList;
    }

    public static class PublicKeyListBean implements Comparable<PublicKeyListBean> {
        /**
         * key_id : 22
         * pub_key : 02DF95E6C7491E0F90A2322075BD973FBCB2D163B92623BBE153F65814583F17C9
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
        public int compareTo(@NonNull PublicKeyListBean o) {
            return this.key_id - o.getKey_id();
        }

        @Override
        public String toString() {
            return "PublicKeyListBean{" +
                    "key_id=" + key_id +
                    ", pub_key='" + pub_key + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AlipayQrcodekey{" +
                "keyType='" + keyType + '\'' +
                ", version=" + version +
                ", publicKeyList=" + publicKeyList +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPubkeyDbList() {
        return this.pubkeyDbList;
    }

    public void setPubkeyDbList(List<String> pubkeyDbList) {
        this.pubkeyDbList = pubkeyDbList;
    }
}
