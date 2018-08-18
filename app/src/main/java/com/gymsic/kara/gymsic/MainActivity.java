package com.gymsic.kara.gymsic;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.gymsic.kara.gymsic.Interface.OnRecycleViewClick;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Plugin.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements SongFragment.OnListFragmentInteractionListener{



    Playlist pl;
    int currentSong = 0;
    PlaylistFragment plist = new PlaylistFragment();

    PlayerFragment player = new PlayerFragment();
    OnRecycleViewClick onRecycleViewClick = new OnRecycleViewClick() {
        @Override
        public void onRecycleViewClick(View view, int position,final  ArrayList<Song> songs) {


            if(!player.isAdded()) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .add(R.id.fragment_bottom_view, player)
                        .setCustomAnimations(R.animator.slide_up, R.animator.slide_up)
                        .commit();
            }else{
                getFragmentManager().beginTransaction().show(player).commit();
            }

            setCurrentSong(position);
            playSong(mediaPlayer,position,songs);
            //int songIndex = position;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mp) {
                    int position = getCurrentSong();
                    playSong(mp ,position,songs);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pl = new Playlist(MainActivity.this);
        startSearchPage();

        //mediaPlayer.setVolume(0,100);
        //startPlaylistFragment(pl.get());

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
                                startSearchPage();
                                break;
                            case R.id.item_player:
                                startPlaylistFragment(pl.get());
                                break;
                            case R.id.item_mysong:
                                startMySongFragment(pl.get());
                                break;
                        }
                        return false;
                    }
                });

    }




    @Override
    public void onListFragmentInteraction(Song item) {

    }


    MediaPlayer mediaPlayer = new MediaPlayer();
    private void startPlaylistFragment(ArrayList<Song> songs){

        plist.setSongs(songs);
        plist.setOnClickListener(onRecycleViewClick);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, plist).commit();
    }

    MySongFragment mySong = new MySongFragment();
    private void startMySongFragment(ArrayList<Song> songs){
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, mySong).commit();
    }

    SearchFragment search = new SearchFragment();
    private void startSearchPage(){
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, search ).commit();
    }

    public void playSong(MediaPlayer mp, int position,ArrayList<Song> songs){
        Log.d("log position player ",songs.size()+ " : "+position);
        if(songs.size() > position) {
            String songFileName = songs.get(position).getFilename();
            File dir = getExternalFilesDir("music");
            String song =  dir + songFileName;

            File file = new File(song);
            if (file.exists()) {
                mp.reset();
                try {
                    mp.setDataSource(song);
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            mp.start();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.prepareAsync();

            }
        }
    }



    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().beginTransaction().hide(playList).commit();
//            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private int getCurrentSong() {
        return ++currentSong;
    }

    private void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mediaPlayer != null && !mediaPlayer .isPlaying()) {
//            mediaPlayer.start();
//        }

    }



}
