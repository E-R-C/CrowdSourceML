package edu.hendrix.huynhem.seniorthesis.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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
            + " TEXT, " + DbContract.LocationEntry.COLUMN_NAME_FEATURE + " TEXT, " + DbContract.LocationEntry.COLUMN_NAME_IMAGE_ROT
            + " DECIMAL(8,5), " + DbContract.LocationEntry.COLUMN_NAME_FASTX + " INTEGER, "
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

}
