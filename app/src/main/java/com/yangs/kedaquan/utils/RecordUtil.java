package com.yangs.kedaquan.utils;

import android.os.Build;

import com.yangs.kedaquan.APPAplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangs on 2017/12/15 0015.
 */

public class RecordUtil {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private static final String SERVER = "http://www.eaglemoe.com:8080/kedaquan/";
    private static final String AdServlet = SERVER + "AdServlet";
    private static final String RecordServlet = SERVER + "RecordServlet";
    private static final String VersionServlet = SERVER + "VersionServlet";
    private String model;
    private String network;
    private String versioncode;

    public RecordUtil() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "kedaquan").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
        model = android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE
                + " " + Build.VERSION.SDK;
        network = IntenetUtil.getNetworkState(APPAplication.getContext());
        versioncode = APPAplication.version;
    }

    public void addRord(String name, String xh, String operate, String remark) {
        addRord("", name, xh, operate, remark, null);
    }

    public void addRord(final String type, final String name, final String xh, final String operate, final String remark,
                        final OnResultListener onResultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder formBodyBuilder;
                RequestBody requestBody;
                Request request;
                String remark_s = remark.equals("") ? " " : remark;
                String name_s = name.equals("") ? " " : name;
                String xh_s = xh.equals("") ? " " : xh;
                switch (type) {
                    case "appStart":
                        formBodyBuilder = new FormBody.Builder()
                                .add("check", "yangs").add("action", "appStart")
                                .add("name", name_s).add("xh", xh_s).add("operate", operate)
                                .add("remark", remark_s).add("versioncode", versioncode)
                                .add("model", model).add("network", network);
                        requestBody = formBodyBuilder.build();
                        request = new Request.Builder().url(AdServlet).headers(requestHeaders)
                                .post(requestBody).build();
                        break;
                    case "checkUpdate":
                        formBodyBuilder = new FormBody.Builder()
                                .add("check", "yangs").add("action", "getLastVersion")
                                .add("name", name).add("xh", xh).add("operate", operate)
                                .add("remark", remark).add("versioncode", versioncode)
                                .add("model", model).add("network", network);
                        requestBody = formBodyBuilder.build();
                        request = new Request.Builder().url(VersionServlet).headers(requestHeaders)
                                .post(requestBody).build();
                        break;
                    default:
                        formBodyBuilder = new FormBody.Builder()
                                .add("check", "yangs").add("action", "addRecord")
                                .add("name", name).add("xh", xh).add("operate", operate)
                                .add("remark", remark).add("versioncode", versioncode)
                                .add("model", model).add("network", network);
                        requestBody = formBodyBuilder.build();
                        request = new Request.Builder().url(RecordServlet).headers(requestHeaders)
                                .post(requestBody).build();
                        break;
                }
                try {
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (onResultListener != null)
                        onResultListener.onSuccess(response.body().string());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (onResultListener != null)
                        onResultListener.onNetworkError();
                }
            }
        }).start();
    }

    public Boolean downloadKpAd(String url) {
        Boolean flag = false;
        Request request = new Request.Builder().url(url).headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            InputStream inStream = response.body().byteStream();
            FileOutputStream fs = new FileOutputStream(APPAplication.getPath()
                    + "/kp.jpg");
            byte[] buffer = new byte[1204];
            int bytesum = 0;
            int byteread;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void uploadErrorLog(String src, String filename) {
        if (src == null || src.length() == 0)
            return;
        String user = APPAplication.xh + APPAplication.name;
        if (user.equals(""))
            user = " ";
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs")
                .add("action", "uploadErrorLog")
                .add("msg", src).add("filename", filename)
                .add("user", user);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url(RecordServlet).headers(requestHeaders)
                .post(requestBody).build();
        try {
            mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnResultListener {
        void onSuccess(String response);

        void onNetworkError();
    }
}
