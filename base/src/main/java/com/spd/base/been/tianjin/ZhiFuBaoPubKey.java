package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/3/12.
 * Email 741183142@qq.com
 */
@Entity
public class ZhiFuBaoPubKey {

    /**
     * public_key : 02AB2FDB0AB23506C48012642E1A572FC46B5D8B8EE3B92A602CC1109921F84B0E
     * key_id : 0
     */

    private String public_key;

    private int key_id;

    @Generated(hash = 1583335828)
    public ZhiFuBaoPubKey(String public_key, int key_id) {
        this.public_key = public_key;
        this.key_id = key_id;
    }

    @Generated(hash = 222957925)
    public ZhiFuBaoPubKey() {
    }

    public String getPublic_key() {
        return this.public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public int getKey_id() {
        return this.key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }


}
