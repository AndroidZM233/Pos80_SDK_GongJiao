package com.spd.base.been.tianjin.produce.shuangmian;

import com.spd.base.been.tianjin.produce.weixin.PayinfoBean;

import java.util.List;

/**
 * 银联双免
 * Created by 张明_ on 2019/4/17.
 * Email 741183142@qq.com
 */
public class ProduceShuangMian {
    private List<ShuangMianBean> payinfo;

    public List<ShuangMianBean> getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(List<ShuangMianBean> payinfo) {
        this.payinfo = payinfo;
    }
}
