package com.spd.bus.spdata.showdata;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.spd.base.been.tianjin.produce.shuangmian.UploadSMDB;
import com.spd.base.been.tianjin.produce.shuangmian.UploadSMDBDao;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDB;
import com.spd.base.been.tianjin.produce.weixin.UploadInfoDBDao;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDB;
import com.spd.base.been.tianjin.produce.yinlian.UploadInfoYinLianDBDao;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDB;
import com.spd.base.been.tianjin.produce.zhifubao.UploadInfoZFBDBDao;
import com.spd.base.db.DbDaoManage;
import com.spd.base.utils.Datautils;
import com.spd.base.utils.DateUtils;
import com.spd.bus.Info;
import com.spd.bus.R;
import com.spd.bus.entity.Payrecord;
import com.spd.bus.spdata.PsamIcActivity;
import com.spd.bus.spdata.been.XFBean;
import com.spd.bus.spdata.mvp.MVPBaseActivity;
import com.spd.bus.spdata.showdata.adapter.RVAdapter;
import com.spd.bus.sql.SqlStatement;
import com.spd.bus.util.DatabaseTabInfo;
import com.spd.bus.view.MarqueeTextView;

import java.util.ArrayList;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ShowDataActivity extends MVPBaseActivity<ShowDataContract.View, ShowDataPresenter>
        implements ShowDataContract.View {

    private LinearLayout mLlAll;
    private TextView mTvTitle;
    private TextView mTvAllMoney;
    private TextView mTvAllPeople;
    private TextView mTvAllYue;
    private TextView mTvBlack;
    private TextView mTvLine;
    private TextView mTvBusNum;
    private LinearLayout mLlDriver;
    private TextView mTvDriverMoney;
    private TextView mTvDriverYue;
    private TextView mTvCompany;
    private TextView mTvPosId;
    private MarqueeTextView mTvNoUpload;
    private RecyclerView mRvContentXF;
    private boolean isDriverUI = false;
    private boolean isXFUI = false;
    private RVAdapter mAdapter;
    private LinearLayout mLlRv;
    private TextView mTvDriverPeople;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        initView();
        initData();
        initRV();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        double allMoney = (double) SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_MONEY, (float) 0.0) / 100;
        mTvAllMoney.setText(allMoney + "");
        int allPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_PEOPLE, 0);
        mTvAllPeople.setText(allPeople + "");
        int allYue = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.ALL_YUE, 0);
        mTvAllYue.setText(allYue + "");
        double driverMoney = (double) SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_MONEY, (float) 0.0) / 100;
        mTvDriverMoney.setText(driverMoney + "");
        int driverPeople = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_PEOPLE, 0);
        mTvDriverPeople.setText(driverPeople + "");
        int driverYue = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.DRIVER_YUE, 0);
        mTvDriverYue.setText(driverYue + "");

        String black = SharedXmlUtil.getInstance(getApplicationContext())
                .read(Info.BLACK, "");
        mTvBlack.setText(black);

        DatabaseTabInfo.getIntence("info");
        String busNr = DatabaseTabInfo.busno;
        mTvBusNum.setText(busNr);
        String lineNr = DatabaseTabInfo.line;
        mTvLine.setText(lineNr);
        String corNr = DatabaseTabInfo.dept;
        mTvCompany.setText(corNr);
        String posID = DatabaseTabInfo.deviceNo;
        mTvPosId.setText(posID);

        StringBuffer stringBuffer = new StringBuffer();
        List<Payrecord> selectTagRecord = SqlStatement.selectTagRecord();
        stringBuffer.append("卡" + selectTagRecord.size() + ",");
        List<UploadInfoDB> uploadInfoDBList = DbDaoManage.getDaoSession().getUploadInfoDBDao()
                .queryBuilder().where(UploadInfoDBDao.Properties.IsUpload.eq(false)).list();
        stringBuffer.append("微信" + uploadInfoDBList.size() + ",");
        List<UploadInfoYinLianDB> uploadInfoYinLianDBList = DbDaoManage.getDaoSession().getUploadInfoYinLianDBDao()
                .queryBuilder().where(UploadInfoYinLianDBDao.Properties.IsUpload.eq(false)).list();
        stringBuffer.append("银联" + uploadInfoYinLianDBList.size() + ",");
        List<UploadInfoZFBDB> uploadInfoZFBDBList = DbDaoManage.getDaoSession().getUploadInfoZFBDBDao()
                .queryBuilder().where(UploadInfoZFBDBDao.Properties.IsUpload.eq(false)).list();
        stringBuffer.append("支付宝" + uploadInfoZFBDBList.size() + ",");
        List<UploadSMDB> uploadSMDBList = DbDaoManage.getDaoSession().getUploadSMDBDao()
                .queryBuilder().where(UploadSMDBDao.Properties.IsUpload.eq(false)).list();
        stringBuffer.append("双免" + uploadSMDBList.size());

        mTvNoUpload.setText(stringBuffer.toString());
    }

    private void initRV() {
        List<XFBean> xfBeans = new ArrayList<>();
        List<Payrecord> listSel = SqlStatement.recordListSel();
        for (Payrecord payrecord : listSel) {
            XFBean xfBean = new XFBean();
            byte[] record = Datautils.hexStringToByteArray(payrecord.getRecord());
            if (record != null) {
                if (record[4] == (byte) 0x02) {
                    byte[] id = Datautils.cutBytes(record, 11, 8);
                    xfBean.setId(Datautils.byteArrayToString(id));
                } else {
                    byte[] id = Datautils.cutBytes(record, 11, 8);
                    xfBean.setId(Datautils.byteArrayToString(id));
                }
                byte exchType = record[2];
                byte[] money = Datautils.cutBytes(record, 56, 3);
                //0钱包2月票
                if (exchType == (byte) 0x02) {
                    xfBean.setMoney("1次");
                } else {
                    int anInt = Datautils.byteArrayToInt(money);
                    xfBean.setMoney(((double) anInt / 100) + "元");
                }
                byte[] ucDateTimeUTC = Datautils.cutBytes(record, 24, 4);
                long aLong = Long.parseLong(Datautils.byteArrayToString(ucDateTimeUTC), 16);
                String longToString = DateUtils.transferLongToString(
                        DateUtils.FORMAT_YMDHMS, aLong * 1000);
                xfBean.setTime(longToString);
                xfBeans.add(xfBean);
            }

            mAdapter = new RVAdapter(R.layout.item_rv, xfBeans);
            mRvContentXF.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRvContentXF.setAdapter(mAdapter);
        }
    }


    private void initView() {
        mLlAll = findViewById(R.id.ll_all);
        mTvTitle = findViewById(R.id.tv_title);
        mTvAllMoney = findViewById(R.id.tv_all_money);
        mTvAllPeople = findViewById(R.id.tv_all_people);
        mTvAllYue = findViewById(R.id.tv_all_yue);
        mTvBlack = findViewById(R.id.tv_black);
        mTvLine = findViewById(R.id.tv_line);
        mTvBusNum = findViewById(R.id.tv_bus_num);
        mLlDriver = findViewById(R.id.ll_driver);
        mTvDriverMoney = findViewById(R.id.tv_driver_money);
        mTvDriverYue = findViewById(R.id.tv_driver_yue);
        mTvCompany = findViewById(R.id.tv_company);
        mTvPosId = findViewById(R.id.tv_posId);
        mTvNoUpload = findViewById(R.id.tv_no_upload);
        mRvContentXF = findViewById(R.id.rv_contentXF);
        mLlRv = findViewById(R.id.ll_rv);
        mTvDriverPeople = findViewById(R.id.tv_driver_people);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ShowDataActivity.this, PsamIcActivity.class);
            startActivity(intent);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (isDriverUI) {
                    mLlRv.setVisibility(View.VISIBLE);
                    isXFUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else if (isXFUI) {
                    mLlRv.setVisibility(View.GONE);
                    isXFUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else {
                    mLlRv.setVisibility(View.GONE);
                    isXFUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isDriverUI) {
                    mLlRv.setVisibility(View.GONE);
                    isXFUI = false;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                } else if (isXFUI) {
                    mLlRv.setVisibility(View.GONE);
                    isXFUI = false;
                    isDriverUI = true;
                    mLlDriver.setVisibility(View.VISIBLE);
                } else {
                    mLlRv.setVisibility(View.VISIBLE);
                    isXFUI = true;
                    isDriverUI = false;
                    mLlDriver.setVisibility(View.GONE);
                }
            }
        }


        return super.onKeyDown(keyCode, event);
    }
}
