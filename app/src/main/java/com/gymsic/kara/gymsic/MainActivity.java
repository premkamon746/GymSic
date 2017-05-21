package com.gymsic.kara.gymsic;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gymsic.kara.gymsic.Model.Song;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.view.ViewGroup.LayoutParams;

public class MainActivity extends AppCompatActivity
        implements SongFragment.OnListFragmentInteractionListener
        , View.OnClickListener{

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public String server = "http://192.168.1.33:3000/";
    private TextView playlistHead;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playlistHead = (TextView)findViewById(R.id.playList);
        playlistHead.setOnClickListener(this);



        final SearchView searchView = (SearchView)findViewById(R.id.search);
        searchView.setQueryHint(getString(R.string.find_song));

        searchView.setIconified(false);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                try {
                    post(server, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if(newText.length() >=3 ) {
                        post(server, newText.toString());

                    }else{
                        post(server, "");
                        searchView.setIconified(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    void post(String url, String search) throws IOException {
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
                // Error
                Log.d("log_debug","res "+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                Log.d("log_debug","res "+res);
                Gson gson = new Gson();

                Type listType = new TypeToken<ArrayList<Song>>(){}.getType();
                ArrayList<Song> songs = gson.fromJson(res, listType);

                final SongFragment b =  SongFragment.newInstance(songs);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.replace(R.id.fragment_place,b);
                ft.commit();
                //Log.d("log_debug","res "+song.get);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onListFragmentInteraction(Song item) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.playList){
            final LinearLayout layout = (LinearLayout)findViewById(R.id.playListHead);
            LayoutParams params = layout.getLayoutParams();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            if(params.height >height) {
                params.height = height;
            }else{
                int heightDp = getResources().getDisplayMetrics().heightPixels / 2;
                params.height = heightDp;
            }
            layout.setLayoutParams(params);
        }
    }
}
