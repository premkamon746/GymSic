package com.gymsic.kara.gymsic.Plugin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gymsic.kara.gymsic.Database.PlayListDbHelper;
import com.gymsic.kara.gymsic.Model.Song;
import com.gymsic.kara.gymsic.Database.SongDB;

import java.util.ArrayList;


/**
 * Created by premkamon on 20/5/2560.
 */

public class Playlist {

    Activity activity;
    SQLiteDatabase db;

    public static SharedPreferences mPrefs;
    public Playlist(Activity activity){
        this.activity = activity;
        PlayListDbHelper pldb = new PlayListDbHelper(activity.getApplicationContext());
        db = pldb.getReadableDatabase();
    }

    public  void set(Song song){
        ContentValues values = new ContentValues();
        values.put(SongDB.TITLE, song.getTitle());
        values.put(SongDB.ARTIST, song.getArtist());
        values.put(SongDB.PLAYLIST_ID, 1);
        values.put(SongDB.SORT, 0);
        values.put(SongDB.FILENAME, song.getFilename());
        db.insert(SongDB.TABLE, null, values);

    }

    public  ArrayList<Song> get(){

        String[] projection = {
                SongDB.TITLE,
                SongDB.ARTIST,
                SongDB.SORT,
                SongDB.FILENAME
        };

        String selection = SongDB.PLAYLIST_ID + " = ?";
        String[] selectionArgs = { "1" };
        String sortOrder =
                SongDB._ID + " ASC";

        Cursor cursor = db.query(
                SongDB.TABLE,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList<Song> mySong = new ArrayList<Song>();
        int i = 0;
        while(cursor.moveToNext()) {
            Song song = new Song();
            String title = cursor.getString(0);
            String artist = cursor.getString(1);
            int sort = cursor.getInt(2);
            String filename = cursor.getString(3);
            song.setTitle(title);
            song.setArtist(artist);
            song.setSort(sort);
            song.setFilename(filename);
            mySong.add(song);
            i++;
        }
        cursor.close();
        return  mySong;
    }
}
