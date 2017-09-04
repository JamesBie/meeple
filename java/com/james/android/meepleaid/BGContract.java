package com.james.android.meepleaid;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 100599223 on 8/10/2017.
 */

public class BGContract {
    private BGContract(){}


    //contract
    public static final String CONTENT_AUTHORITY = "com.james.android.boardgamecollection.boardgamecollection";

    //concatanate the previous with the base to get a uri that everything will use
    // uri parse will take the string name and make a uri object

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_BG = "boardgamecollection";
    // inner class that defines constant values for the BG database table.
    // each entry in the table represents a single BG

    public static final class BGEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BG);

        /*
        The MIME type of the (@link content_URI) for a list of bg
         */

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BG;

        /**
         * The MIME type of the {@Link #Content_URI} for a single pet
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BG;

        // database name for bg
        public static final String TABLE_NAME = "boardgamecollection";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BG_NAME = "name";
        public static final String COLUMN_BG_MINPLAYER = "min";
        public static final String COLUMN_BG_MAXPLAYER = "max";



    }






}
