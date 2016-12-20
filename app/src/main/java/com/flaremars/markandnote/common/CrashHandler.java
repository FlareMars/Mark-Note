package com.flaremars.markandnote.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.flaremars.markandnote.util.FileUtils;
import com.flaremars.markandnote.util.StringUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by FlareMars on 2016/6/14.
 * 异常反馈Handler
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static String CRASH_FILES_PATH;
    public static final int CRASH_FILE_LIMIT = 10;
    private static final String TAG = "CrashHandler";

    //handler的唯一实例
    private static CrashHandler INSTANCE;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler defaultHandler;
    //程序的Context对象
    private App app;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();
    //用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    /** 获取CrashHandler实例 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    public void init(App app) {
        Log.i(TAG,"init()");
        this.app = app;
        CRASH_FILES_PATH = app.getExternalCacheDir().getPath() + "/markandnote/crashlog/";
        //获取系统默认的UncaughtException处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该类为线程默认UncatchException的处理器。
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /** 当UncaughtException发生时会回调该函数来处理 */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && defaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            defaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /*
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }


    /*
     * 保存错误信息到文件中
     *
     * @param ex 异常信息
     * @return 返回文件路径,便于将文件传送到服务器
     */
    private String saveCrashInfoToFile(Throwable ex) {

        //将设备信息变成string
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        //递归获取全部的exception信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result); //将写入的结果

        //构造文件名
        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + "-" + timestamp + ".log";

        //写文件和限制数量
        File creashFilesDir = new File(CRASH_FILES_PATH);
        if (!creashFilesDir.exists()) {
            creashFilesDir.mkdirs();
        }
        File crashFile = new File(creashFilesDir, fileName);
        FileUtils.INSTANCE.writeFile(sb.toString().getBytes(), crashFile);
        cleanLogFileToN(CRASH_FILES_PATH);

        if (crashFile.exists()) {
            return crashFile.getPath();
        } else {
            return "";
        }
    }

    private void sendCrashFileToServer(String filePath) {
        if (isNetworkAvailable(app)) {
            BmobFile bmobFile = new BmobFile(new File(filePath));
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }

    Comparator<File> newFileFinder = new Comparator<File>(){

        @Override
        public int compare(File x, File y) {
            if (x.lastModified()>y.lastModified()) return 1;
            if (x.lastModified()<y.lastModified()) return -1;
            else return 0;
        }
    };

    /*
     * 清空日志文件夹
     */
    private int cleanLogFileToN(String dirname){
        File dir = new File(dirname);
        if (dir.isDirectory()){
            File[] logFiles = dir.listFiles();
            if (logFiles.length > CRASH_FILE_LIMIT){
                Arrays.sort(logFiles, newFileFinder);  //从小到大排
                for (int i = 0 ; i < logFiles.length - CRASH_FILE_LIMIT;i++){
                    logFiles[i].delete();
                }
            }
        }

        return 1;
    }

    /*
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                // debug蛮重要的
                ex.printStackTrace();
                Toast.makeText(app, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //收集设备参数信息
        collectDeviceInfo(app);
        //保存日志文件
        final String crashFilePath = saveCrashInfoToFile(ex);
        //发送日志文件到服务器
        if (!StringUtils.INSTANCE.isEmpty(crashFilePath)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendCrashFileToServer(crashFilePath);
                }
            }).start();
        }
        return true;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
