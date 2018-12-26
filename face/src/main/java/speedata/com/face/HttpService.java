package speedata.com.face;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;


/**
 * HTTP 服务端 监听8080端口
 */
public class HttpService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 创建对象，端口我这里设置为8080
        HTTPServer myServer = new HTTPServer(8080,getApplicationContext());
        try {
            // 开启HTTP服务
            myServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
