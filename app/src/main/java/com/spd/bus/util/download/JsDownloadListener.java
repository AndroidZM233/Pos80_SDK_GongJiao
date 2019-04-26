package com.spd.bus.util.download;

/**
 * Created by 张明_ on 2019/4/11.
 * Email 741183142@qq.com
 */
public interface JsDownloadListener {
    void onStartDownload(long length);
    void onProgress(int progress);
    void onFail(String errorInfo);
}
