package com.gymsic.kara.gymsic;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gymsic.kara.gymsic.Interface.OnRecycleViewClick;
import com.gymsic.kara.gymsic.Interface.OnTaskComplete;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Plugin.Download;
import com.gymsic.kara.gymsic.Plugin.Playlist;

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
        implements SongFragment.OnListFragmentInteractionListener{

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public String server = "http://192.168.1.33:3000/";
    private TextView playlistHead;
    MediaPlayer mediaPlayer = new MediaPlayer();
    PlaylistFragment playList;

    OnRecycleViewClick onRecVwClick;
    SongFragment songFram;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDefaultData();
        //playlistHead = (TextView)findViewById(R.id.playList);
        //playlistHead.setOnClickListener(this);





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

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);

        bottomNavigationView.inflateMenu(R.menu.bottom_menu);
        bottomNavigationView.setItemBackgroundResource(R.color.colorPrimary);

//        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.colorAccent));
//        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.colorGray));
//
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {

                        item.setChecked(true);

                        switch (item.getItemId()) {
                            case R.id.item_search:
                                getFragmentManager().beginTransaction().show(playList).commit();
                                break;
                            case R.id.item_player:
                                // do this event
                                break;
                            case R.id.item_mysong:
                                // do this event
                                break;
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
                    if(newText.length() >=2 ) {
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

                if( songFram instanceof  SongFragment){

                }else {
                    songFram = new SongFragment();
                }

                songFram.setOnRecycleViewClick(onRecVwClick);
                songFram.setSong(songs);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.detach(songFram);
                ft.attach(songFram);
                ft.replace(R.id.fragment_place,songFram);

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




    public void loadDefaultData(){
        //cmp = setOnDownloadSongSuccess();
        onRecVwClick = setOnSearchClick();
        loadDefaultPlayList();
    }

    public void loadDefaultPlayList(){
        Playlist pl = new Playlist(this);
        ArrayList<Song> songs = pl.get();
        Log.d("song length",songs.size()+"");
        startPlaylistFragment(songs);
    }

    // Download song sucess
    public OnTaskComplete setOnDownloadSongSuccess(){
        //final LinearLayout layout = (LinearLayout)findViewById(R.id.playListHead);
        //get haft
        //final int heightDp = getResources().getDisplayMetrics().heightPixels / 2;

        return new OnTaskComplete() {
            @Override
            public void onTaskCompleted(Song song) {
                Playlist pl = new Playlist(MainActivity.this);
                pl.set(song);
                ArrayList<Song> songs  = pl.get();
                //layout.getLayoutParams().height = heightDp;
                startPlaylistFragment(songs);
            }
        };

    }

    public OnRecycleViewClick setOnSearchClick(){
        return new OnRecycleViewClick(){
                @Override
                public void onRecycleViewClick(View view,int position,ArrayList<Song> songs){
                    ProgressBar pb = (ProgressBar)view.findViewById(R.id.progressBar);
                    OnTaskComplete cmp = setOnDownloadSongSuccess();
                    new Download(MainActivity.this,pb,songs.get(position),cmp).execute("http://192.168.1.33/mp3db/"+songs.get(position).getFilename());
                }
        };

    }

    private void startPlaylistFragment(ArrayList<Song> songs){

        if(playList instanceof PlaylistFragment){

        }else{
            playList = new PlaylistFragment();
        }
        Log.d("log on load ", "add fragment");
        playList.setMediaPlayer(mediaPlayer);
        playList.setSongs(songs);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down);
        ft.addToBackStack(null);
        ft.detach(playList);
        ft.attach(playList);
        ft.replace(R.id.fragment_playlist,playList);
        ft.show(playList);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().beginTransaction().hide(playList).commit();
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}
