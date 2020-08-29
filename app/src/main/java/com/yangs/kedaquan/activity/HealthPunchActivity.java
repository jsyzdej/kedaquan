package com.yangs.kedaquan.activity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HealthPunchActivity {
    public void loginids() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://ids2.just.edu.cn/cas/login")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String pattern = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}_[A-Za-z0-9]*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((CharSequence) response.body());
        System.out.println(m.matches());
    }

}
