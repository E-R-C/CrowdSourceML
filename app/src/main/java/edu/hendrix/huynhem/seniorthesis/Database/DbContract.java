package edu.hendrix.huynhem.seniorthesis.Database;

import android.provider.BaseColumns;

/**
 * Created by eric on 10/15/17.
 * Reference: https://developer.android.com/training/basics/data-storage/databases.html
 */

public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract(){}

    // This table keeps track of patch number and the image file
    public static class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_IMAGE_NAME = "imgfile";
        public static final String COLUMN_NAME_FEATURE = "patchNum";
        public static final String COLUMN_NAME_BUILDING = "building";
        public static final String COLUMN_NAME_GPS_LONG = "longitude";
        public static final String COLUMN_NAME_GPS_LAT = "latitude";
        public static final String COLUMN_NAME_IMAGE_ROT = "rotation";
    }

}
