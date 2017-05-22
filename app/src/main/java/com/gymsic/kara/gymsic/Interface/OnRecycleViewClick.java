package com.gymsic.kara.gymsic.Interface;

import android.view.View;

import com.gymsic.kara.gymsic.Model.Song;

import java.util.ArrayList;

/**
 * Created by premkamon on 22/5/2560.
 */

public interface OnRecycleViewClick {
    public void onRecycleViewClick(View view, int position,ArrayList<Song> songs);
}
