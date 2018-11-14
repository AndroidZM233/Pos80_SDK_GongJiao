package com.spd.alipay;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.spd.alipay.been.AliCodeinfoData;
import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.mvp.MVPBaseActivity;


import java.util.Collections;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AlipayActivity extends MVPBaseActivity<AlipayContract.View, AlipayPresenter> implements AlipayContract.View, View.OnClickListener {
    String TAG = "alipay";
    /**
     * 测试
     */
    private Button mButton;
    private TextView mTextView2;
    private AlipayJni alipay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_layout);
        mPresenter.getPublicKey();

        initView();
        alipay = new AlipayJni();
//        int log = alipay.initDv();
//        Log.i(TAG, "onCreate: " + log);
//        if (log == 1) {
//            Toast.makeText(this, "初始化成功", Toast.LENGTH_LONG).show();
//        }
        //json转换为list
//        Gson gson = new Gson();
//        List<AlipayPublicKey.PublicKeyListBean> persons = gson.fromJson(QRCODE_HEX_DATA, new TypeToken<List<AlipayPublicKey.PublicKeyListBean>>() {
//        }.getType());
    }

    private void initView() {
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mTextView2 = findViewById(R.id.textView2);
    }

    AliCodeinfoData testdata = new AliCodeinfoData();
    String record_id = "sh001_20160514140218_000001";
    String pos_id = "20170000000001";
    String pos_mf_id = "9998112123";
    String pos_sw_version = "2.6.14.03arm";
    String merchant_type = "22";
    String currency = "156";
    int amount = 1;
    String vehicle_id = "vid9702";
    String plate_no = "粤A 095852";
    String driver_id = "0236245394";
    String line_info = "795";
    String station_no = "asd";
    String lbs_info = "aaaa";
    String record_type = "SUBWAY";
    String QRCODE_HEX_DATA = "";
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button) {
            testdata = alipay.checkAliQrCode(testdata, "02010057323038383131323138363832303536365" +
                    "bee9706025807d" +
                    "00000008200000000000000000353b16bb2202598a06be71057b0c49b390c720fb217" +
                    "39d5cb5430343630313030103230383831313231383638323035363600483046022100b7" +
                    "b634bc71f2b110583e11492f732b72a19b042e71e523bf112c62298fd7d175022100f6eb0cf" +
                    "df4a53405f856ff22b72d8a8686fbe9ce154301515e450872bfb57def045be55c8e37" +
                    "3035021900e6678f761dc0124b53863f9a96e6ed6b86b2d01c255fdc2302187c69281e0" +
                    "aa3b679579abb9f7d8a1a0f4c16462650c7be36", record_id,
                    pos_id, pos_mf_id, pos_sw_version,
                    merchant_type, currency, amount,
                    vehicle_id, plate_no, driver_id,
                    line_info, station_no, lbs_info,
                    record_type);
            mTextView2.setText("\n校验结果:：" + testdata.inforState +
                    "\n卡类型:：" + Datautils.byteArrayToAscii(testdata.cardType) +
                    "\n卡号：:" + Datautils.byteArrayToAscii(testdata.cardNo) +
                    "\nuserId:：" + Datautils.byteArrayToAscii(testdata.userId)
            );

        } else {
        }
    }

    @Override
    public void success(Object o) {
        mTextView2.append(o.toString());
    }

    @Override
    public void erro(String msg) {
        mTextView2.append(msg);
    }

    String[] pubkeyBytes = null;

    @Override
    public void getPublicKey(AlipayPublicKey alipayPublickKey) {
        List<AlipayPublicKey.PublicKeyListBean> macKeyListBeans = alipayPublickKey.getPublicKeyList();
        Collections.sort(macKeyListBeans);
        List<AlipayPublicKey.PublicKeyListBean> keyListBeans = alipayPublickKey.getPublicKeyList();
        Collections.sort(keyListBeans);
        int log = alipay.initAliDev(keyListBeans);
        Log.i(TAG, "onCreate: " + log);
        if (log == 1) {
            Toast.makeText(this, "初始化成功", Toast.LENGTH_LONG).show();
        }
        mTextView2.append(macKeyListBeans.toString());

        Log.i(TAG, "getPblicKey: " + macKeyListBeans.toString());
    }


}
