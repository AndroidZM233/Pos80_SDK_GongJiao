package com.wechat;


import android.util.Log;


import com.tencent.wlxsdk.WlxSdk;
import com.wechat.been.WechatPublicKey;
import com.wechat.mvp.BasePresenterImpl;
import com.wechat.net.WechatApi;

import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class WechatsPresenter extends BasePresenterImpl<WechatsContract.View> implements WechatsContract.Presenter {
    String TAG = "tw";


    @Override
    public void checkQRCode(String code, List<WechatPublicKey.PubKeyListBean> pbKeyList, List<WechatPublicKey.MacKeyListBean> macKeyList) {
        weixin(code, pbKeyList, macKeyList);
    }


    private void weixin(String code, List<WechatPublicKey.PubKeyListBean> pbKeyList, List<WechatPublicKey.MacKeyListBean> macKeyList) {
        WlxSdk wlxSdk = new WlxSdk();
        wlxSdk.init(code);
        Log.i(TAG, "key_id:" + wlxSdk.get_key_id());
        Log.i(TAG, "mac_root_id:" + wlxSdk.get_mac_root_id());
        Log.i(TAG, "opne_id:" + wlxSdk.get_open_id());
        Log.i(TAG, "biz_data:" + wlxSdk.get_biz_data_hex());
        int result = 0;
        String openId = wlxSdk.get_open_id();
        String pubKey = "";
        //单位分
        int payfee = 1;
        //1 一次扫码计费
        byte scene = 1;
        //一次性扫码计费 scan_type=1
        byte scantype = 1;
        //机具流水号
        String pos_id = "17430597";

        String pos_trx_id = "12";
        String aes_mac_root = "";
        for (int i = 0; i < pbKeyList.size(); i++) {
            if (wlxSdk.get_key_id()==pbKeyList.get(i).getKey_id()) {
                pubKey = pbKeyList.get(i).getPub_key();
                break;
            }

        }
        for (int i = 0; i < macKeyList.size(); i++) {
            if (String.valueOf(wlxSdk.get_mac_root_id()).equals(macKeyList.get(i).getKey_id())) {
                aes_mac_root = macKeyList.get(i).getMac_key();
                break;
            }
        }
//        aes_mac_root = "B431AF7CD446C6221ACF9395B50C8C8E";
//        pubKey = "0403142215E227D8B3B8EB9F73A713963C57313E318DD8C9ED81582E12DB8D2087DAA9E93E0E18C73031432842794B42694D57EA5C2FCDB38C";
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

        String resInfo = "二维码:" + code + "\r\nkey_id:" + wlxSdk.get_key_id() + "\r\nmac_root_id:" + wlxSdk.get_mac_root_id() + "\r\nopne_id:" + wlxSdk.get_open_id() + "\r\nbiz_data:" + wlxSdk.get_biz_data_hex() + "\r\npub_Key:" + pubKey + "\r\nmac_key:" + aes_mac_root + "\r\n验码结果:" + result + "\r\n扫码记录:" + record;
        Log.i("tw", "验码记录:" + resInfo);
        mView.success("返回验码记录" + resInfo);
    }


    @Override
    public void getPublicKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        WechatApi.getInstance().getPublicKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<WechatPublicKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WechatPublicKey book) {
                        mView.getPublicKey(book);
                        mView.success("获取成功成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.erro(e.toString());
                        Log.i("tw", "onError:  获取 錯誤 " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getPrivateKey() {

    }
}
