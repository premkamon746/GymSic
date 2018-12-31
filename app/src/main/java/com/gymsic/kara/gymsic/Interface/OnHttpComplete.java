package com.gymsic.kara.gymsic.Interface;

import com.google.gson.Gson;
import com.gymsic.kara.gymsic.Model.Song;

public interface OnHttpComplete {
    void onTaskCompleted(Gson gson);
}
