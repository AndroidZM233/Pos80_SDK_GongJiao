package com.spd.base.been.tianjin;

import org.apache.commons.lang3.text.StrBuilder;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 张明_ on 2019/3/23.
 * Email 741183142@qq.com
 */
@Entity
public class CardRecord {
    @Id
    private Long id;
    private byte[] record;
    private boolean isUpload;
    @Generated(hash = 522513063)
    public CardRecord(Long id, byte[] record, boolean isUpload) {
        this.id = id;
        this.record = record;
        this.isUpload = isUpload;
    }
    @Generated(hash = 19461391)
    public CardRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public byte[] getRecord() {
        return this.record;
    }
    public void setRecord(byte[] record) {
        this.record = record;
    }
    public boolean getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

}
