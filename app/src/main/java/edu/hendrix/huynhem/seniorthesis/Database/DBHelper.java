package edu.hendrix.huynhem.seniorthesis.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eric on 10/16/17.
 * Reference: https://developer.android.com/training/basics/data-storage/databases.html
 * To use this class call DBHelper mdbHelper = new DBHelper(getContext());
 */

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Patches.db";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.LocationEntry.TABLE_NAME + " ("
            + DbContract.LocationEntry.COLUMN_NAME_IMAGE_NAME + " TEXT, " + DbContract.LocationEntry.COLUMN_NAME_LABEL
            + " TEXT, " + DbContract.LocationEntry.COLUMN_NAME_FEATURE + " INTEGER, " + DbContract.LocationEntry.COLUMN_NAME_IMAGE_ROT
            + " DECIMAL(8,5) " + DbContract.LocationEntry.COLUMN_NAME_FASTX + " INTEGER "
            + DbContract.LocationEntry.COLUMN_NAME_FASTY + " INTEGER) ";
    public static final String SQL_DROP_TABLE = "DROP TABLE " + DbContract.LocationEntry.TABLE_NAME;
    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context c){
        if (instance == null){
            instance = new DBHelper(c.getApplicationContext());
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This application is currently will probably not upgrade, ever.
        // So for now it just deletes everything.
        sqLiteDatabase.execSQL(SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

}
