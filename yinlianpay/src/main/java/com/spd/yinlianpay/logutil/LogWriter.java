package com.spd.yinlianpay.logutil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guoxiaomeng on 2017/6/26.
 */

public class LogWriter {
    private static LogWriter mLogWriter;

    private static String mPath;

    private static Writer mWriter;

    private static SimpleDateFormat df;
    private static String filePath = "/mnt/sdcard/crash_log.txt";

    private LogWriter(String file_path) {
        this.mPath = file_path;
        this.mWriter = null;
    }

    public static LogWriter open(String file_path) throws IOException {
        if (mLogWriter == null) {
            mLogWriter = new LogWriter(file_path);
        }
        File mFile = new File(mPath);
        if(!mFile.exists())
        {
            mFile.createNewFile();
        }
        mWriter = new BufferedWriter(new FileWriter(mPath), 2048);
        df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");

        return mLogWriter;
    }

    public void close() throws IOException {
        mWriter.close();
    }

    public static void print(String log) {
        try {
            open(filePath);
            mWriter.write(df.format(new Date()));
            mWriter.write(log);
            mWriter.write("\n");
            mWriter.flush();
        }catch (Exception ex)
        {
            ex.printStackTrace();

        }
    }

    public void print(Class cls, String log) throws IOException { //如果还想看是在哪个类里可以用这个方法
        mWriter.write(df.format(new Date()));
        mWriter.write(cls.getSimpleName() + " ");
        mWriter.write(log);
        mWriter.write("\n");
        mWriter.flush();
    }

}
