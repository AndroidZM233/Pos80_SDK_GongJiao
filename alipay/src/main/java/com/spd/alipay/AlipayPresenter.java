package com.spd.alipay;

import android.util.Log;

import com.spd.alipay.been.AlipayPublicKey;
import com.spd.alipay.mvp.BasePresenterImpl;
import com.spd.alipay.net.AlipayApi;


import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class AlipayPresenter extends BasePresenterImpl<AlipayContract.View> implements AlipayContract.Presenter {

    @Override
    public void getPublicKey() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "".toString());
        AlipayApi.getInstance().getPublicKey(requestBody)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<AlipayPublicKey>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AlipayPublicKey alipayPublickKey) {
                        mView.getPublicKey(alipayPublickKey);
                        mView.success("获取成功成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.erro(e.toString());
                        Log.i("PsamIcActivity", "onError:  获取 錯誤 " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
