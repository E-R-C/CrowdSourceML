package edu.hendrix.huynhem.seniorthesis.Database;

import android.provider.BaseColumns;

/**
 * Created by eric on 10/15/17.
 * Reference: https://developer.android.com/training/basics/data-storage/databases.html
 */

public final class DbContract {

    private DbContract(){}

    public static class ImageLabelEntry implements BaseColumns{
        public static final String TABLE_NAME = "imageAndLabelTable";
        public static final String COLUMN_NAME_FILE = "fileURL";
        public static final String COLUMN_NAME_LABEL = "location";
    }
    public static class RestructuredBlobEntry implements  BaseColumns{
        public static final String TABLE_NAME = "tableWithBlobs";
        public static final String COLUMN_NAME_FEATURE = "hexCode";
        public static final String COLUMN_NAME_COUNTBLOB = "blobCount";
    }
    public static class LocationsEntry implements  BaseColumns{
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_NAME_PLACE = "location";
    }

}
