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

    private Long recordId;
    private byte[] record;
    private boolean isUpload;
    //司机记录
    private String busRecord;
    @Generated(hash = 720913872)
    public CardRecord(Long id, Long recordId, byte[] record, boolean isUpload,
            String busRecord) {
        this.id = id;
        this.recordId = recordId;
        this.record = record;
        this.isUpload = isUpload;
        this.busRecord = busRecord;
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
    public Long getRecordId() {
        return this.recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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
    public String getBusRecord() {
        return this.busRecord;
    }
    public void setBusRecord(String busRecord) {
        this.busRecord = busRecord;
    }
    

}
