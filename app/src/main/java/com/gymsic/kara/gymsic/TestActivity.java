package com.gymsic.kara.gymsic;

import android.app.FragmentManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gymsic.kara.gymsic.Interface.OnRecycleViewClick;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Service.User;
import com.gymsic.kara.gymsic.Plugin.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SongFragment.OnListFragmentInteractionListener {

    Playlist pl;
    int currentSong = 0;
    PlaylistFragment plist = new PlaylistFragment();

    private User user;

    MediaPlayer mediaPlayer = new MediaPlayer();

    PlayerFragment player = new PlayerFragment();
    OnRecycleViewClick onRecycleViewClick = new OnRecycleViewClick() {
        @Override
        public void onRecycleViewClick(View view, int position,final ArrayList<Song> songs) {


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
        setContentView(R.layout.activity_test);


        user = new User(getApplicationContext());
        gotoLogin();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        pl = new Playlist(TestActivity.this);
        startRecordPage();

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
                            case R.id.item_record:
                                startRecordPage();
                                break;
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

    private void gotoLogin(){
        if( !user.isLogin() )
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


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

    RecordFragment record = new RecordFragment();
    private void startRecordPage(){
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, record ).commit();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().beginTransaction().hide(playList).commit();
//            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }

    private int getCurrentSong() {
        return ++currentSong;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            if(user.logout()) {
                gotoLogin();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Song item) {

    }

   @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }/* */

    @Override
    protected void onResume() {
        super.onResume();
//        if (mediaPlayer != null && !mediaPlayer .isPlaying()) {
//            mediaPlayer.start();
//        }

    }
}
