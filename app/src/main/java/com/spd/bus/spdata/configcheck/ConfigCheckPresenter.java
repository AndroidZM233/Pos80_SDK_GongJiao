package com.spd.bus.spdata.configcheck;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.example.test.yinlianbarcode.utils.SharedXmlUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spd.base.been.tianjin.AppSercetBackBean;
import com.spd.base.been.tianjin.AppSercetPost;
import com.spd.base.been.tianjin.GetMacBackBean;
import com.spd.base.been.tianjin.GetPublicBackBean;
import com.spd.base.been.tianjin.GetZhiFuBaoKey;
import com.spd.base.been.tianjin.KeysBean;
import com.spd.base.been.tianjin.PosKeysBackBean;
import com.spd.base.been.tianjin.UnqrkeyBackBean;
import com.spd.base.db.DbDaoManage;
import com.spd.base.utils.Datautils;
import com.spd.bus.Info;
import com.spd.bus.MyApplication;
import com.spd.bus.net.HttpMethods;
import com.spd.base.utils.LogUtils;
import com.spd.bus.spdata.YinLianPayManage;
import com.spd.bus.spdata.been.PsamBeen;
import com.spd.bus.spdata.mvp.BasePresenterImpl;
import com.spd.bus.util.ConfigUtils;
import com.spd.yinlianpay.util.PrefUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import wangpos.sdk4.libbasebinder.BankCard;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigCheckPresenter extends BasePresenterImpl<ConfigCheckContract.View> implements ConfigCheckContract.Presenter {
    /**
     * 微智接口返回数据
     */
    private byte[] respdata = new byte[512];
    /**
     * 微智接口返回数据长度
     */
    private int[] resplen = new int[1];
    /**
     * 微智接口返回状态 非0错误
     */
    private int retvalue = -1;

    private BankCard mBankCard;
    /**
     * //获取PSAM卡序列号
     */
    private final byte[] PSAM_15FILE = {0x00, (byte) 0xB0, (byte) 0x95, 0x00, 0x0A};
    /**
     * //获取PSAM卡终端机编号指令
     */
    private final byte[] PSAN_GET_ID = {0x00, (byte) 0xB0, (byte) 0x96, 0x00, 0x06};
    /**
     * //交通部
     */
    private final byte[] PSAM_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x80, 0x11};
    /**
     * //读取psam卡17文件
     */
    private final byte[] PSAM_GET_17FILE = {0x00, (byte) 0xB0, (byte) 0x97, 0x00, 0x01};
    /**
     * 返回正确结果
     */
    private final byte[] APDU_RESULT_SUCCESS = {(byte) 0x90, 0x00};
    /**
     * //住建部
     */
    private final byte[] ZJB_SELECT_DIR = {0x00, (byte) 0xA4, 0x00, 0x00, 0x02, (byte) 0x10, 0x01};


    @Override
    public void initPsam(Context context) {
        //获取支付宝微信key
        getZhiFuBaoAppSercet(context);
        getAliPubKeyTianJin();
        getYinLianPubKey(context);
        getWechatPublicKeyTianJin();
        String read = SharedXmlUtil.getInstance(context)
                .read(Info.POS_ID, Info.POS_ID_INIT);
        getShuangMianPubKey(context, "pos/posKeys?data=" + read);
        getWechatMacTianJin();
        new Thread(new Runnable() {
            @Override
            public void run() {

                ConfigCheckPresenter.this.mBankCard = MyApplication.getmBankCard();
                ConfigUtils.jsonToDB();

                boolean psam1Init = psam1Init();
                boolean psam2Init = psam2Init();
                LogUtils.d("" + psam1Init + psam2Init);
                if (!psam1Init) {
                    mView.setTextView(1, "失败");
                } else {
                    mView.setTextView(1, "成功");
                }
                if (!psam2Init) {
                    mView.setTextView(2, "失败");
                } else {
                    mView.setTextView(2, "成功");
                }


                if (psam1Init && psam2Init) {
                    mView.openActivity();
                }

            }
        }).start();

    }

    public void getShuangMianPubKey(Context context, String url) {
        HttpMethods.getInstance().posKeys(url, new Observer<PosKeysBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(PosKeysBackBean posKeysBackBean) {
                String code = posKeysBackBean.getCode();
                if (code.equals("00")) {
//                    SharedXmlUtil.getInstance(context).write(Info.YLSM_KEY
//                            , posKeysBackBean.getKey());
                    PrefUtil.setMasterkey(posKeysBackBean.getKey());
                    mView.setTextView(7, "成功");
                } else {
                    mView.setTextView(7, "失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(7, "失败");
                LogUtils.v(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getYinLianPubKey(Context context) {
        HttpMethods.getInstance().unqrkey(new Observer<UnqrkeyBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UnqrkeyBackBean unqrkeyBackBean) {
                List<KeysBean> keys = unqrkeyBackBean.getKeys();
                if (keys.size() > 0) {
                    DbDaoManage.getDaoSession().getKeysBeanDao().deleteAll();
                    for (KeysBean key : keys) {
                        DbDaoManage.getDaoSession().getKeysBeanDao().insertOrReplace(key);
                    }
                    mView.setTextView(5, "成功");
                } else {
                    mView.setTextView(5, "失败");
                }

            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(5, "失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取微信mac
     */
    public void getWechatMacTianJin() {
        HttpMethods.getInstance().getMac("", new Observer<GetMacBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetMacBackBean getMacBackBean) {
                DbDaoManage.getDaoSession().getGetMacBackBeanDao().deleteAll();
                DbDaoManage.getDaoSession().getGetMacBackBeanDao()
                        .insertOrReplace(getMacBackBean);
                mView.setTextView(8, "成功");
            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(8, "失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * 获取微信的秘钥
     */
    public void getWechatPublicKeyTianJin() {
        HttpMethods.getInstance().getPublic("", new Observer<GetPublicBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetPublicBackBean getPublicBackBean) {
                DbDaoManage.getDaoSession().getGetPublicBackBeanDao().deleteAll();
                DbDaoManage.getDaoSession().getGetPublicBackBeanDao()
                        .insertOrReplace(getPublicBackBean);
                mView.setTextView(6, "成功");
            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(6, "失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 调用天津后台接口AppSercet
     */
    public void getZhiFuBaoAppSercet(Context context) {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String, String> map = new HashMap<>();
        String posID = SharedXmlUtil.getInstance(context).read(Info.POS_ID, Info.POS_ID_INIT);
        AppSercetPost appSercetPost = new AppSercetPost();
        appSercetPost.setDeviceId(posID);
        map.put("data", gson.toJson(appSercetPost));
        HttpMethods.getInstance().appSercet(map, new Observer<AppSercetBackBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AppSercetBackBean appSercetBackBean) {
                AppSercetBackBean.DataBean data = appSercetBackBean.getData();
                SharedXmlUtil.getInstance(context).write(Info.ZFB_APP_KEY, data.getAppKey());
                SharedXmlUtil.getInstance(context).write(Info.ZFB_APP_SERCET, data.getAppSercet());
                mView.setTextView(3, "成功");
            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(3, "失败");
                LogUtils.v(e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getAliPubKeyTianJin() {
        HttpMethods.getInstance().publicKey(new Observer<GetZhiFuBaoKey>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetZhiFuBaoKey getZhiFuBaoKey) {
                String publicKeys = getZhiFuBaoKey.getPublicKeys();
                if (!TextUtils.isEmpty(publicKeys)) {
                    DbDaoManage.getDaoSession().getGetZhiFuBaoKeyDao().deleteAll();
                    DbDaoManage.getDaoSession().getGetZhiFuBaoKeyDao().insert(getZhiFuBaoKey);
                    mView.setTextView(4, "成功");
                } else {
                    mView.setTextView(4, "失败");
                }

            }

            @Override
            public void onError(Throwable e) {
                mView.setTextView(4, "失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * psam 初始化流程
     */
    private boolean psam1Init() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM1
                    , 60, respdata, resplen, "app1");
            if (retvalue != 0) {
                return false;
            }
            if (respdata[0] == (byte) 0x01) {
                return false;
            } else if (respdata[0] == (byte) 0x05) {
                byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_15FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    return false;
                }
                byte[] snr = resultBytes;
                //IC卡已经插入
                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAN_GET_ID);
                if (resultBytes == null || resultBytes.length == 2) {
                    return false;
                }
                //终端机编号
                byte[] deviceCode = resultBytes;

                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_SELECT_DIR);
                if (resultBytes == null || resultBytes.length == 2) {
                    return false;
                }
                resultBytes = sendApdus(BankCard.CARD_MODE_PSAM1_APDU, PSAM_GET_17FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    return false;
                }
                byte[] psamKey = Datautils.cutBytes(resultBytes, 0, 1);

                MyApplication.psamDatas.add(new PsamBeen(1, deviceCode, psamKey, snr));

                return true;
            } else {
                LogUtils.e("交通部psam初始化失败 " + Datautils
                        .byteArrayToString(Datautils.cutBytes(respdata, 0, resplen[0])));
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 住建部psam初始化
     */
    private boolean psam2Init() {
        try {
            retvalue = mBankCard.readCard(BankCard.CARD_TYPE_NORMAL, BankCard.CARD_MODE_PSAM2
                    , 60, respdata, resplen, "app1");
            if (retvalue != 0) {
                return false;
            }
            if (respdata[0] == (byte) 0x01) {
                //读卡失败
                return false;

            } else if (respdata[0] == (byte) 0x05) {
                byte[] resultBytes = sendApdus(BankCard.CARD_MODE_PSAM2_APDU, PSAM_15FILE);
                if (resultBytes == null || resultBytes.length == 2) {
                    return false;
                }
                byte[] snr = resultBytes;
                //IC卡已经插入
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, PSAN_GET_ID
                        , PSAN_GET_ID.length, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                //住建部获取终端编号错误
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    return false;
                }
                //终端机编号
                byte[] deviceCode = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
                //住建部PSAM卡终端机编号;
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, ZJB_SELECT_DIR
                        , ZJB_SELECT_DIR.length, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                //住建部选文件 10 01
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    //住建部切换1001错误
                    return false;
                }
                retvalue = mBankCard.sendAPDU(BankCard.CARD_MODE_PSAM2_APDU, PSAM_GET_17FILE
                        , PSAM_GET_17FILE.length, respdata, resplen);
                if (retvalue != 0) {
                    return false;
                }
                //住建部17文件获取秘钥索引
                if (!checkResuleAPDU(respdata, resplen[0])) {
                    //住建部获取秘钥索引错误
                    return false;
                }
                byte[] psamKey = Datautils.cutBytes(respdata, 0, 1);
                //住建部秘钥索引
                MyApplication.psamDatas.add(new PsamBeen(2, deviceCode, psamKey, snr));
                return true;
            } else {
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 封装接口自定义
     *
     * @param cardType 卡片类型
     * @param sendApdu 发送指令
     * @return 结果
     */
    private byte[] sendApdus(int cardType, byte[] sendApdu) {
        byte[] reBytes = null;
        //微智接口返回数据
        byte[] respdata = new byte[512];
        //微智接口返回数据长度
        int[] resplen = new int[1];
        try {
            int retvalue = mBankCard.sendAPDU(cardType, sendApdu, sendApdu.length, respdata, resplen);
            if (retvalue != 0) {
                mBankCard.breakOffCommand();
                //"微智接口返回错误码" + retvalue
                return reBytes;
            }
            if (!Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(respdata, resplen[0] - 2, 2))) {
                mBankCard.breakOffCommand();
                return Datautils.cutBytes(respdata, resplen[0] - 2, 2);
            }
            reBytes = Datautils.cutBytes(respdata, 0, resplen[0] - 2);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                mBankCard.breakOffCommand();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        return reBytes;
    }

    private boolean checkResuleAPDU(byte[] reByte, int le) {
        return Arrays.equals(APDU_RESULT_SUCCESS, Datautils.cutBytes(reByte, le - 2, 2));
    }
}
