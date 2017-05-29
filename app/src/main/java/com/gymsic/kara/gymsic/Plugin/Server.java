package com.gymsic.kara.gymsic.Plugin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gymsic.kara.gymsic.Interface.OnServerDataComplete;
import com.gymsic.kara.gymsic.Interface.OnTaskComplete;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.R;
import com.gymsic.kara.gymsic.SongFragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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
}
