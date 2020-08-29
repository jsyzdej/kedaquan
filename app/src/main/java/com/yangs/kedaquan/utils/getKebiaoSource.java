package com.yangs.kedaquan.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yangs.kedaquan.APPAplication;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangs on 2017/2/12.
 */

public class getKebiaoSource {
    private String xh;
    private String pwd;
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie;
    private Context context;

    public getKebiaoSource(String xh, String pwd, Context context) {
        this.context = context;
        this.xh = xh;
        this.pwd = pwd;
        cookie = APPAplication.save.getString("vpn_cookie", "");
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .followRedirects(false).followSslRedirects(false).build();
    }

    public void exit() {
        Date date = new Date();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/xk/,DanaInfo=jwgl.just.edu.cn,Port=8080+LoginToXk?method=exit&tktime="
                        + date.getTime())
                .headers(requestHeaders).header("Cookie", cookie).build();
        try {
            mOkHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkUser() {
        exit();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("USERNAME", xh).add("PASSWORD",
                pwd);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/jsxsd/xk/LoginToXk,DanaInfo=jwgl.just.edu.cn,Port=8080")
                .headers(requestHeaders).header("Cookie", cookie).post(requestBody).build();
        try {
            mOkHttpClient.newCall(request).execute();
            request = new Request.Builder()
                    .url("https://vpn.just.edu.cn/jsxsd/framework/,DanaInfo=jwgl.just.edu.cn,Port=8080+xsMain.jsp")
                    .headers(requestHeaders).header("Cookie", cookie).post(requestBody).build();
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Element name = document.getElementById("Top1_divLoginName");
            response.close();
            if (name != null)
                return 0;
            else
                return -1;
        } catch (Exception e) {
            return -2;
        }
    }

    public String getCookie() {
        return cookie;
    }

    public int getWeek(Context context) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/getWeek.jsp")
                .post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JSONObject tmp = new JSONObject(response.body().string());
            return Integer.parseInt(tmp.getString("week"));
        } catch (Exception e) {
            APPAplication.showToast("从服务器获取当前周失败!", 0);
            return 1;
        }
    }

    public int getKebiao(String year) {
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='course' ";
        Cursor cursor = APPAplication.db.rawQuery(sql, null);
        try {
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    APPAplication.db.execSQL("drop table course");
                }
            }
        } catch (Exception e) {
            APPAplication.showToast(e.getMessage(), 1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        APPAplication.save.edit().putString("xh", this.xh).putString("pwd", this.pwd).putString("term", year).apply();
        APPAplication.db.execSQL("create table course(id INTEGER PRIMARY KEY AUTOINCREMENT,课程名 TEXT,课程代码 TEXT,教室 TEXT,老师 TEXT,星期 INTEGER,节次 INTEGER,周次 TEXT,颜色代码 INTEGER);");
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("xnxq01id", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/xskb/,DanaInfo=jwgl.just.edu.cn,Port=8080+xskb_list.do")
                .headers(requestHeaders)
                .header("Cookie", cookie)
                .post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Element name = document.getElementById("Top1_divLoginName");
            APPAplication.save.edit().putString("name", name.text().split("\\(")[0]).apply();
            APPAplication.name = APPAplication.save.getString("name", "未登录");
            Elements kebiao = document.getElementsByAttributeValue("id", "kbtable").select("tr");
            if (kebiao.size() <= 2) {
                return -1;
            }
            int i = 0, color;
            int color_no_again_control = 0;
            for (Element e : kebiao) {
                Elements everyday = e.select("td");
                if (everyday.size() > 0) {
                    i++;
                }
                for (int j = 0; j < everyday.size(); j++) {
                    if (everyday.size() == 1) { // 课表备注
                        APPAplication.save.edit().putString("extra", everyday.get(0).text()).apply();
                    } else {
                        String tmp = "";
                        Element detail = everyday.get(j).select("div.kbcontent").first();
                        if (!detail.html().contains("&nbsp")) {
                            for (String t : detail.html().split("---------------------")) {
                                // 高兼容性 by yangs 2017-2-12
                                Document t2 = Jsoup.parse(t);
                                ContentValues cv = new ContentValues();
                                cv.put("课程代码", t2.text().split(" ")[0]);
                                cv.put("课程名", t2.text().split(" ")[1]);
                                cv.put("教室", t2.getElementsByAttributeValue("title", "教室").text() + " ");
                                cv.put("老师", t2.getElementsByAttributeValue("title", "老师").text());
                                cv.put("周次", t2.getElementsByAttributeValue("title", "周次(节次)").text());
                                cv.put("星期", (j + 1));
                                cv.put("节次", i);
                                sql = "select * from course where 课程名='" + cv.get("课程名") + "';";
                                cursor = APPAplication.db.rawQuery(sql, null);
                                try {
                                    cursor.moveToNext();
                                    color = cursor.getInt(8);
                                } catch (Exception ee) {
                                    if (color_no_again_control == 13) {
                                        color_no_again_control = 0;     //只支持14种颜色
                                        APPAplication.showToast("客官你的课程数量超过14了,目前还没有足够的颜色区分" +
                                                ",将使用重复的颜色!", 1);
                                    }
                                    color = color_no_again_control++;
                                } finally {
                                    if (cursor != null)
                                        cursor.close();
                                }
                                cv.put("颜色代码", color + "");
                                APPAplication.db.insert("course", null, cv);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return -2;
        }
        return 0;
    }

    public void getScore(String year, OnResponseResult onResponseResult) {
        if (onResponseResult == null)
            return;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("kksj", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/kscj/,DanaInfo=jwgl.just.edu.cn,Port=8080+cjcx_list")
                .headers(requestHeaders).post(requestBody).header("Cookie", cookie)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            onResponseResult.onResponseResult(1, response.body().string());
        } catch (IOException e) {
            onResponseResult.onResponseResult(-1, null);
        }
    }

    public void getScore2(String year, OnResponseResult onResponseResult) {
        if (onResponseResult == null)
            return;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("kksj", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/kscj/,DanaInfo=jwgl.just.edu.cn,Port=8080+cjtd_add_left?kch=&xnxq01id=")
                .headers(requestHeaders).post(requestBody).header("Cookie", cookie)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            onResponseResult.onResponseResult(1, response.body().string());
        } catch (IOException e) {
            onResponseResult.onResponseResult(-1, null);
        }
    }

    public interface OnResponseResult {
        void onResponseResult(int code, String result);
    }
}
