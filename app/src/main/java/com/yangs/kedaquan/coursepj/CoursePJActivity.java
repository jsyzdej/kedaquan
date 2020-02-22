package com.yangs.kedaquan.coursepj;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.activity.TitleBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/7/30.
 */

public class CoursePJActivity extends Activity implements View.OnClickListener, OnItemClickListener, OnRefreshListener {
    private EditText et_xh;
    private EditText et_pwd;
    private Button bt_login;
    private RadioGroup rg;
    private String xh;
    private String pwd;
    private AlertDialog dialog_login;
    private List<CoursePJList> list;
    private View header_view;
    private Button header_bt;
    private CoursePjAdapter coursePjAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private LRecyclerView lRecyclerView;
    private CoursePjSource coursePjSource;
    private TitleBuilder titleBuilder;
    private ProgressDialog progressDialog;
    private static int ss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coursepj_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        titleBuilder = new TitleBuilder(this);
        titleBuilder.setLeftImage(R.drawable.ic_arraw_back_white).setTitleText("一键评教", Boolean.FALSE).setTvTitleNoClick().setIvRightNoClick();
        titleBuilder.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lRecyclerView = findViewById(R.id.coursepj_lr);
        xh = APPAplication.save.getString("pj_xh", APPAplication.xh);
        pwd = APPAplication.save.getString("pj_pwd", APPAplication.save
                .getString("pwd", ""));
        View dialog_login_view = getLayoutInflater().inflate(R.layout.coursepj_login_dialog, null);
        et_xh = dialog_login_view.findViewById(R.id.coursepj_login_et_xh);
        et_pwd = dialog_login_view.findViewById(R.id.coursepj_login_et_pwd);
        bt_login = dialog_login_view.findViewById(R.id.coursepj_login_bt_login);
        rg = dialog_login_view.findViewById(R.id.coursepj_login_rg);
        switch (APPAplication.save.getInt("pj_type", 2)) {
            case 2:
                rg.check(R.id.coursepj_login_rg_1);
                break;
            case 0:
                rg.check(R.id.coursepj_login_rg_2);
                break;
        }
        dialog_login = new AlertDialog.Builder(this).setTitle("登录教务系统")
                .setView(dialog_login_view).create();
        et_xh.setText(xh);
        et_pwd.setText(pwd);
        dialog_login.show();
        bt_login.setOnClickListener(this);
        list = new ArrayList<>();
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coursePjAdapter = new CoursePjAdapter(list, getLayoutInflater());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(coursePjAdapter);
        header_view = LayoutInflater.from(this).inflate(R.layout.coursepj_header_view, (ViewGroup) findViewById(android.R.id.content), false);
        header_bt = header_view.findViewById(R.id.coursepj_header_bt);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "", "");
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coursepj_login_bt_login:
                if (et_xh.getText().toString().trim().equals("")) {
                    et_xh.setError("请输入学号");
                    return;
                } else {
                    et_xh.setError(null);
                }
                if (et_pwd.getText().toString().trim().equals("")) {
                    et_pwd.setError("请输入密码");
                    return;
                } else {
                    et_pwd.setError(null);
                }
                xh = et_xh.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                coursePjSource = new CoursePjSource(xh, pwd);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (coursePjSource.checkUser()) {
                            case 0:
                                dialog_login.dismiss();
                                int type = 2;
                                switch (rg.getCheckedRadioButtonId()) {
                                    case R.id.coursepj_login_rg_1:
                                        type = 2;
                                        break;
                                    case R.id.coursepj_login_rg_2:
                                        type = 0;
                                        break;
                                }
                                APPAplication.save.edit().putString("pj_xh", xh).putString("pj_pwd", pwd)
                                        .putInt("pj_type", type).apply();
                                handler.sendEmptyMessage(1);
                                break;
                            case -1:
                                handler.sendEmptyMessage(2);
                                break;
                            case -2:
                                handler.sendEmptyMessage(3);
                                break;
                        }
                    }
                }).start();
                break;
            case R.id.coursepj_header_bt:
                View view = getLayoutInflater().inflate(R.layout.coursepj_dialog_yijian, null);
                final EditText ee = view.findViewById(R.id.coursepj_dialog_yijian_et);
                new AlertDialog.Builder(CoursePJActivity.this).setTitle("评教设置").setView(view)
                        .setCancelable(false)
                        .setPositiveButton("开始评教", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (progressDialog == null)
                                    progressDialog = new ProgressDialog(CoursePJActivity.this);
                                progressDialog.setCancelable(false);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < list.size(); i++) {
                                            CoursePJActivity.ss = i;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.setMessage("正在评教 [" + list.get(CoursePJActivity.ss).getName() + "] ...");
                                                    if (!progressDialog.isShowing())
                                                        progressDialog.show();
                                                }
                                            });
                                            coursePjSource.doPj(list.get(i).getUrl(), ee.getText().toString());
                                        }
                                        APPAplication.recordUtil.addRord(
                                                APPAplication.save.getString("name", ""),
                                                APPAplication.save.getString("xh", ""),
                                                "yjpj", xh);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                APPAplication.showToast("评教完成", 0);
                                                lRecyclerView.refresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    lRecyclerView.refresh();
                    break;
                case 2:
                    APPAplication.showDialog(CoursePJActivity.this, "用户名或密码错误");
                    break;
                case 3:
                    APPAplication.showDialog(CoursePJActivity.this, "网络出错");
                    break;
                case 4:
                    lRecyclerViewAdapter.addHeaderView(header_view);
                    header_bt.setOnClickListener(CoursePJActivity.this);
                    coursePjAdapter.clear();
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    coursePjAdapter.addAll(list);
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case 5:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    APPAplication.showToast("评教完成", 0);
                    lRecyclerView.refresh();
                    break;
