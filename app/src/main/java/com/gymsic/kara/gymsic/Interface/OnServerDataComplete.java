package com.gymsic.kara.gymsic.Interface;

import com.gymsic.kara.gymsic.Model.Song;

import java.util.ArrayList;

/**
 * Created by premkamon on 29/5/2560.
 */

public interface OnServerDataComplete {
    public void onServerDataComplete(ArrayList<Song> songs);
}
