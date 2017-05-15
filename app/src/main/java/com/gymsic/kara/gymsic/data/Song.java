package com.gymsic.kara.gymsic.data;

import java.io.Serializable;

/**
 * Created by premkamon on 14/5/2560.
 */

public class Song {
    private String _id;
    private String name;
    private String artist;
    private String duration;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
