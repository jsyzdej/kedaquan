package com.yangs.kedaquan.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.R;

/**
 * Created by yangs on 2018/2/26 0026.
 */

public class CetTicketActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private EditText et_name;
    private EditText et_sfz;
    private Spinner spinner;
    private Button bt_query;
    private String select_type;
    private ProgressDialog progressDialog;
    private String result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cetticketactivity_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.cetticketactivity_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("四六级准考证找回");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_name = findViewById(R.id.cetticketactivity_et_name);
        et_sfz = findViewById(R.id.cetticketactivity_et_sfz);
        bt_query = findViewById(R.id.cetticketactivity_bt);
        spinner = findViewById(R.id.cetticketactivity_sp);
        bt_query.setOnClickListener(this);
        String[] mItems = new String[2];
        mItems[0] = "四级";
        mItems[1] = "六级";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0, true);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    progressDialog.dismiss();
                    if (TextUtils.isEmpty(result)) {
                        APPAplication.showToast("连接查询服务器失败,请稍后重试!", 0);
                        break;
                    }
                    try {
                        final JSONObject jsonObject = JSON.parseObject(result);
                        String mesg = jsonObject.getString("msg");
                        if (TextUtils.isEmpty(mesg)) {
                            String show_msg = "姓名:  " + jsonObject.getString("ks_xm") + "\n"
                                    + "身份证:  " + jsonObject.getString("ks_sfz") + "\n"
                                    + "准考证号:  " + jsonObject.getString("ks_bh");
                            new AlertDialog.Builder(CetTicketActivity.this).setMessage(show_msg)
                                    .setPositiveButton("复制准考证号", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData mClipData = ClipData.newPlainText("Label", jsonObject.getString("ks_bh"));
                                            cm.setPrimaryClip(mClipData);
                                            APPAplication.showToast("已复制准考证号", 0);
                                        }
                                    }).create().show();
                        } else
                            APPAplication.showDialog(CetTicketActivity.this, mesg);
                    } catch (Exception e) {
                        APPAplication.showToast("解析数据失败,请稍后重试!", 0);
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cetticketactivity_bt:
                final String name = et_name.getText().toString().trim();
                final String sfz = et_sfz.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    et_name.setError("请输入姓名");
                    return;
                } else
                    et_name.setError(null);
                if (TextUtils.isEmpty(sfz)) {
                    et_sfz.setError("请输入身份证");
                    return;
                } else
                    et_sfz.setError(null);
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(CetTicketActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("正在查询中");
                }
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result = APPAplication.bbsSource.cetTicketQuery(name, sfz, select_type);
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
            select_type = "1";
        else
            select_type = "2";
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
