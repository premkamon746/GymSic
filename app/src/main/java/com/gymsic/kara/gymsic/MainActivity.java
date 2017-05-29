package com.gymsic.kara.gymsic;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Plugin.Playlist;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements SongFragment.OnListFragmentInteractionListener{


    MediaPlayer mediaPlayer = new MediaPlayer();
    Playlist pl;


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



    private void startPlaylistFragment(ArrayList<Song> songs){

        PlaylistFragment plist = new PlaylistFragment();
        plist.setSongs(songs);
        plist.setMediaPlayer(mediaPlayer);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, plist).commit();
    }


    private void startMySongFragment(ArrayList<Song> songs){
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, new MySongFragment()).commit();
    }

    private void startSearchPage(){
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_bottom_view, new SearchFragment()).commit();
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





}
