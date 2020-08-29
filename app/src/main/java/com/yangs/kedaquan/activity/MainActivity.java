package com.yangs.kedaquan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.coursepj.CoursePJActivity;
import com.yangs.kedaquan.fragment.BBSFragment;
import com.yangs.kedaquan.fragment.FindFragment;
import com.yangs.kedaquan.fragment.KebiaoFragment;
import com.yangs.kedaquan.fragment.MeFragment;
import com.yangs.kedaquan.utils.AsyncTaskUtil;
import com.yangs.kedaquan.utils.RecordUtil;
import com.yangs.kedaquan.utils.getKebiaoSource;

public class MainActivity extends AppCompatActivity implements
        OnClickListener, MeFragment.OnLoginListener, KebiaoFragment.OnKebiaoRefreshListener {

    private long exitTime = 0;
    private JSONObject tmp_version;
    private TextView bottom_tv_kebiao;
    private ImageView bottom_iv_kebiao;
    private TextView bottom_tv_school;
    private ImageView bottom_iv_school;
    private TextView bottom_tv_find;
    private ImageView bottom_iv_find;
    private TextView bottom_tv_me;
    private ImageView bottom_iv_me;
    private LinearLayout mTabKebiao;
    private LinearLayout mTabSchool;
    private LinearLayout mTabFind;
    private LinearLayout mTabMe;
    private KebiaoFragment kebiaoFragment;
    private BBSFragment bbsFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;
    private FragmentManager fm;
    private int currentFragment;
    private ProgressDialog waitingDialog;
    private AsyncTaskUtil mDownloadAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
        mTabKebiao = findViewById(R.id.id_tab_kebiao);
        mTabSchool = findViewById(R.id.id_tab_school);
        mTabFind = findViewById(R.id.id_tab_find);
        mTabMe = findViewById(R.id.id_tab_me);
        mTabKebiao.setOnClickListener(this);
        mTabSchool.setOnClickListener(this);
        mTabFind.setOnClickListener(this);
        mTabMe.setOnClickListener(this);
        bottom_tv_kebiao = mTabKebiao.findViewById(R.id.id_tab_tv_kebiao);
        bottom_iv_kebiao = mTabKebiao.findViewById(R.id.id_tab_iv_kebiao);
        bottom_tv_school = mTabSchool.findViewById(R.id.id_tab_tv_school);
        bottom_iv_school = mTabSchool.findViewById(R.id.id_tab_iv_school);
        bottom_tv_find = mTabFind.findViewById(R.id.id_tab_tv_find);
        bottom_iv_find = mTabFind.findViewById(R.id.id_tab_iv_find);
        bottom_tv_me = mTabMe.findViewById(R.id.id_tab_tv_me);
        bottom_iv_me = mTabMe.findViewById(R.id.id_tab_iv_me);
        bottom_tv_kebiao.setTextColor(Color.rgb(30, 137, 231));
        bottom_iv_kebiao.setImageResource(R.drawable.bottom_iv_kebiao_press);
        fm = getSupportFragmentManager();
        showFragment(1);
        if (!APPAplication.debug) {
            appStart();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                APPAplication.showToast("未授予存储权限，当科大圈有新版本时将无法下载更新哦!", 1);
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 5:
                    APPAplication.showDialog(MainActivity.this, "学号或密码错误!");
                    waitingDialog.dismiss();
                    break;
                case 6:
                    APPAplication.showToast("抱歉，教务系统正在维护中，请稍后再绑定!", 0);
                    waitingDialog.dismiss();
                    break;
                case 7:
                    waitingDialog.setMessage("导入课表 [" + APPAplication.term + "].....");
                    break;
                case 8:
                    APPAplication.showToast("导入成功!", 0);
                    kebiaoFragment.initKebiao();
                    kebiaoFragment.toolbar_login.setText(APPAplication.save.getString("name", ""));
                    showFragment(1);
                    APPAplication.sendRefreshKebiao(getApplicationContext());
                    break;
                case 9:
                    waitingDialog.cancel();
                    APPAplication.showDialog(MainActivity.this, "账号或密码为空!");
                    break;
                case 10:
                    new AlertDialog.Builder(MainActivity.this).setTitle("导入课表失败")
                            .setMessage("1.教务系统还没有公布 " + APPAplication.term + " 学期的课表,请稍后再试\n" +
                                    "2.当前还没有完成评教，不能查看课表。\n是否需要打开一键评教？")
                            .setCancelable(false).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, CoursePJActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    break;
                case 12:
                    APPAplication.showDialog(MainActivity.this, "获取课表时正则失败!");
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private void appStart() {
        if (!APPAplication.debug)
            new Thread(new Runnable() {
                @Override
                public void run() {
                }
            }).start();
    }

    private void praseJson(String response) {
        JSONObject json = JSON.parseObject(response);
        final JSONObject notice_json = json.getJSONObject("通知");
        String old_id = APPAplication.save.getInt("notice_id", 0)
                + "";
        if (notice_json.getString("status").equals("开")
                && !notice_json.getString("id").equals(old_id)) {
            if (notice_json.getString("operate").equals("notice")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(notice_json.getString("title"))
                        .setMessage(notice_json.getString("content"))
                        .setPositiveButton(notice_json.getString("positivebutton"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                APPAplication.save.edit().putInt("notice_id",
                                        notice_json.getInteger("id"))
                                        .apply();
                            }
                        }).create().show();
            } else if (notice_json.getString("operate").equals("web")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(notice_json.getString("title"))
                        .setMessage(notice_json.getString("content"))
                        .setPositiveButton(notice_json.getString("positivebutton"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                APPAplication.save.edit().putInt("notice_id",
                                        notice_json.getInteger("id"))
                                        .apply();
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(notice_json.getString("remark"));
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        }).setNegativeButton(notice_json.getString("negativebutton"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        APPAplication.save.edit().putInt("notice_id",
                                notice_json.getInteger("id"))
                                .apply();
                    }
                }).create().show();
            } else if (notice_json.getString("operate").equals("qq")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(notice_json.getString("title"))
                        .setMessage(notice_json.getString("content"))
                        .setPositiveButton(notice_json.getString("positivebutton"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                APPAplication.save.edit().putInt("notice_id",
                                        notice_json.getInteger("id"))
                                        .apply();
                                PackageManager packageManager = getPackageManager();
                                try {
                                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                                    String url = "mqqwpa://im/chat?chat_type=wpa&uin="
                                            + notice_json.getString("remark");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } catch (PackageManager.NameNotFoundException e) {
                                    APPAplication.showToast("无法拉起手机qq!", 0);
                                }
                            }
                        }).setNegativeButton(notice_json.getString("negativebutton"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        APPAplication.save.edit().putInt("notice_id",
                                notice_json.getInteger("id"))
                                .apply();
                    }
                }).create().show();
            }
        }
        /**
         * 下方被注释掉的代码是开启检测更新，后期会改
         */
        tmp_version = json.getJSONObject("版本");
        if (!tmp_version.getString("versioncode").equals(APPAplication.version)) {
            try {
                final String url = tmp_version.getString("url");
                final String app_name = "KeDaQuan.apk";
                String info = "版本号 : " + tmp_version.getString("versioncode") + "\n"
                        + "大小 : " + tmp_version.getString("size") + "\n"
                        + "发布时间 : " + tmp_version.getString("time") + "\n"
                        + "详细 : \n" + tmp_version.getString("content");
                new AlertDialog.Builder(MainActivity.this).setTitle("发现新版本")
                        .setMessage(info)
                        .setNegativeButton("点击更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "正在下载中...", Toast.LENGTH_SHORT).show();
                                mDownloadAsyncTask = new AsyncTaskUtil(MainActivity.this, handler);
                                mDownloadAsyncTask.execute(url, app_name);
                            }
                        }).setPositiveButton("手动下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                }).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final JSONObject json_kp = (JSONObject) json.getJSONArray("开屏").get(0);
        if (json_kp.getString("状态").equals("开")) {
            if (!json_kp.getString("id").equals(APPAplication.save.getString("kp_id", ""))) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Boolean status = APPAplication.recordUtil.downloadKpAd(json_kp.getString("url"));
                        if (status) {
                            APPAplication.save.edit().putString("kp_id", json_kp.getString("id"))
                                    .putString("kp_status", "开")
                                    .putString("kp_remark", json_kp.getString("remark"))
                                    .apply();
                        }
                    }
                }).start();
            } else
                APPAplication.save.edit().putString("kp_status", "开").apply();
        } else {
            APPAplication.save.edit().putString("kp_status", "关").apply();
        }
        final JSONObject json_kbxx = (JSONObject) json.getJSONArray("课表详细").get(0);
        if (json_kbxx.getString("状态").equals("开")) {
            APPAplication.save.edit().putString("kbxx_status", "开")
                    .putString("kbxx_url", json_kbxx.getString("url"))
                    .putString("kbxx_remark", json_kbxx.getString("remark")).apply();
        } else
            APPAplication.save.edit().putString("kbxx_status", "关").apply();
        final JSONArray json_find = json.getJSONArray("发现");
        for (int i = 0, j = json_find.size(); i < j; i++) {
            JSONObject jo = (JSONObject) json_find.get(i);
            APPAplication.save.edit().putString("find_" + i + "_url", jo.getString("url"))
                    .putString("find_" + i + "_remark", jo.getString("remark")).apply();
        }
    }

    private void showFragment(int index) {
        FragmentTransaction transaction = fm.beginTransaction();
        if (kebiaoFragment != null)
            transaction.hide(kebiaoFragment);
        if (bbsFragment != null)
            transaction.hide(bbsFragment);
        if (findFragment != null)
            transaction.hide(findFragment);
        if (meFragment != null)
            transaction.hide(meFragment);
        bottom_tv_kebiao.setTextColor(Color.rgb(154, 154, 154));
        bottom_iv_kebiao.setImageResource(R.drawable.bottom_iv_kebiao);
        bottom_tv_school.setTextColor(Color.rgb(154, 154, 154));
        bottom_iv_school.setImageResource(R.drawable.bottom_iv_bbs);
        bottom_tv_find.setTextColor(Color.rgb(154, 154, 154));
        bottom_iv_find.setImageResource(R.drawable.bottom_iv_find);
        bottom_tv_me.setTextColor(Color.rgb(154, 154, 154));
        bottom_iv_me.setImageResource(R.drawable.bottom_iv_me);
        switch (index) {
            case 1:
                bottom_tv_kebiao.setTextColor(Color.rgb(30, 137, 231));
                bottom_iv_kebiao.setImageResource(R.drawable.bottom_iv_kebiao_press);
                if (kebiaoFragment == null) {
                    kebiaoFragment = new KebiaoFragment();
                    transaction.add(R.id.main_content, kebiaoFragment);
                } else {
                    transaction.show(kebiaoFragment);
                }
                currentFragment = 1;
                break;
            case 2:
                bottom_tv_school.setTextColor(Color.rgb(30, 137, 231));
                bottom_iv_school.setImageResource(R.drawable.bottom_iv_bbs_press);
                if (bbsFragment == null) {
                    bbsFragment = new BBSFragment();
                    transaction.add(R.id.main_content, bbsFragment);
                } else {
                    transaction.show(bbsFragment);
                }
                currentFragment = 2;
                break;
            case 3:
                bottom_tv_find.setTextColor(Color.rgb(30, 137, 231));
                bottom_iv_find.setImageResource(R.drawable.bottom_iv_find_press);
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.main_content, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                currentFragment = 3;
                break;
            case 4:
                bottom_tv_me.setTextColor(Color.rgb(30, 137, 231));
                bottom_iv_me.setImageResource(R.drawable.bottom_iv_me_press);
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.main_content, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                currentFragment = 4;
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_kebiao:
                if (currentFragment != 1)
                    showFragment(1);
                break;
            case R.id.id_tab_school:
                if (currentFragment != 2)
                    showFragment(2);
                break;
            case R.id.id_tab_find:
                if (currentFragment != 3)
                    showFragment(3);
                break;
            case R.id.id_tab_me:
                if (currentFragment != 4)
                    showFragment(4);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                kebiaoFragment.initKebiao();
                showFragment(1);
                APPAplication.sendRefreshKebiao(getApplicationContext());
                break;
            case 2:         //bottom_iv_kebiao
                login(data);
                break;
            case 3:         //bottom_iv_bbs
                showFragment(2);
                if (meFragment != null && meFragment.tv_bbs != null) {
                    meFragment.tv_bbs.setText("论坛ID : " + APPAplication.save.getString("bbs_user",
                            "论坛未登录"));
                }
                if (bbsFragment.lRecyclerView != null)
                    bbsFragment.lRecyclerView.refresh();
                break;
            case 4:
                APPAplication.login_stat = 1;
                APPAplication.save.edit().putInt("week", APPAplication.week)
                        .putInt("login_stat", 1).apply();
                handler.sendEmptyMessage(8);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void login(Intent data) {
        if (waitingDialog == null)
            waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setMessage("正在登陆...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
        final Bundle bundle = data.getBundleExtra("data");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(bundle.getString("user")) || TextUtils.isEmpty(bundle.getString("pwd"))) {
                    handler.sendEmptyMessage(9);
                } else {
                    getKebiaoSource getKebiaoSource = new getKebiaoSource(bundle.getString("user"), bundle.getString("pwd"), MainActivity.this);
                    switch (getKebiaoSource.checkUser()) {
                        case 0:
                            try {
                                APPAplication.term = bundle.getString("term");
                                handler.sendEmptyMessage(7);
                                switch (getKebiaoSource.getKebiao(bundle.getString("term"))) {
                                    case -1:
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(10);
                                        break;
                                    case -2:
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(12);
                                        break;
                                    default:
                                        APPAplication.week = getKebiaoSource.getWeek(getApplication());
                                        APPAplication.login_stat = 1;
                                        APPAplication.save.edit().putInt("week", APPAplication.week)
                                                .putInt("login_stat", 1).apply();
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(8);
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case -1:
                            handler.sendEmptyMessage(5);
                            break;
                        case -2:
                            waitingDialog.cancel();
                            handler.sendEmptyMessage(6);
                            break;
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出科大圈", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLogin(Intent data) {
        login(data);
    }

    @Override
    public void onKebiaoRefresh(Intent data) {
        login(data);
    }
}