//                case 6 :
                default:
                    APPAplication.showDialog(CoursePJActivity.this, "当前没有需要评教的课!");
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    break;
//                case 7:
//                    APPAplication.showDialog(CoursePJActivity.this, "当前没有需要评教的课!");
//                    lRecyclerView.refreshComplete(10);
//                    lRecyclerViewAdapter.notifyDataSetChanged();
//                    break;
            }
            return true;
        }
    });

    @Override
    public void onItemClick(View view, final int position) {
        new AlertDialog.Builder(CoursePJActivity.this).setTitle("是否评教此课程?").setCancelable(false)
                .setMessage(list.get(position).getName())
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (list.get(position).getHasPj()) {
                            APPAplication.showDialog(CoursePJActivity.this, "此课程已经评教了,无法再次评教!");
                        } else {
                            if (progressDialog == null)
                                progressDialog = new ProgressDialog(CoursePJActivity.this);
                            progressDialog.setMessage("正在评教 [" + list.get(position).getName() + "] ...");
                            progressDialog.setCancelable(false);
                            if (!progressDialog.isShowing())
                                progressDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    coursePjSource.doPj(list.get(position).getUrl(), "老师的教学工作很认真出色");
                                    APPAplication.recordUtil.addRord(
                                            APPAplication.save.getString("name", ""),
                                            APPAplication.save.getString("xh", ""),
                                            "yjpj", xh + " ; " + list.get(position).getName());
                                    handler.sendEmptyMessage(5);
                                }
                            }).start();
                        }
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onRefresh() {
        lRecyclerViewAdapter.removeHeaderView();
        new Thread(new Runnable() {
            private List<String> pjFind;

            @Override
            public void run() {
                int type = APPAplication.save.getInt("pj_type", 2);
                pjFind = coursePjSource.getPjFind();
                if (pjFind.size() > 0) {
                    list = coursePjSource.getPjTable(pjFind.get(type));
                    if (list.size() > 0)
                        handler.sendEmptyMessage(4);
                    else
                        handler.sendEmptyMessage(6);
                } else {
                    handler.sendEmptyMessage(7);
                }
            }
        }).start();
    }
}
