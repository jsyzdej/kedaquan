package com.yangs.kedaquan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.tencent.smtt.sdk.QbSdk;
import com.yangs.kedaquan.bbs.BBSSource;
import com.yangs.kedaquan.utils.RecordUtil;
import com.yangs.kedaquan.utils.VpnSource;
import com.yangs.kedaquan.utils.CrashHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yangs on 2017/3/18.
 */

public class APPAplication extends Application {
    private static Context context;
    public static Boolean isFindUrl = false;
    public static SharedPreferences save;
    public static int kebiao_show_ct;
    public static String term;
    public static String version;       //版本号
    public static String kp_status;     //是否显示开屏
    public static int login_stat;
    public static String name;
    public static String xh;
    public static int week;
    public static android.database.sqlite.SQLiteDatabase db;
    public static Boolean debug;
    public static BBSSource bbsSource;
    public static VpnSource vpnSource;
    public static RecordUtil recordUtil;
    public static Boolean bbs_login_status;
    public static Boolean bbs_login_status_check;
    public static String vpn_user;
    public static String vpn_pwd;
    public static Boolean isInitWebview;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = null;
        File file = new File("/proc/" + android.os.Process.myPid() + "/cmdline");
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            processName = br.readLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
          去除包名检测
          if ("com.yangs.kedaquan".equals(processName)) {
         */
            debug = false;
            bbs_login_status = false;
            bbs_login_status_check = false;
            context = getApplicationContext();
            save = getSharedPreferences("MainActivity", MODE_PRIVATE);
            kp_status = save.getString("kp_status", "关");
            name = save.getString("name", "");
            xh = save.getString("xh", "");
            login_stat = save.getInt("login_stat", 0);
            kebiao_show_ct = save.getInt("kebiao_show_ct", 0);
            term = save.getString("term", "2020-2021-1");   //当前学期
            db = getApplicationContext().openOrCreateDatabase("info.db", Context.MODE_PRIVATE, null);
            if (bbsSource == null) {
                bbsSource = new BBSSource();
            }
            isInitWebview = false;
            vpn_user = save.getString("vpn_user", "");
            vpn_pwd = save.getString("vpn_pwd", "");
            vpnSource = new VpnSource(vpn_user, vpn_pwd);
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                /*
                  注意，下面这行用于判断当前周，请务必修改好第一周的周一日期
                 */
                week = (int) (1 + (Calendar.getInstance().getTime().getTime() - df.parse("2020-09-07")
                        .getTime()) / (1000 * 3600 * 24 * 7));
                if (week < 1 || week > 20)
                    week = 1;
            } catch (ParseException e) {
                week = 1;
                e.printStackTrace();
            }
            try {
                PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName()
                        , 0);
                version = packageInfo.versionName;
            } catch (Exception e) {
                version = "1.0";
                e.printStackTrace();
            }
            recordUtil = new RecordUtil();
            Fresco.initialize(this);
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            imagePipeline.clearCaches();
            if (!debug) {
                CrashHandler crashHandler = CrashHandler.getInstance();
                crashHandler.init(getApplicationContext());
            }
            QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {

                }

                @Override
                public void onViewInitFinished(boolean b) {
                    if (b) {
                        Log.i("TAG", "X5内核加载成功");
                    } else {
                        Log.i("TAG", "X5内核加载失败");
                    }

                }
            });
//        }
    }

    public static Context getContext() {
        return context;
    }

    public static void showToast(final String src, final int i) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, src, i).show();
            }
        });
    }

    public static String getPath() {
        return context.getFilesDir().getAbsolutePath();
    }

    public static void showDialog(final Context context1, final String src) {
        try {
            new AlertDialog.Builder(context1).setTitle("提示").setMessage(src)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog2(final Context context1, final String src, final String title) {
        try {
            new AlertDialog.Builder(context1).setTitle(title).setMessage(src)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendHandler(Runnable r) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }

    public static void sendRefreshKebiao(Context context) {
        Intent appWidgetIntent = appWidgetIntent = new Intent();
        appWidgetIntent.setAction("Kedaquan_Widget_Update");
        context.sendBroadcast(appWidgetIntent);
    }

}