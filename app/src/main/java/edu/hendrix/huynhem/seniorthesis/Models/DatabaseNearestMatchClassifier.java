package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class DatabaseNearestMatchClassifier extends AsyncTask<String, Integer, String> implements ModelClassifierInterface {
    final static String LOG_TAG = "DATABASE_NEAREST_CLASS";
    Context c;
    DBHelper dbHelper;
    SQLiteDatabase writableDB;
    ProgressBar pb;
    int arbitraryLimitForClassification = 1000;

    public DatabaseNearestMatchClassifier(Context c){
        this.c = c;
        dbHelper = DBHelper.getInstance(c);
        writableDB = dbHelper.getWritableDatabase();
    }
    public void setProgressBar(ProgressBar pb){
        this.pb = pb;
    }
    @Override
    public String classify(String imageLocation) {
        if(writableDB.isOpen()){
            Image image = new Image(imageLocation, DatabaseNearestMatchTrainer.maxDimension);
            PriorityQueue<FASTFeature> fastpts = image.getFastPoints();
            HashMap<String,Integer> counts = new HashMap<>();
            String[] projection = {
                    DbContract.LocationEntry.COLUMN_NAME_IMAGE_NAME,
                    DbContract.LocationEntry.COLUMN_NAME_LABEL
            };
            String selection = DbContract.LocationEntry.COLUMN_NAME_FEATURE + " =  ?";

            for (int i = 0; i < FAST.TOTAL_PATCHES && fastpts.size() > 0 ; i++){
                String hexKey = BriefPatches.calcDescriptorString(image,fastpts.poll());
                String[] args = {hexKey};
                int chunkSize = 10000;
                int lastCursorSize = chunkSize;
                int currentOffset = 0;
                Cursor cursor = null;
                while(lastCursorSize == chunkSize){
                    String limitString = currentOffset + ", " + chunkSize;
                    cursor = writableDB.query(
                            DbContract.LocationEntry.TABLE_NAME,
                            projection,         // the columns to return
                            selection,          // columns for WHERE clause
                            args,               // values for WHERE clause
                            null,               // group rows?
                            null,               // filter
                            null,                // Sort order
                            limitString
                    );
                    lastCursorSize = cursor.getCount();
                    while(cursor.moveToNext()){
                        String label = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.LocationEntry.COLUMN_NAME_LABEL));
                        if(!counts.containsKey(label)){
                            counts.put(label,0);
                        }
                        counts.put(label,counts.get(label) + 1);
                        if (counts.get(label) + 1 > arbitraryLimitForClassification){
                            return label;
                        }
                    }
                    Log.d(LOG_TAG, "Last Query Size" + lastCursorSize);
                    cursor.close();

                }
                pb.setMax(FAST.TOTAL_PATCHES);
                pb.setProgress(i);
                publishProgress();
            }
            String result = getMajority(counts);
            if (result == null){
                return "Unknown";
            }
            return result;

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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(c," I predict the image is " + s, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, s);

    }

    @Override
    public ModelClassifierInterface fromString() {
        return null;
    }

    @Override
    protected String doInBackground(String... strings) {
        return classify(strings[0]);
    }
}
