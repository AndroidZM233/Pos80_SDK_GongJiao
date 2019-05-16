package com.spd.bus.spdata.showdata.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spd.bus.R;
import com.spd.bus.spdata.been.XFBean;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by 张明_ on 2019/5/15.
 * Email 741183142@qq.com
 */
public class RVAdapter extends BaseQuickAdapter<XFBean, BaseViewHolder> {
    public RVAdapter(int layoutResId, @Nullable List<XFBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, XFBean item) {
        helper.setText(R.id.tv_id, item.getId());
        helper.setText(R.id.tv_money, item.getMoney());
        helper.setText(R.id.tv_time, item.getTime());
    }
}
