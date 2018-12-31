package com.gymsic.kara.gymsic.Plugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gymsic.kara.gymsic.Interface.OnHttpComplete;
import com.gymsic.kara.gymsic.Interface.OnServerDataComplete;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Model.UserModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by premkamon on 29/5/2560.
 */

public class Server {

    OkHttpClient client = new OkHttpClient();

    OnServerDataComplete comp;


    public Server(OnServerDataComplete comp){
        this.comp = comp;
    }
    public Server(){ }

    public void post(String url, String search) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("search", search);
        RequestBody body = formBuilder.build();

//        RequestBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("search", search)
//                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                //Log.d("log_debug","res "+res);
                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<Song>>(){}.getType();
                ArrayList<Song> songs = gson.fromJson(res, listType);
                comp.onServerDataComplete(songs);

            }
        });
    }

    public void postRegister(String url, UserModel user, OnHttpComplete httpComplete) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("name", user.getName());
        formBuilder.add("email", user.getEmail());
        formBuilder.add("fbid", user.getFbID());
        RequestBody requestBody = formBuilder.build();/**/

        /*okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", user.getName())
                .addFormDataPart("email", user.getEmail())
                .addFormDataPart("fbid", user.getFbID())
                .build();*/

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                httpComplete.onTaskCompleted(gson);

            }
        });
    }
}
