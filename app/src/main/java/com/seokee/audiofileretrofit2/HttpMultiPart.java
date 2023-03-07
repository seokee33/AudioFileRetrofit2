package com.seokee.audiofileretrofit2;

import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpMultiPart {
    Context mContext;
    String result;

    public HttpMultiPart(final Context mContext, String path) {
        this.mContext = mContext;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "보낼파일명", RequestBody.create(MultipartBody.FORM, new File(path)))
                .build();


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("192.168.45.130");
        builder.appendPath("upload.php");
        String serverUrl = builder.build().toString();


        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.body().string());

                    result = json.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
