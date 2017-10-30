package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.PriorityQueue;

import edu.hendrix.huynhem.seniorthesis.Database.DBHelper;
import edu.hendrix.huynhem.seniorthesis.Database.DbContract;
import edu.hendrix.huynhem.seniorthesis.Imaging.BriefPatches;
import edu.hendrix.huynhem.seniorthesis.Imaging.FAST;
import edu.hendrix.huynhem.seniorthesis.Imaging.FASTFeature;
import edu.hendrix.huynhem.seniorthesis.Imaging.Image;

/**
 *
 */

public class DatabaseNearestMatch implements ModelInterface {
    static final String LOG_TAG = "DATABASE_NEAREST_MATCH";
    Context c;
    DBHelper dbHelper;
    SQLiteDatabase writableDB;

    // The BRIEF paper said that it can handle about 10-15 degrees in rotation. Therefore
    // Since I am assuming that nobody is going to take an upside down picture and instead only take right side up pictures,
    // We only need to account for rotations from 0 to 180 degrees. 20 is a high number, and probably can be reduced down to 12
    // Since 180/15 == 12
    static final int numRot = 20;
    static final int numScales = 10;

    DatabaseNearestMatch(Context context){
        c = context;
        dbHelper = DBHelper.getInstance(context);
        writableDB = dbHelper.getWritableDatabase();
    }
    /*
     * I want to rotate each image from -90 to 90
     * I want to scale each image from 100% to 20%
     */
    @Override
    public void train(String imageLocation, String label) {
        if(writableDB.isOpen()){
            float scaleStep = 1/numScales;
            Image image = new Image(imageLocation);
            PriorityQueue<FASTFeature> fastPoints = FAST.calculateFASTPoints(image);
            for(int scale = 0; scale < numScales; scale++){
                Image scaledImage = image.scaleImage(1 - (scale * scaleStep));
                for(int rot = 0; rot < numRot; rot++){
                    Image tempImage = scaledImage.rotateImage((180/numRot) - 90);
                    for(int i = 0; i < FAST.TOTAL_PATCHES; i++){
                        FASTFeature point = fastPoints.poll();
                        ContentValues content = new ContentValues();
                        content.put(DbContract.LocationEntry.COLUMN_NAME_LABEL, label);
                        content.put(DbContract.LocationEntry.COLUMN_NAME_IMAGE_NAME, imageLocation);
                        content.put(DbContract.LocationEntry.COLUMN_NAME_FASTX, point.X());
                        content.put(DbContract.LocationEntry.COLUMN_NAME_FASTY, point.Y());
                        content.put(DbContract.LocationEntry.COLUMN_NAME_FEATURE, BriefPatches.calculateDescriptor(image,point).toString(16));
                        content.put(DbContract.LocationEntry.COLUMN_NAME_IMAGE_ROT, rot);
                        writableDB.insert(DbContract.LocationEntry.TABLE_NAME,null,content);
                    }
                }
            }
        } else {
            Log.e(LOG_TAG,"Database is not open: TRAIN");
        }
    }
    /*
     * I have the option to either scale the incoming image and find all the matching features, or
     * I can scale and rotate the training data. for now I am scaling and rotating the training data
     * The downsides to scaling the training data is that it takes significantly more space, but
     * in theory, training should be faster. If we scale the tobeclassified image, we would
     * Make the user wait longer when identifying. The user most likely would be upset at waiting
     * longer for id than training.
     *
     * Another Idea I currently have is to maybe scale classify's input by 50% as well. but Not rotate
     */
    @Override
    public String classify(String imageLocation) {
        if(writableDB.isOpen()){
            Image image = new Image(imageLocation);
            PriorityQueue<FASTFeature> fastpts = image.getFastPoints();
            HashMap<String,Integer> counts = new HashMap<>();
            String[] projection = {
                    DbContract.LocationEntry.COLUMN_NAME_IMAGE_NAME,
                    DbContract.LocationEntry.COLUMN_NAME_LABEL
            };
            String selection = DbContract.LocationEntry.COLUMN_NAME_FEATURE + " =  ?";

            for(int i = 0; i < FAST.TOTAL_PATCHES; i++){
                String hexKey = BriefPatches.calculateDescriptor(image,fastpts.poll()).toString(16);
                String[] args = {hexKey};
                Cursor cursor = writableDB.query(
                        DbContract.LocationEntry.TABLE_NAME,
                        projection,         // the columns to return
                        selection,          // columns for WHERE clause
                        args,               // values for WHERE clause
                        null,               // group rows?
                        null,               // filter
                        null                // Sort order
                );
                while(cursor.moveToNext()){
                    String label = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.LocationEntry.COLUMN_NAME_LABEL));
                    if(!counts.containsKey(label)){
                        counts.put(label,0);
                    }
                    counts.put(label,counts.get(label) + 1);
                }
                cursor.close();
            }
            return getMajority(counts);

        }
        Log.e(LOG_TAG,"Database is not open: CLASSIFY");
        return "COULD NOT OPEN DATABASE";
    }

    private static String getMajority(HashMap<String, Integer> map){
        int maxVal = 0;
        String maxkey = null;
        for(String key: map.keySet()){
            if(map.get(key) > maxVal){
                maxVal = map.get(key);
                maxkey = key;
            }
        }
        return maxkey;
    }
    @Override
    public ModelInterface fromString() {
        return null;
    }
}
