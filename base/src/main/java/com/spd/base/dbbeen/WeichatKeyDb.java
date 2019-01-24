package com.spd.base.dbbeen;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WeichatKeyDb {
    @Id(autoincrement = false)
    String id;
    private int curVersion;
    private String keyType;
    private String mackeyId;
    private String macKey;
    private String pubkeyId;
    private String pubKey;
    @Generated(hash = 2065192450)
    public WeichatKeyDb(String id, int curVersion, String keyType, String mackeyId,
            String macKey, String pubkeyId, String pubKey) {
        this.id = id;
        this.curVersion = curVersion;
        this.keyType = keyType;
        this.mackeyId = mackeyId;
        this.macKey = macKey;
        this.pubkeyId = pubkeyId;
        this.pubKey = pubKey;
    }
    @Generated(hash = 1156755244)
    public WeichatKeyDb() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getCurVersion() {
        return this.curVersion;
    }
    public void setCurVersion(int curVersion) {
        this.curVersion = curVersion;
    }
    public String getKeyType() {
        return this.keyType;
    }
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
    public String getMackeyId() {
        return this.mackeyId;
    }
    public void setMackeyId(String mackeyId) {
        this.mackeyId = mackeyId;
    }
    public String getMacKey() {
        return this.macKey;
    }
    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }
    public String getPubkeyId() {
        return this.pubkeyId;
    }
    public void setPubkeyId(String pubkeyId) {
        this.pubkeyId = pubkeyId;
    }
    public String getPubKey() {
        return this.pubKey;
    }
    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

}
