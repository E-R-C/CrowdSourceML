package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.PriorityQueue;

import edu.hendrix.huynhem.seniorthesis.Database.BlobDBHelper;
import edu.hendrix.huynhem.seniorthesis.Database.BlobHistogram;
import edu.hendrix.huynhem.seniorthesis.Database.DbContract;
import edu.hendrix.huynhem.seniorthesis.Database.Serializer;
import edu.hendrix.huynhem.seniorthesis.Imaging.BriefPatches;
import edu.hendrix.huynhem.seniorthesis.Imaging.FAST;
import edu.hendrix.huynhem.seniorthesis.Imaging.FASTFeature;
import edu.hendrix.huynhem.seniorthesis.Imaging.Image;

import static edu.hendrix.huynhem.seniorthesis.Models.LearnerSettings.maxDimension;

/**
 *
 */

public class DatabaseBlobClassifier extends AsyncTask<String, Integer, String> implements ModelClassifierInterface{
    final static String LOG_TAG = "DATABASE_BLOB_CLASS";
    BlobDBHelper blobDBHelper;
    Context c;
    SQLiteDatabase writableDB;


    ProgressBar pb;
    public DatabaseBlobClassifier(Context context){
        c = context;
        blobDBHelper = BlobDBHelper.getInstance(context);
        writableDB = blobDBHelper.getWritableDatabase();
    }
    public void setProgressBar(ProgressBar pb){
        this.pb = pb;
    }

    @Override
    protected String doInBackground(String... strings) {
        return classify(strings[0]);
    }

    @Override
    public String classify(String imageLocation) {
        if(writableDB.isOpen()){
            BlobHistogram resultingHist = new BlobHistogram();
            Image image = new Image(imageLocation, maxDimension);
            PriorityQueue<FASTFeature> fastpts = image.getFastPoints();
            String[] projection = {
                    DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB
            };
            String selection = DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + " =  ?";

            for (int i = 0; i < FAST.TOTAL_PATCHES && fastpts.size() > 0 ; i++){
                String hexKey = BriefPatches.calcDescriptorString(image,fastpts.poll());
                String[] args = {hexKey};
                Cursor cursor = writableDB.query(
                        DbContract.RestructuredBlobEntry.TABLE_NAME,
                        projection,         // the columns to return
                        selection,          // columns for WHERE clause
                        args,               // values for WHERE clause
                        null,               // group rows
                        null,               // filter
                        null,                // Sort order
                        "1"                 // limit to 1
                );
                cursor.moveToNext();
                byte[] blobBytes = cursor.getBlob(cursor.getColumnIndex(DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB));

                try {
                    BlobHistogram bh = Serializer.deserializeBlob(blobBytes);
                    resultingHist = resultingHist.mergeMakeNewCopy(bh);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            String result = resultingHist.getMaxLabel();
            if (result == null){
                return "Unknown";
            }
            return result;

        }
        Log.e(LOG_TAG,"Database is not open: CLASSIFY");
        return "COULD NOT OPEN DATABASE";
    }

    @Override
    public ModelClassifierInterface fromString() {
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(c," I predict the image is " + s, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, s);
    }
}
