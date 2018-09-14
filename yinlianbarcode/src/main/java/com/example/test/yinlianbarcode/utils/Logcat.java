package com.example.test.yinlianbarcode.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.test.yinlianbarcode.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * @author :Reginer in  2018/3/7 15:19.
 * 联系方式:QQ:282921012
 * 功能描述:log工具类
 */
public final class Logcat {

    static final int V = Log.VERBOSE;
    static final int D = Log.DEBUG;
    static final int I = Log.INFO;
    static final int W = Log.WARN;
    static final int E = Log.ERROR;
    static final int A = Log.ASSERT;

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    @interface TYPE {
    }

    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};

    private static final int FILE = 0x10;
    private static final int JSON = 0x20;
    private static final int XML = 0x30;

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_CORNER = "┌";
    private static final String MIDDLE_CORNER = "├";
    private static final String LEFT_BORDER = "│ ";
    private static final String BOTTOM_CORNER = "└";
    private static final String SIDE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final int MAX_LEN = 3000;
    private static final Format FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA);
    private static final String NOTHING = "log nothing";
    private static final String NULL = "null";
    private static final String ARGS = "args";
    private static final String PLACEHOLDER = " ";
    private static Context sAppContext;
    private static Config sConfig;
    private static ExecutorService sExecutor;

    private Logcat() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    @NonNull
    public static Config init(@NonNull final Context context) {
        sAppContext = context;
        if (sConfig == null) {
            sConfig = new Config();
        }
        return sConfig;
    }

    @NonNull
    public static Config getConfig() {
        if (sConfig == null) {
            throw new NullPointerException("U should init first.");
        }
        return sConfig;
    }

    public static void v(@NonNull final Object... contents) {
        log(V, sConfig.mGlobalTag, contents);
    }

    public static void vTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(V, tag, contents);
    }

    public static void d(@NonNull final Object... contents) {
        log(D, sConfig.mGlobalTag, contents);
    }

    public static void dTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(D, tag, contents);
    }

    public static void i(@NonNull final Object... contents) {
        log(I, sConfig.mGlobalTag, contents);
    }

    public static void iTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(I, tag, contents);
    }

    public static void w(@NonNull final Object... contents) {
        log(W, sConfig.mGlobalTag, contents);
    }

    public static void wTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(W, tag, contents);
    }

    public static void e(@NonNull final Object... contents) {
        log(E, sConfig.mGlobalTag, contents);
    }

    public static void eTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(E, tag, contents);
    }

    public static void a(@NonNull final Object... contents) {
        log(A, sConfig.mGlobalTag, contents);
    }

    public static void aTag(@NonNull final String tag, @NonNull final Object... contents) {
        log(A, tag, contents);
    }

    public static void file(@NonNull final Object content) {
        log(FILE | D, sConfig.mGlobalTag, content);
    }

    public static void file(@TYPE final int type,@NonNull final Object content) {
        log(FILE | type, sConfig.mGlobalTag, content);
    }

    public static void file(@NonNull final String tag, @NonNull final Object content) {
        log(FILE | D, tag, content);
    }

    public static void file(@TYPE final int type, @NonNull final String tag, @NonNull final Object content) {
        log(FILE | type, tag, content);
    }

    public static void json(@NonNull final String content) {
        log(JSON | D, sConfig.mGlobalTag, content);
    }

    public static void json(@TYPE final int type,@NonNull final String content) {
        log(JSON | type, sConfig.mGlobalTag, content);
    }

    public static void json(@NonNull final String tag, @NonNull final String content) {
        log(JSON | D, tag, content);
    }

    public static void json(@TYPE final int type, @NonNull final String tag, @NonNull final String content) {
        log(JSON | type, tag, content);
    }

    public static void xml(@NonNull final String content) {
        log(XML | D, sConfig.mGlobalTag, content);
    }

    public static void xml(@TYPE final int type,@NonNull final String content) {
        log(XML | type, sConfig.mGlobalTag, content);
    }

    public static void xml(@NonNull final String tag, @NonNull final String content) {
        log(XML | D, tag, content);
    }

    public static void xml(@TYPE final int type, @NonNull final String tag, @NonNull final String content) {
        log(XML | type, tag, content);
    }

    private static void log(final int type, final String tag, final Object... contents) {
        boolean tempIf = !sConfig.mLog2ConsoleSwitch && !sConfig.mLog2FileSwitch;
        if (!sConfig.mLogSwitch || tempIf) {
            return;
        }
        int typeLow = type & 0x0f, typeHigh = type & 0xf0;
        if (typeLow < sConfig.mConsoleFilter && typeLow < sConfig.mFileFilter) {
            return;
        }
        final TagHead tagHead = processTagAndHead(tag);
        String body = processBody(typeHigh, contents);
        if (sConfig.mLog2ConsoleSwitch && typeLow >= sConfig.mConsoleFilter && typeHigh != FILE) {
            print2Console(typeLow, tagHead.tag, tagHead.consoleHead, body);
        }
        boolean tempIf1 = (sConfig.mLog2FileSwitch || typeHigh == FILE);
        if (tempIf1 && typeLow >= sConfig.mFileFilter) {
            print2File(typeLow, tagHead.tag, tagHead.fileHead + body);
        }
    }

    private static TagHead processTagAndHead(String tag) {
        if (!sConfig.mTagIsSpace && !sConfig.mLogHeadSwitch) {
            tag = sConfig.mGlobalTag;
        } else {
            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            StackTraceElement targetElement = stackTrace[3];
            final String fileName = getFileName(targetElement);
            if (sConfig.mTagIsSpace && isSpace(tag)) {
                int index = fileName.indexOf('.');
                tag = index == -1 ? fileName : fileName.substring(0, index);
            }
            if (sConfig.mLogHeadSwitch) {
                String tName = Thread.currentThread().getName();
                final String head = new Formatter()
                        .format("%s, %s.%s(%s:%d)",
                                tName,
                                targetElement.getClassName(),
                                targetElement.getMethodName(),
                                fileName,
                                targetElement.getLineNumber())
                        .toString();
                final String fileHead = " [" + head + "]: ";
                if (sConfig.mStackDeep <= 1) {
                    return new TagHead(tag, new String[]{head}, fileHead);
                } else {
                    final String[] consoleHead =
                            new String[Math.min(sConfig.mStackDeep, stackTrace.length - 3)];
                    consoleHead[0] = head;
                    int spaceLen = tName.length() + 2;
                    String space = new Formatter().format("%" + spaceLen + "s", "").toString();
                    for (int i = 1, len = consoleHead.length; i < len; ++i) {
                        targetElement = stackTrace[i + 3];
                        consoleHead[i] = new Formatter()
                                .format("%s%s.%s(%s:%d)",
                                        space,
                                        targetElement.getClassName(),
                                        targetElement.getMethodName(),
                                        getFileName(targetElement),
                                        targetElement.getLineNumber())
                                .toString();
                    }
                    return new TagHead(tag, consoleHead, fileHead);
                }
            }
        }
        return new TagHead(tag, null, ": ");
    }

    private static String getFileName(final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) {
            return fileName;
        }
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        int index = className.indexOf('$');
        if (index != -1) {
            className = className.substring(0, index);
        }
        return className + ".java";
    }

    private static String processBody(final int type, final Object... contents) {
        String body = NULL;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                if (object != null) {
                    body = object.toString();
                }
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS)
                            .append("[")
                            .append(i)
                            .append("]")
                            .append(" = ")
                            .append(content == null ? NULL : content.toString())
                            .append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body.length() == 0 ? NOTHING : body;
    }

    private static String formatJson(String json) {
        try {
            final String braces = "{";
            final String bracket = "[";
            if (json.startsWith(braces)) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith(bracket)) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private static void print2Console(final int type,
                                      final String tag,
                                      final String[] head,
                                      final String msg) {
        if (sConfig.mSingleTagSwitch) {
            StringBuilder sb = new StringBuilder();
            sb.append(PLACEHOLDER).append(LINE_SEP);
            if (sConfig.mLogBorderSwitch) {
                sb.append(TOP_BORDER).append(LINE_SEP);
                for (String aHead : head) {
                    sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP);
                }
                sb.append(MIDDLE_BORDER).append(LINE_SEP);
                for (String line : msg.split(LINE_SEP)) {
                    sb.append(LEFT_BORDER).append(line).append(LINE_SEP);
                }
                sb.append(BOTTOM_BORDER);
            } else {
                for (String aHead : head) {
                    sb.append(aHead).append(LINE_SEP);
                }
                sb.append(msg);
            }
            printMsgSingleTag(type, tag, sb.toString());
        } else {
            printBorder(type, tag, true);
            printHead(type, tag, head);
            printMsg(type, tag, msg);
            printBorder(type, tag, false);
        }
    }

    private static void printBorder(final int type, final String tag, boolean isTop) {
        if (sConfig.mLogBorderSwitch) {
            Log.println(type, tag, isTop ? TOP_BORDER : BOTTOM_BORDER);
        }
    }

    private static void printHead(final int type, final String tag, final String[] head) {
        if (head != null) {
            for (String aHead : head) {
                Log.println(type, tag, sConfig.mLogBorderSwitch ? LEFT_BORDER + aHead : aHead);
            }
            if (sConfig.mLogBorderSwitch) {
                Log.println(type, tag, MIDDLE_BORDER);
            }
        }
    }

    private static void printMsg(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len));
            }
        } else {
            printSubMsg(type, tag, msg);
        }
    }

    private static void printMsgSingleTag(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            if (sConfig.mLogBorderSwitch) {
                Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
                int index = MAX_LEN;
                for (int i = 1; i < countOfSub; i++) {
                    Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                            + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                            + LINE_SEP + BOTTOM_BORDER);
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                            + LEFT_BORDER + msg.substring(index, len));
                }
            } else {
                int index = 0;
                for (int i = 0; i < countOfSub; i++) {
                    Log.println(type, tag, msg.substring(index, index + MAX_LEN));
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag, msg.substring(index, len));
                }
            }
        } else {
            Log.println(type, tag, msg);
        }
    }

    private static void printSubMsg(final int type, final String tag, final String msg) {
        if (!sConfig.mLogBorderSwitch) {
            Log.println(type, tag, msg);
            return;
        }
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            Log.println(type, tag, LEFT_BORDER + line);
        }
    }

    private static void print2File(final int type, final String tag, final String msg) {
        Date now = new Date(System.currentTimeMillis());
        String format = FORMAT.format(now);
        String date = format.substring(0, 5);
        String time = format.substring(6);
        final String fullPath =
                (sConfig.mDir == null ? sConfig.mDefaultDir : sConfig.mDir)
                        + sConfig.mFilePrefix + "-" + date + ".txt";
        if (!createOrExistsFile(fullPath)) {
            Log.e("Logcat", "create " + fullPath + " failed!");
            return;
        }
        final String content = time +
                T[type - V] +
                "/" +
                tag +
                msg +
                LINE_SEP;
        input2File(content, fullPath);
    }

    private static boolean createOrExistsFile(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            boolean isCreate = file.createNewFile();
            if (isCreate) {
                printDeviceInfo(filePath);
            }
            return isCreate;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void printDeviceInfo(final String filePath) {
        String versionName = "";
        try {
            PackageInfo pi = sAppContext
                    .getPackageManager()
                    .getPackageInfo(sAppContext.getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String head = "************* Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + versionName +
                "\n************* Log Head ****************\n\n";
        input2File(head, filePath);
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void input2File(final String input, final String filePath) {
        if (sExecutor == null) {
            sExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        Future<Boolean> submit = sExecutor.submit(() -> {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(filePath, true));
                bw.write(input);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            if (submit.get()) {
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("Logcat", "log to " + filePath + " failed!");
    }

    public static class Config {
        private String mDefaultDir;
        private String mDir;
        private String mFilePrefix = "util";
        private boolean mLogSwitch = BuildConfig.DEBUG;
        private boolean mLog2ConsoleSwitch = true;
        private String mGlobalTag = "Reginer";
        private boolean mTagIsSpace = true;
        private boolean mLogHeadSwitch = true;
        private boolean mLog2FileSwitch = false;
        private boolean mLogBorderSwitch = true;
        private boolean mSingleTagSwitch = true;
        private int mConsoleFilter = V;
        private int mFileFilter = V;
        private int mStackDeep = 1;

        private Config() {
            if (mDefaultDir != null) {
                return;
            }
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && sAppContext.getExternalCacheDir() != null) {
                mDefaultDir = sAppContext.getExternalCacheDir() + FILE_SEP + "log" + FILE_SEP;
            } else {
                mDefaultDir = sAppContext.getCacheDir() + FILE_SEP + "log" + FILE_SEP;
            }
        }
        @NonNull
        public Config setLogSwitch(final boolean logSwitch) {
            mLogSwitch = logSwitch;
            return this;
        }
        @NonNull
        public Config setConsoleSwitch(final boolean consoleSwitch) {
            mLog2ConsoleSwitch = consoleSwitch;
            return this;
        }
        @NonNull
        public Config setGlobalTag(@NonNull final String tag) {
            if (isSpace(tag)) {
                mGlobalTag = "";
                mTagIsSpace = true;
            } else {
                mGlobalTag = tag;
                mTagIsSpace = false;
            }
            return this;
        }
        @NonNull
        public Config setLogHeadSwitch(final boolean logHeadSwitch) {
            mLogHeadSwitch = logHeadSwitch;
            return this;
        }
        @NonNull
        public Config setLog2FileSwitch(final boolean log2FileSwitch) {
            mLog2FileSwitch = log2FileSwitch;
            return this;
        }
        @NonNull
        public Config setDir(@NonNull final String dir) {
            if (isSpace(dir)) {
                mDir = null;
            } else {
                mDir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
            }
            return this;
        }

        @NonNull
        public Config setDir(@NonNull final File dir) {
            mDir = dir.getAbsolutePath() + FILE_SEP;
            return this;
        }

        @NonNull
        public Config setFilePrefix(@NonNull final String filePrefix) {
            if (isSpace(filePrefix)) {
                mFilePrefix = "util";
            } else {
                mFilePrefix = filePrefix;
            }
            return this;
        }

        @NonNull
        public Config setBorderSwitch(final boolean borderSwitch) {
            mLogBorderSwitch = borderSwitch;
            return this;
        }

        @NonNull
        public Config setSingleTagSwitch(final boolean singleTagSwitch) {
            mSingleTagSwitch = singleTagSwitch;
            return this;
        }

        @NonNull
        public Config setConsoleFilter(@TYPE final int consoleFilter) {
            mConsoleFilter = consoleFilter;
            return this;
        }

        @NonNull
        public Config setFileFilter(@TYPE final int fileFilter) {
            mFileFilter = fileFilter;
            return this;
        }

        @NonNull
        public Config setStackDeep(@IntRange(from = 1) final int stackDeep) {
            mStackDeep = stackDeep;
            return this;
        }

        @NonNull
        @Override
        public String toString() {
            return "switch: " + mLogSwitch
                    + LINE_SEP + "console: " + mLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (mTagIsSpace ? "null" : mGlobalTag)
                    + LINE_SEP + "head: " + mLogHeadSwitch
                    + LINE_SEP + "file: " + mLog2FileSwitch
                    + LINE_SEP + "dir: " + (mDir == null ? mDefaultDir : mDir)
                    + LINE_SEP + "filePrefix: " + mFilePrefix
                    + LINE_SEP + "border: " + mLogBorderSwitch
                    + LINE_SEP + "singleTag: " + mSingleTagSwitch
                    + LINE_SEP + "consoleFilter: " + T[mConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[mFileFilter - V]
                    + LINE_SEP + "stackDeep: " + mStackDeep;
        }
    }

    private static class TagHead {
        String tag;
        String[] consoleHead;
        String fileHead;

        TagHead(String tag, String[] consoleHead, String fileHead) {
            this.tag = tag;
            this.consoleHead = consoleHead;
            this.fileHead = fileHead;
        }
    }
}