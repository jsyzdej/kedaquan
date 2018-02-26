package com.yangs.kedaquan.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.activity.BrowserActivity;
import com.yangs.kedaquan.activity.CetTicketActivity;
import com.yangs.kedaquan.activity.VpnLoginActivity;
import com.yangs.kedaquan.book.Book_Find;
import com.yangs.kedaquan.coursepj.CoursePJActivity;
import com.yangs.kedaquan.find.FindMainAdapter;
import com.yangs.kedaquan.find.GlideImageLoader;
import com.yangs.kedaquan.score.ScoreActivity;
import com.yangs.kedaquan.utils.VPNUtils;
import com.yangs.kedaquan.utils.getKebiaoSource;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangs on 2017/5/6.
 */

public class FindFragment extends LazyLoadFragment implements OnBannerListener, OnItemClickListener, OnRefreshListener, OnItemLongClickListener {
    private Activity activity;
    private View mLayFind;
    private Banner banner;
    private LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<String> imagesUrl;
    private ProgressDialog progressDialog;
    private Handler handler;
    private int index;
    private List<String> dataList;
    private List<String> findUrlList;
    private Map<String, String> url_data;
    private View header_view;
    private Toolbar toolbar;
    private AlertDialog ty_dialog;

    @Override
    protected int setContentView() {
        return R.layout.find_layout;
    }

    @Override
    protected void lazyLoad() {
        if (isInit) {
            if (!isLoad) {
                initview();
                setHandler();
            }
        }

    }

