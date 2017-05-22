package com.gymsic.kara.gymsic.Plugin;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by premkamon on 22/5/2560.
 */

public class Player {

    String song;
    MediaPlayer mediaPlayer;

    public Player(){
        mediaPlayer = new MediaPlayer();


    }

    public void play(String song){
        try {

            mediaPlayer.reset();
            mediaPlayer.setDataSource(song);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
