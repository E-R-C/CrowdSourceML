package edu.hendrix.huynhem.seniorthesis.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */

public class BlobDBHelper extends SQLiteOpenHelper {
    static BlobDBHelper instance;
    private static final String DATABASE_NAME = "Blobs.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_BLOB_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.RestructuredBlobEntry.TABLE_NAME + " ("
            + DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + " TEXT PRIMARY KEY, " + DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB
            + " BLOB) ";
    private static final String SQL_CREATE_FILE_TABLE = "CREATE TABLE IF NOT EXISTS " +  DbContract.ImageLabelEntry.TABLE_NAME + "(" + DbContract.ImageLabelEntry.COLUMN_NAME_FILE + " TEXT PRIMARY KEY, " + DbContract.ImageLabelEntry.COLUMN_NAME_LABEL + " TEXT) ";
    private static final String SQL_CREATE_LOCATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbContract.LocationsEntry.TABLE_NAME + " ("
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
        sqLiteDatabase.execSQL(SQL_CREATE_FILE_TABLE);
        Log.d("DB", "ONCreate dbCreated");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_CREATE_BLOB_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FILE_TABLE);
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

    public void insertNewFile(String filename, String label){
        SQLiteDatabase sqlDB = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.ImageLabelEntry.COLUMN_NAME_FILE, filename);
        cv.put(DbContract.ImageLabelEntry.COLUMN_NAME_LABEL, label);
        sqlDB.insert(DbContract.ImageLabelEntry.TABLE_NAME, null, cv);
    }
    public void insertNewLocation(String location){
        String loc = location.toUpperCase();
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] projection = {DbContract.LocationsEntry.COLUMN_NAME_PLACE};
        String selection = DbContract.LocationsEntry.COLUMN_NAME_PLACE + " = ?";
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
    public List<String> getLocations(){
        onCreate(this.getWritableDatabase());
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] projection  = {DbContract.LocationsEntry.COLUMN_NAME_PLACE};
        Cursor c = sqlDB.query(
                DbContract.LocationsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                DbContract.LocationsEntry.COLUMN_NAME_PLACE + " ASC"
        );
        ArrayList<String> result = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(c.getString(c.getColumnIndex(projection[0])));
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }
    public HashMap<String, String> getFilesAndLabels(){
        SQLiteDatabase sqlDB = getWritableDatabase();
        String[] projection = {DbContract.ImageLabelEntry.COLUMN_NAME_FILE, DbContract.ImageLabelEntry.COLUMN_NAME_LABEL};
        Cursor c = sqlDB.query(
                DbContract.ImageLabelEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        HashMap<String, String> result = new HashMap<>();
        if(c.moveToFirst()){
            do {
                String filename = c.getString(c.getColumnIndex(DbContract.ImageLabelEntry.COLUMN_NAME_FILE));
                String label = c.getString(c.getColumnIndex(DbContract.ImageLabelEntry.COLUMN_NAME_LABEL));
                result.put(filename,label);
            } while (c.moveToNext());
        }
        return result;
    }
    public HashMap<String, String> getFilesAndLabels(List<String> files){
        // There could potentially be a time where there are too many files to query, the default SQLite query limit is 1,000,000 characters,
        HashMap<String, String> result = new HashMap<>();
        SQLiteDatabase sqlDB = getWritableDatabase();
        String[] selectionArgs = (String[]) files.toArray();
        String[] projection = {DbContract.ImageLabelEntry.COLUMN_NAME_FILE, DbContract.ImageLabelEntry.COLUMN_NAME_LABEL};
        String selectionQuery = makeSelectionQuery(DbContract.ImageLabelEntry.COLUMN_NAME_FILE, selectionArgs.length);
        if (selectionQuery.length() > 750000){
            List<String> l1 = files.subList(0, files.size()/2);
            List<String> l2 = files.subList(files.size()/2, files.size());
            result.putAll(getFilesAndLabels(l1));
            result.putAll(getFilesAndLabels(l2));
            return result;
        } else {
            Cursor c = sqlDB.query(
                    DbContract.ImageLabelEntry.TABLE_NAME,
                    projection,
                    selectionQuery,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            if(c.moveToFirst()){
                do {
                    String filename = c.getString(c.getColumnIndex(DbContract.ImageLabelEntry.COLUMN_NAME_FILE));
                    String label = c.getString(c.getColumnIndex(DbContract.ImageLabelEntry.COLUMN_NAME_LABEL));
                    result.put(filename,label);
                } while (c.moveToNext());
            }
            return result;
        }

    }
    private String makeSelectionQuery(String columnname, int numFiles){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < numFiles - 1; i++){
            sb.append(columnname);
            sb.append(" = ? OR ");
        }
        sb.append(columnname);
        sb.append(" = ? ");
        return sb.toString();
    }
}