    private void initview() {
        activity = getActivity();
        mLayFind = getContentView();
        toolbar = mLayFind.findViewById(R.id.find_toolbar);
        url_data = new HashMap<>();
        url_data.put("科大校历", "https://vpn.just.edu.cn/list/,DanaInfo=202.195.195.226+65.html");
        url_data.put("体育成绩", "https://vpn.just.edu.cn/xsgl/,DanaInfo=202.195.195.147+cjcx.asp");
        url_data.put("体育刷卡", "https://vpn.just.edu.cn/zcgl/,DanaInfo=202.195.195.147+xskwcx.asp?action=jlbcx");
        url_data.put("早操出勤", "https://vpn.just.edu.cn/zcgl/,DanaInfo=202.195.195.147+xskwcx.asp?action=zccx");
        url_data.put("实验系统", "https://vpn.just.edu.cn/sy/,DanaInfo=202.195.195.198+");
        url_data.put("强智教务", "https://vpn.just.edu.cn/,DanaInfo=jwgl.just.edu.cn,Port=8080+");
        url_data.put("奥蓝系统", "https://vpn.just.edu.cn/,DanaInfo=202.195.195.238,Port=866+LOGIN.ASPX");
        url_data.put("办事大厅", "https://vpn.just.edu.cn/,DanaInfo=my.just.edu.cn");
        url_data.put("电话列表", "http://mp.weixin.qq.com/s?__biz=MzA5MDQ1MTU5OQ==&mid=203948380&idx=1&sn=38768267f5611e44b0ad2480559a463c#wechat_redirect");
        url_data.put("四六级", "http://www.yunchafen.com.cn/score/alipay/cet-login");
        url_data.put("空教室", "https://vpn.just.edu.cn/jsxsd/kbcx/,DanaInfo=jwgl.just.edu.cn,Port=8080+kbxx_classroom");
        url_data.put("实时公交", "http://211.138.195.226/ba_traffic_js/wapbus/zhenjiang/");
        url_data.put("毕业设计", "https://vpn.just.edu.cn/,DanaInfo=bysj.just.edu.cn");
        url_data.put("学术论文", "https://vpn.just.edu.cn/library/,DanaInfo=lib.just.edu.cn,Port=81+");
        dataList = new ArrayList<>();
        dataList.add("成绩绩点");
        dataList.add("科大校历");
        dataList.add("体育成绩");
        dataList.add("体育刷卡");
        dataList.add("早操出勤");
        dataList.add("实验系统");
        dataList.add("空教室");
        dataList.add("一键评教");
        dataList.add("图书借阅");
        dataList.add("强智教务");
        dataList.add("奥蓝系统");
        dataList.add("办事大厅");
        dataList.add("毕业设计");
        dataList.add("学术论文");
        dataList.add("电话列表");
        dataList.add("四六级");
        dataList.add("四六级准考证找回");
        dataList.add("实时公交");
        FindMainAdapter adapter = new FindMainAdapter(dataList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        header_view = activity.getLayoutInflater().inflate(R.layout.find_header_layout, null);
        banner = header_view.findViewById(R.id.find_header_banner);
        List<String> images = new ArrayList<>();
        images.add("http://kedaquan-1251118879.file.myqcloud.com/1.jpg");
        images.add("http://kedaquan-1251118879.file.myqcloud.com/2.jpg");
        images.add("http://kedaquan-1251118879.file.myqcloud.com/3.jpg");
        banner.setImages(images).setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setOnBannerListener(this).setDelayTime(3000).start();
        lRecyclerViewAdapter.addHeaderView(header_view);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerView = mLayFind.findViewById(R.id.aaa);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 1) ? layoutManager.getSpanCount() : 1;
            }
        });
        lRecyclerView.setLayoutManager(layoutManager);
        lRecyclerView.setPullRefreshEnabled(true);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOutRapid);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.setHeaderViewColor(R.color.colorGreen, R.color.gallery_black, android.R.color.white);
    }

    @Override
    public void OnBannerClick(int position) {
        if (findUrlList == null) {
            findUrlList = new ArrayList<>();
            findUrlList.add(APPAplication.save.getString("find_0_remark", ""));
            findUrlList.add(APPAplication.save.getString("find_1_remark", ""));
            findUrlList.add(APPAplication.save.getString("find_2_remark", ""));
        }
        if (findUrlList.get(position).equals("")) {
            findUrlList = null;
            APPAplication.showToast("正在加载链接....", 0);
            return;
        }
        APPAplication.recordUtil.addRord(
                APPAplication.save.getString("name", ""),
                APPAplication.save.getString("xh", ""),
                "点击广告" + (position + 1), "");
        Intent intent = new Intent(activity, BrowserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", findUrlList.get(position));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initTy(final String user, final String pwd, final int position) {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(getContext());
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog.setMessage("正在登录体育系统...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int code = APPAplication.vpnSource.loginTy(user, pwd);
                progressDialog.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (code) {
                            case 0:
                                APPAplication.recordUtil.addRord(
                                        APPAplication.save.getString("name", ""),
                                        APPAplication.save.getString("xh", ""),
                                        "查体育", "");
                                if (ty_dialog != null)
                                    ty_dialog.dismiss();
                                APPAplication.save.edit()
                                        .putString("ty_user", user)
                                        .putString("ty_pwd", pwd).apply();
                                Bundle bundle = new Bundle();
                                bundle.putString("url", url_data.get(dataList.get(position)));
                                bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));
                                Intent intent2 = new Intent(activity, BrowserActivity.class);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent2.putExtras(bundle);
                                startActivity(intent2);
                                break;
                            case -1:
                                APPAplication.showToast("学号或密码错误", 0);
                                APPAplication.save.edit()
                                        .putString("ty_user", "")
                                        .putString("ty_pwd", "").apply();
                                break;
                            case -2:
                                APPAplication.showToast("网络出错", 0);
                                break;
                        }
                    }
                });
            }
        }).start();
    }

    private void gotoPage(final int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(activity, ScoreActivity.class);  //cjjd
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(activity, CoursePJActivity.class);  //yjpj
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(activity, Book_Find.class); //tsjy
                startActivity(intent);
                break;
            case 6:
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("加载中...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String xh = APPAplication.save.getString("xh", "");
                        String pwd = APPAplication.save.getString("pwd", "");
                        final getKebiaoSource source = new getKebiaoSource(xh, pwd, getContext());
                        final int code = source.checkUser();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                switch (code) {
                                    case 0:
                                        Bundle bundle = new Bundle();
                                        bundle.putString("url", url_data.get(dataList.get(position)));
                                        bundle.putString("cookie",
                                                APPAplication.save.getString("vpn_cookie", "")
                                                        + ";" + source.getCookie());
                                        Intent intent2 = new Intent(activity, BrowserActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent2.putExtras(bundle);
                                        startActivity(intent2);
                                        break;
                                    case -1:
                                        APPAplication.showToast("用户名或密码错误", 0);
                                        break;
                                    case -2:
                                        APPAplication.showToast("网络出错", 0);
                                        break;
                                }
                            }
                        });
                    }
                }).start();
                break;
            case 16:
                intent = new Intent(activity, CetTicketActivity.class); //四六级准考证找回
                startActivity(intent);
                break;
            default:
                if (position == 2 || position == 3 || position == 4) {
                    final String ty_user = APPAplication.save.getString("ty_user", "");
                    final String ty_pwd = APPAplication.save.getString("ty_pwd", "");
                    if (ty_dialog == null) {
                        View view = LayoutInflater.from(getContext())
                                .inflate(R.layout.ty_login_dialog, null);
                        final EditText ty_et_user = view.findViewById(R.id.ty_login_dialog_et_user);
                        final EditText ty_et_pwd = view.findViewById(R.id.ty_login_dialog_et_pwd);
                        Button bt_login = view.findViewById(R.id.ty_login_dialog_bt_login);
                        ty_et_user.setText(ty_user);
                        ty_et_pwd.setText(ty_pwd);
                        ty_dialog = new AlertDialog.Builder(getContext())
                                .setTitle("登录到体育管理系统").setView(view).create();
                        bt_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String user = ty_et_user.getText().toString().trim();
                                final String pwd = ty_et_pwd.getText().toString().trim();
                                if (user.equals("")) {
                                    ty_et_user.setError("请输入学号");
                                    return;
                                } else {
                                    ty_et_user.setError(null);
                                }
                                if (pwd.equals("")) {
                                    ty_et_pwd.setError("请输入密码");
                                    return;
                                } else {
                                    ty_et_pwd.setError(null);
                                }
                                initTy(user, pwd, position);
                            }
                        });
                    }
                    if (ty_user.equals("") || ty_pwd.equals("")) {
                        ty_dialog.show();
                    } else {
                        String[] xh = new String[1];
                        xh[0] = ty_user;
                        new AlertDialog.Builder(getContext()).setTitle("要查询的学号")
                                .setSingleChoiceItems(xh, 0, null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        initTy(ty_user, ty_pwd, position);
                                    }
                                }).setNegativeButton("换个学号", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ty_dialog.show();
                            }
                        }).create().show();
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url_data.get(dataList.get(position)));
                    bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));
                    Intent intent2 = new Intent(activity, BrowserActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtras(bundle);
                    startActivity(intent2);
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        if (position > 13) {
            gotoPage(position);
            return;
        }
        if (APPAplication.save.getString("vpn_cookie", "").equals("")) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("提示").setMessage("请先绑定VPN再使用此功能!")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(getContext(), VpnLoginActivity.class));
                        }
                    }).create().show();
        } else {
            VPNUtils.checkVpnIsValid(getContext(), new VPNUtils.onReultListener() {
                @Override
                public void onResult(int code, final ProgressDialog progressDialog) {
                    switch (code) {
                        case 0:
                            if (APPAplication.isInitWebview) {
                                progressDialog.dismiss();
                                gotoPage(position);
                            } else if (position == 1 || position == 6 || position == 9) {
                                String cookie = APPAplication.save.getString("vpn_cookie", "");
                                String url = "https://vpn.just.edu.cn/,DanaInfo=jwgl.just.edu.cn,Port=8080+";
                                try {
                                    final WebView webView = new WebView(getContext());
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    if (cookie != null) {
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                            CookieSyncManager.createInstance(getContext());
                                        }
                                        CookieManager cookieManager = CookieManager.getInstance();
                                        for (String t : cookie.split(";")) {
                                            cookieManager.setCookie(url, t);
                                        }
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
                                            progressDialog.dismiss();
                                            gotoPage(position);
                                        }
                                    });
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    APPAplication.showToast("发生了一个错误,请反馈!\n" +
                                            e.toString(), 1);
                                }
                            } else {
                                progressDialog.dismiss();
                                gotoPage(position);
                            }
                            break;
                        case -1:
                            progressDialog.dismiss();
                            APPAplication.showDialog(getContext(),
                                    "VPN密码错误,请重新绑定!");
                            break;
                        case -2:
                            progressDialog.dismiss();
                            new android.app.AlertDialog.Builder(getContext())
                                    .setTitle("提示").setMessage("此用户已经在其他地方登录,点击确定后" +
                                    "会自动打开一个浏览器页面,请在页面里点击继续会话,然后点击右上角的" +
                                    "登出,再返回科大圈绑定VPN!\n注意: 登出以后不要在浏览器里面再次登录," +
                                    "否则会无限循环!")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(APPAplication.vpnSource.getLocation());
                                            intent.setData(content_url);
                                            startActivity(intent);
                                        }
                                    }).create().show();
                            break;
                        case -3:
                            progressDialog.dismiss();
                            APPAplication.showDialog(getContext(), "网络错误");
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        APPAplication.showDialog(getContext(), dataList.get(position));
    }

    @Override
    public void onRefresh() {
        lRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                APPAplication.isFindUrl = false;
                lRecyclerViewAdapter.notifyDataSetChanged();
                lRecyclerView.refreshComplete(10);
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                List<String> images = new ArrayList<String>();
                images.add("http://kedaquan-1251118879.file.myqcloud.com/1.jpg");
                images.add("http://kedaquan-1251118879.file.myqcloud.com/2.jpg");
                images.add("http://kedaquan-1251118879.file.myqcloud.com/3.jpg");
                banner.update(images);
            }
        }, 2500);
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 2:
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        APPAplication.showDialog(getContext(), "从服务器获取链接失败!");
                        break;
                }
            }
        };
    }

}
