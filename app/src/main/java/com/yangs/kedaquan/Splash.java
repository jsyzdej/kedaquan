package com.yangs.kedaquan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.activity.MainActivity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yangs on 2017/2/27.
 */

public class Splash extends Activity implements View.OnClickListener {
    private LinearLayout linearLayout;
    private View splash_v;
    private int count = 3;
    private TextView tv_skip;
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (APPAplication.debug || APPAplication.kp_status.equals("关")) {
            startActivity(new Intent(Splash.this, MainActivity.class));
            finish();
        } else {
            try {
                setContentView(R.layout.splash_layout);
                linearLayout = findViewById(R.id.splash_layout);
                splash_v = findViewById(R.id.splash_v);
                tv_skip = findViewById(R.id.splash_tv_skip);
                splash_v.setOnClickListener(this);
                tv_skip.setOnClickListener(this);
                if (new File(APPAplication.getPath() + "/kp.jpg").exists()) {
                    Drawable drawable = Drawable.createFromPath(APPAplication.getPath() + "/kp.jpg");
                    linearLayout.setBackground(drawable);
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                };
                timer = new Timer();
                timer.schedule(task, 50, 1000);
            } catch (Exception e) {
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null)
            timer.cancel();
        if (task != null)
            task.cancel();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (count == 0) {
                        if (timer != null)
                            timer.cancel();
                        if (task != null)
                            task.cancel();
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                    }
                    tv_skip.setVisibility(View.VISIBLE);
                    tv_skip.setText("跳过(" + count + "s)");
                    count--;
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_v:
                if (timer != null)
                    timer.cancel();
                if (task != null)
                    task.cancel();
                startActivity(new Intent(Splash.this, MainActivity.class));
                String s = APPAplication.save.getString("kp_remark", "");
                if (!s.equals("")) {
                    PackageManager packageManager = getPackageManager();
                    try {
                        packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + s;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        APPAplication.recordUtil.addRord(
                                APPAplication.save.getString("name", ""),
                                APPAplication.save.getString("xh", ""),
                                "点击开屏", "");
                    } catch (PackageManager.NameNotFoundException e) {
                        APPAplication.showToast("无法拉起手机qq!", 0);
                    }
                }
                finish();
                break;
            case R.id.splash_tv_skip:
                startActivity(new Intent(Splash.this, MainActivity.class));
                if (timer != null)
                    timer.cancel();
                if (task != null)
                    task.cancel();
                finish();
                break;
        }
    }
}
