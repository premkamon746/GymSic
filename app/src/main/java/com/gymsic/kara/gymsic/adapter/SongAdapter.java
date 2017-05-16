package com.gymsic.kara.gymsic.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gymsic.kara.gymsic.R;
import com.gymsic.kara.gymsic.data.Song;

import java.util.ArrayList;

/**
 * Created by premkamon on 16/5/2560.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    ArrayList<Song> songs;
    public SongAdapter(Context context, int resource, ArrayList<Song> songs) {
        super(context, resource);
        this.songs = songs;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Song song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_searchitem, parent, false);
        }
        // Lookup view for data population
        TextView song_name = (TextView) convertView.findViewById(R.id.song);
        // Populate the data into the template view using the data object
        song_name.setText(song.getTitle()+" - "+song.getArtist());
        // Return the completed view to render on screen
        return convertView;
    }

}