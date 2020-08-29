package com.yangs.kedaquan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.utils.VpnSource;

/**
 * Created by yangs on 2017/11/16 0016.
 */

public class KebiaoGetActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private EditText et_user;
    private EditText et_pwd;
    private Button bt_login;
    private TextView tv_forget;
    private int kebiao_status_code;
    private VpnSource vpnSource;
    private ProgressDialog progressDialog;
    private Context context;
    private Spinner spinner;
    private String[] mItems;
    private String current_term;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kebiaogetactivity_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        progressDialog = new ProgressDialog(this);
        context = this;
        progressDialog.setCancelable(false);
        toolbar = findViewById(R.id.kebiaogetactivity_toolbar);
        et_user = findViewById(R.id.kebiaogetactivity_et_user);
        spinner = findViewById(R.id.kebiaogetactivity_sp);
        et_pwd = findViewById(R.id.kebiaogetactivity_et_pwd);
        bt_login = findViewById(R.id.kebiaogetactivity_bt_login);
        tv_forget = findViewById(R.id.kebiaogetactivity_tv_forget);
        bt_login.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("导入课表");
        toolbar.setTitleTextColor(getResources().getColor(R.color.global_blue));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 这个 if 用来判断是否登入了微皮恩，不用的时候可以删掉，
         * 当然在MeFragment.java里也要相应的注释掉一部分
         * 别忘了教务系统的地址也要改
         */
        if (APPAplication.save.getString("vpn_cookie", "").equals("")) {
            APPAplication.showToast("请先绑定VPN", 1);
            finish();
        }

        et_user.setText(APPAplication.save.getString("xh", ""));
        et_pwd.setText(APPAplication.save.getString("pwd", ""));
        mItems = new String[8];
        mItems[0] = "2016-2017-2";
        mItems[1] = "2017-2018-1";
        mItems[2] = "2017-2018-2";
        mItems[3] = "2018-2019-1";
        mItems[4] = "2018-2019-2";
        mItems[5] = "2019-2020-1";
        mItems[6] = "2019-2020-2";
        mItems[7] = "2020-2021-1";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(7, true);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    switch (kebiao_status_code) {
                        case 0:
                            progressDialog.setMessage("正在导入课表...");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    kebiao_status_code = vpnSource.getKebiao(current_term);
                                    handler.sendEmptyMessage(2);
                                }
                            }).start();
                            break;
                        case -1:
                            progressDialog.dismiss();
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "教务密码错误");
                            break;
                        case -2:
                            progressDialog.dismiss();
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "网络出错");
                            break;
                    }
                    break;
                case 2:
                    progressDialog.dismiss();
                    switch (kebiao_status_code) {
                        case 0:
                            APPAplication.save.edit().putString("xh", et_user.getText().toString().trim())
                                    .putString("pwd", et_pwd.getText().toString().trim()).apply();
                            setResult(4);
                            finish();
                            break;
                        case -1:
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "没有公布课表");
                            break;
                        case -2:
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "网络出错");
                            break;
                        case -3:
                            APPAplication.showDialog(KebiaoGetActivity.this,
                                    "获取课表时正则失败!");
                            APPAplication.recordUtil.addRord(
                                    APPAplication.save.getString("name", ""),
                                    APPAplication.save.getString("xh", ""),
                                    "导入课表", "获取课表时正则失败");
                            break;
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.kebiaogetactivity_tv_forget:
                if (APPAplication.isInitWebview) {
                    Bundle bundle = new Bundle();
                    /**
                     * 忘记密码
                     */
                    bundle.putString("url", "https://vpn.just.edu.cn/framework/,DanaInfo=jwgl.just.edu.cn,Port=8080+enteraccount.jsp");
                    bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));

                    Intent intent = new Intent(KebiaoGetActivity.this, BrowserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    final ProgressDialog pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setMessage("加载中...");
                    pd.show();
                    String cookie = APPAplication.save.getString("vpn_cookie", "");
                    String url = "https://vpn.just.edu.cn/,DanaInfo=jwgl.just.edu.cn,Port=8080+";
                    final WebView webView = new WebView(context);
                    webView.getSettings().setJavaScriptEnabled(true);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        CookieSyncManager.createInstance(context);
                    }
                    CookieManager cookieManager = CookieManager.getInstance();
                    for (String t : cookie.split(";")) {
                        cookieManager.setCookie(url, t);
                    }
                    webView.loadUrl(url);
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                            sslErrorHandler.proceed();
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            webView.destroy();
                            APPAplication.isInitWebview = true;
                            pd.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("url", "https://vpn.just.edu.cn/framework/,DanaInfo=jwgl.just.edu.cn,Port=8080+enteraccount.jsp");
                            bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));
                            Intent intent = new Intent(KebiaoGetActivity.this, BrowserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
                break;
            case R.id.kebiaogetactivity_bt_login:
                if (current_term == null || current_term.equals("")) {
                    APPAplication.showToast("请选择学期", 0);
                    return;
                }
                final String user = et_user.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (user.equals("")) {
                    et_user.setError("请输入学号");
                    return;
                } else {
                    et_user.setError(null);
                }
                if (pwd.equals("")) {
                    et_pwd.setError("请输入教务密码");
                    return;
                } else {
                    et_pwd.setError(null);
                }
                progressDialog.setMessage("正在登录教务系统...");
                progressDialog.show();
                vpnSource = new VpnSource("", "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        kebiao_status_code = vpnSource.checkJwUser(user, pwd);
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        current_term = mItems[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
