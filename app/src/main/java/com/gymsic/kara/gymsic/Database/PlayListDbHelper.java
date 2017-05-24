package com.gymsic.kara.gymsic.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by premkamon on 24/5/2560.
 */

public class PlayListDbHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gymsic.db";

    String CREATE_PLAYLIST_TABLE = String.format(
                    "CREATE TABLE %s " +
                    "(%s INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                            " %s TEXT, " +
                            " %s INTEGER)",
                            PlaylistDB.TABLE,
                            PlaylistDB._ID,
                            PlaylistDB.NAME,
                            PlaylistDB.SORT
                    );

    String CREATE_SONG_TABLE = String.format(
            "CREATE TABLE %s " +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s INTEGER, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER, "+
                    " %s TEXT " +
                    ")",
            SongDB.TABLE,
            SongDB._ID,
            SongDB.PLAYLIST_ID,
            SongDB.TITLE,
            SongDB.ARTIST,
            SongDB.SORT,
            SongDB.FILENAME

    );

    public PlayListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("log debug ", "on database create call");
        db.execSQL("DROP TABLE IF EXISTS playlist");
        db.execSQL("DROP TABLE IF EXISTS song");

        Log.d("debug",CREATE_PLAYLIST_TABLE);
        Log.d("debug",CREATE_SONG_TABLE);
        db.execSQL(CREATE_PLAYLIST_TABLE);
        db.execSQL(CREATE_SONG_TABLE);
        insertDefault(db);
    }

    private void insertDefault(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(PlaylistDB.NAME, "DEFAULT");
        values.put(PlaylistDB.SORT, 0);
        db.insert(PlaylistDB.TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL();
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
