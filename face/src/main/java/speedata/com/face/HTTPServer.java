package speedata.com.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.spd.base.been.AddTradeBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import speedata.com.face.bean.ResponseData;


/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2018/12/25 下午1:53.
 * 功能描述:TODO
 */
public class HTTPServer extends NanoHTTPD {

    private static final String TAG = "HTTPServer";
    private static final String REQUEST_ROOT = "/device/test/push";
    private final int port;

    private Context mContext;

    public HTTPServer(int port, Context context) {
        super(port);
        this.port = port;
        Log.i(TAG, "port: " + port);
        mContext = context;
    }


    @Override
    public Response serve(IHTTPSession session) {

        try {
            // 这一句话必须要写，否则在获取数据时，获取不到数据
            session.parseBody(new HashMap<String, String>());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        String uri = session.getUri();
        ResponseData responseData = new ResponseData();
        if (uri.equals(REQUEST_ROOT)) {
            Map<String, String> parms = session.getParms();
            //这里的data是POST提交表单时key
            String faceid = parms.get("data");
            Log.i(TAG, "uri: " + uri);
            Log.i(TAG, "faceid: " + faceid);
            if (faceid != null && !"".equals(faceid)) {
                sendReceFace(true, faceid);
            } else {
                sendReceFace(false, "");
            }

            upLoad(faceid);
            //回复数据
            responseData.setSuccess(true);
            responseData.setMessage("");
            builder.append("success");// 反馈给调用者的数据
        } else {
            responseData.setSuccess(false);
            responseData.setMessage("");
            builder.append("");
        }
        return new Response(builder.toString());
    }

    /**
     * 上传至服务器
     *
     * @param faceid 人脸ID
     */
    private void upLoad(String faceid) {
        AddTradeBean addTradeBean = getAddTradeBean(faceid);
        String addTradeBeanString = JSON.toJSONString(addTradeBean);
        FaceApi.getInstance().addTrade(addTradeBeanString).subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<ResponseData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseData s) {
                        Log.d(TAG, "rece==" + s);
                        sendUploadStatus("succes".equals(s.getMessage()), s.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "rece==error===" + e.getMessage());
                        sendUploadStatus(false, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "rece==onComplete");
                    }
                });
    }


    private AddTradeBean getAddTradeBean(String faceid) {
        AddTradeBean addTradeBean = new AddTradeBean();
        //TODO 终端流水号和终端编号 替换为实际值
        addTradeBean.setLocalTxnSeq((int) (1 + Math.random() * (1000 - 1 + 1)) + "");
        addTradeBean.setPosId((int) (1 + Math.random() * (1000 - 1 + 1)) + "");


        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateString = format.format(date);
        addTradeBean.setTxnDate(dateString);
        addTradeBean.setTxnTime("173829");
        addTradeBean.setFaceid(faceid);
        return addTradeBean;
    }

    private void sendReceFace(boolean result, String msg) {
        Intent intent = new Intent(Contants.ACTION_RECE_FACE);
        Bundle bundle = new Bundle();
        bundle.putBoolean("issuccess", result);
        bundle.putString("msg", msg);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    private void sendUploadStatus(boolean result, String msg) {
        Intent intent = new Intent(Contants.ACTION_UPLOAD_STATUS);
        Bundle bundle = new Bundle();
        bundle.putBoolean("issuccess", result);
        bundle.putString("msg", msg);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

}
