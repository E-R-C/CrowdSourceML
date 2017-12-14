package edu.hendrix.huynhem.seniorthesis.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 *
 */

public class BlobDBHelper extends SQLiteOpenHelper {
    static BlobDBHelper instance;
    public static final String DATABASE_NAME = "Blobs.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_BLOB_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.RestructuredBlobEntry.TABLE_NAME + " ("
            + DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + " TEXT PRIMARY KEY, " + DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB
            + " BLOB) ";
    public static final String SQL_CREATE_LOCATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.LocationsEntry.TABLE_NAME + " ("
            + DbContract.LocationsEntry.COLUMN_NAME_PLACE + " TEXT) ";
    private BlobDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static synchronized BlobDBHelper getInstance(Context c){
        if (instance == null){
            instance = new BlobDBHelper(c.getApplicationContext());
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_BLOB_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

    public void insertNewLocation(String location){
        String loc = location.toUpperCase();
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] projection = {DbContract.LocationsEntry.COLUMN_NAME_PLACE};
        String selection = loc;
        String[] args  = {loc};
        Cursor cursor = sqlDB.query(
                DbContract.LocationsEntry.TABLE_NAME,
                projection,
                selection,          // columns for WHERE clause
                args,               // values for WHERE clause
                null,               // group rows?
                null,               // filter
                null,               // Sort order
                "1"                 // Sort Limit
        );
        if (cursor.getCount() == 0){
            ContentValues cv = new ContentValues();
            cv.put(DbContract.LocationsEntry.COLUMN_NAME_PLACE,loc);
            sqlDB.insert(DbContract.LocationsEntry.TABLE_NAME, null, cv);
        }
    }
    public Cursor getLocationsCursor(){
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] projection  = {DbContract.LocationsEntry.COLUMN_NAME_PLACE};
        return sqlDB.query(
                DbContract.LocationsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                "DESC"
        );
    }
}
