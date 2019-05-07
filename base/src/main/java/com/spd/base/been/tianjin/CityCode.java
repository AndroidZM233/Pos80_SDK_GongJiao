package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/5/6.
 * Email 741183142@qq.com
 */
@Entity
public class CityCode {

    /**
     * Id : 1
     * city : 邯郸
     * created_at : 2019-04-021 10:42:37
     * is_available : 1
     * issuer_code : 1581270
     * updated_at : 2019-04-021 10:42:37
     */
    private int Id;
    private String city;
    private String created_at;
    private String is_available;
    private String issuer_code;
    private String updated_at;
    @Generated(hash = 478306877)
    public CityCode(int Id, String city, String created_at, String is_available,
            String issuer_code, String updated_at) {
        this.Id = Id;
        this.city = city;
        this.created_at = created_at;
        this.is_available = is_available;
        this.issuer_code = issuer_code;
        this.updated_at = updated_at;
    }
    @Generated(hash = 614948543)
    public CityCode() {
    }
    public int getId() {
        return this.Id;
    }
    public void setId(int Id) {
        this.Id = Id;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCreated_at() {
        return this.created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getIs_available() {
        return this.is_available;
    }
    public void setIs_available(String is_available) {
        this.is_available = is_available;
    }
    public String getIssuer_code() {
        return this.issuer_code;
    }
    public void setIssuer_code(String issuer_code) {
        this.issuer_code = issuer_code;
    }
    public String getUpdated_at() {
        return this.updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    
}
