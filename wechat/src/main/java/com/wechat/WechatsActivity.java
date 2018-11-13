package com.wechat;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spd.base.mvp.MVPBaseActivity;
import com.tencent.wlxsdk.WlxSdk;
import com.wechat.been.WechatPublicKey;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class WechatsActivity extends MVPBaseActivity<WechatsContract.View, WechatsPresenter> implements WechatsContract.View, View.OnClickListener {

    /**
     * hellword
     */
    private TextView mTextview;
    List<WechatPublicKey.MacKeyListBean> macKeyListBeans;
    List<WechatPublicKey.PubKeyListBean> pubKeyListBeans;
    /**
     * 测试
     */
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        initView();
        mPresenter.getPublicKey();


    }


    @Override
    public void success(Object o) {

        Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void erro(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getPublicKey(WechatPublicKey publicKey) {
        if (publicKey != null) {
            macKeyListBeans = publicKey.getMacKeyList();
            pubKeyListBeans = publicKey.getPubKeyList();
            Log.i(TAG, "getPblicKey: " + pubKeyListBeans.toString());
            Log.i(TAG, "getmacklicKey: " + macKeyListBeans.toString());
        }
    }

    String TAG = "tws";

    private void weixin(String code) {
        WlxSdk wlxSdk = new WlxSdk();
        wlxSdk.init("TXACAd2AgtZ2fZIG6mxXcyDWzDDoQxl2GqzJErbDHGyqhyAQEHAAIgAAAHIAdiFVZ3NkNb6OFbW+mKVwEJAABOIAAAAADQ0AQHXr3PHVwP6tSvsJMMmnxSYqpB94esZ6sNjku+NsnbWphrYydpp+x5wABb6V6HSXa1GgAAAABLTbi1");
        Log.i(TAG, "key_id:" + wlxSdk.get_key_id());
        Log.i(TAG, "mac_root_id:" + wlxSdk.get_mac_root_id());
        Log.i(TAG, "opne_id:" + wlxSdk.get_open_id());
        Log.i(TAG, "biz_data:" + wlxSdk.get_biz_data_hex());
        int result = 0;
        String openId = wlxSdk.get_open_id();
        String pubKey = "04E771FBE3D1E5730515F4FCFDF89133E9E5922A5CD665AD875506AE8E65DC35C513D8CF018AE5B303";
        //单位分
        int payfee = 1;
        //1 一次扫码计费
        byte scene = 1;
        //一次性扫码计费 scan_type=1
        byte scantype = 1;
        //机具流水号
        String pos_id = "17430597";

        String pos_trx_id = "12";
        String aes_mac_root = "D0868DE36E903A637143C0982FBCA2FC";
        result = wlxSdk.verify(openId, pubKey, payfee, scene, scantype, pos_id, pos_trx_id, aes_mac_root);
        Log.i(TAG, "验码结果:" + result);

        String record;
        record = wlxSdk.get_record();

//        try {
//            NameValuePair pair1 = new BasicNameValuePair("record", record);
//            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
//            pairList.add(pair1);
//            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
//                    pairList);
//            // URL使用基本URL即可，其中不需要加参数
//            HttpPost httpPost = new HttpPost("http://192.168.1.102:8020/acquirements_tasks/QRCode/uploadQRCode");
//            // 将请求体内容加入请求中
//            httpPost.setEntity(requestHttpEntity);
//            // 需要客户端对象来发送请求
//            HttpClient httpClient = new DefaultHttpClient();
//            // 发送请求
//            HttpResponse response = httpClient.execute(httpPost);
//            // 显示响应
//            HttpEntity httpEntity = response.getEntity();
//            InputStream is = null;
//            is = httpEntity.getContent();
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(is));
//            String retResult = "";
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                retResult = retResult + line;
//            }
//            // 显示响应
//            Log.i(TAG, retResult);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.getKeyId() + "\r\nmac_root_id:" + wlxSdk.getMacRootId() + "\r\nopne_id:" + wlxSdk.getOpenId() + "\r\nbiz_data:" + wlxSdk.getBizDataHex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aes_mac_root + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
//        Log.i(TAG, "验码记录:" + record);
    }

    private void initView() {
        mTextview = (TextView) findViewById(R.id.textview);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button) {
            mPresenter.checkQRCode("TXACAd2AgtZ2fZIG6mxXcyDWzDDoQxl2GqzJErbDHGyqhyAQEHAAIgAAAHIAdiFVZ3NkNb6itOW+rUSgEJAABOIAAAAADQ0AQDscD63NLAndzy59+A9p+DW/+KhA/duUy4EyiUoXk1MIpX7AvNozsZLQBb6i4euuPnmQAAAAB/gJOT", pubKeyListBeans, macKeyListBeans);
        } else {
        }
    }
}
