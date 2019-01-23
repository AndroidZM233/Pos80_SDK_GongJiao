package com.spd.base.dbbeen;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AlipayKeyDb {
    @Id(autoincrement = true)
    private Long id;
    private int version;
    private String keyType;
    private String pubkeyId;
    private String pubKey;

    public AlipayKeyDb(int version, String keyType, String pubkeyId, String pubKey) {
        this.version = version;
        this.keyType = keyType;
        this.pubkeyId = pubkeyId;
        this.pubKey = pubKey;
    }

    @Generated(hash = 2001501070)
    public AlipayKeyDb(Long id, int version, String keyType, String pubkeyId,
                       String pubKey) {
        this.id = id;
        this.version = version;
        this.keyType = keyType;
        this.pubkeyId = pubkeyId;
        this.pubKey = pubKey;
    }

    @Generated(hash = 1627622546)
    public AlipayKeyDb() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getKeyType() {
        return this.keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
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
