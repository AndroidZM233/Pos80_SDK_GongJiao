package com.spd.bus.spdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.test.yinlianbarcode.entity.ItineraryBackEntity;
import com.example.test.yinlianbarcode.entity.ItineraryEntity;
import com.example.test.yinlianbarcode.entity.QrEntity;
import com.example.test.yinlianbarcode.entity.SyncDataBackEntity;
import com.example.test.yinlianbarcode.entity.SyncEntity;
import com.example.test.yinlianbarcode.entity.UploadQrEntity;
import com.example.test.yinlianbarcode.net.NetApi;
import com.example.test.yinlianbarcode.utils.Logcat;
import com.example.test.yinlianbarcode.utils.SdkTool;
import com.example.test.yinlianbarcode.utils.ValidationUtils;
import com.spd.bus.R;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        findViewById(R.id.getPubKey).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        validation();
//        String qr = "AhEQA6MQEgGAAAApMQAAAQA8AgAmdyYJkBhklgL8LWEAAAAAAABoMRgAAAAAAAAAAAAAJncmCZAYZJYAAAAAAAAVilrtPWZEIl6Vb8rKgMpgXE4sJ9VgEwAfHwL1svHYhy44l8tuuP3hZn3Fyo5thO1uxiLI+cSzepfWXCttHHotjQ==";
//        QrEntity qrEntity = new QrEntity(qr);
//        String customData = qrEntity.getCustomData();


//        syncData("2569101089506304", "2569101089506304", "A450320180000032", "02",
//                "2231566612315668", "20180417094241", "6001", "0111", "20180417094234"
//                , "AhEQBaMQEgGAAAAowAIQAQAeAgAlaRAQiVBjBALJz7eVPhYpro5PWxgAAAAAAAAAAAAAJWkQEIlQYwQAAAAAAAAVnsHapkzr8j3M+XCwPdqAJgS9vRttyzld4obzleLZTd1WK0XzRNG9sSZwVKrkksdHOPNsRI2MbgstkAFfpVdeqw==");


        uploadQr();
    }


    private void validation() {
        String qr = "AhEQAaMQEgGAAAAowAIQAQAeAgAmSQSWmJJ2FgLvClXMQb7Lz2NJ0hgAAAAAAAAAAAAAJkkElpiSdhYAAAAAAAAVsz0FzpsvvnBB06jD04sgTCU8PK/hdub9Ncq9TJwEB9+tynGIyUyn9e++eDbh9W6qXoLkonuaiOjTggZ/lAtOGw==";
        QrEntity qrEntity = new QrEntity(qr);
        try {
            boolean validation = ValidationUtils.validation(qrEntity);

            Logcat.d(validation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void itinerary(String tripNo, String feeMode, String transAmount,
                           String baseAmount, String discountAmount, String discountDesc, String fineAmount, String fineDesc,
                           String settlementAmount) {

        ItineraryEntity itineraryEntity = SdkTool.getItineraryEntity(tripNo, feeMode, transAmount,
                baseAmount, discountAmount, discountDesc, fineAmount, fineDesc,
                settlementAmount);
        NetApi.getInstance().itinerary(SdkTool.getHeader(itineraryEntity.toString()), SdkTool.itinerary(itineraryEntity)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.Observer<ItineraryBackEntity>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ItineraryBackEntity response) {
                boolean success = response.isSuccess();
                Logcat.d(response.toString());

            }

            @Override
            public void onError(Throwable e) {
                Logcat.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void syncData(String voucherNo, String tripNo, String appId, String serviceId, String userId
            , String createTime, String lineNo, String stationNo, String scanTime, String qrCode) {

        SyncEntity syncEntity = SdkTool.getSyncEntity(voucherNo, tripNo, appId, serviceId, userId
                , createTime, lineNo, stationNo, scanTime, qrCode);
        NetApi.getInstance().syncData(SdkTool.getHeader(syncEntity.toString()), SdkTool.syncData(syncEntity)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.Observer<SyncDataBackEntity>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SyncDataBackEntity response) {
                boolean success = response.isSuccess();
                Logcat.d(response.toString());
                if (!success) {
                    Toast.makeText(MainActivity.this, "行程同步失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                itinerary(response.getResult().getTrip_no(), "1", "10", "10"
                        , "0", "no", "0",
                        "", "10");


            }

            @Override
            public void onError(Throwable e) {
                Logcat.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void uploadQr() {

        UploadQrEntity uploadQrEntity = SdkTool.getUploadQrEntity("0000000000000001", "2569101089506304", "2569101089506304",
                "A450320180000032", "02", "953e1629ae8e4f5b", "00", "20180626103223", "C120120170000018",
                "6001", "0111", "01", "0000000017400797", "192.168.1.107", "20180626103223",
                "01", "AhEQBaMQEgGAAAAowAIQAQAeAgAlaRAQiVBjBALJz7eVPhYpro5PWxgAAAAAAAAAAAAAJWkQEIlQYwQAAAAAAAAVnsHapkzr8j3M+XCwPdqAJgS9vRttyzld4obzleLZTd1WK0XzRNG9sSZwVKrkksdHOPNsRI2MbgstkAFfpVdeqw==",
                0.1, 0.1, "01", null);
        NetApi.getInstance().uploadQr(SdkTool.getHeader(uploadQrEntity.toString()), SdkTool.uploadQr(uploadQrEntity)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.Observer<ResponseBody>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody response) {
                try {
                    String result = response.string();
                    Logcat.d(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable e) {
                Logcat.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
