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
        public static final String COLUMN_NAME_FEATURE = "hexCode";
        public static final String COLUMN_NAME_LABEL = "building";
        public static final String COLUMN_NAME_IMAGE_ROT = "rotation";
        public static final String COLUMN_NAME_SCALE = "scale";
        public static final String COLUMN_NAME_FASTX = "fastx";
        public static final String COLUMN_NAME_FASTY = "fasty";
    }

}
