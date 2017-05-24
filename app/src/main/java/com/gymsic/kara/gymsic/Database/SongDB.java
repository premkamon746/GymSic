package com.gymsic.kara.gymsic.Database;

import android.provider.BaseColumns;

/**
 * Created by premkamon on 24/5/2560.
 */

public class SongDB  implements BaseColumns {
    public static final String TABLE = "song";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String SORT = "sort";
    public static final String FILENAME = "filename";
}
