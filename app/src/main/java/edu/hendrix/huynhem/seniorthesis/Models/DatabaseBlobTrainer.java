package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
import static edu.hendrix.huynhem.seniorthesis.Models.LearnerSettings.numRot;
import static edu.hendrix.huynhem.seniorthesis.Models.LearnerSettings.numScales;


/**
 *
 */

public class DatabaseBlobTrainer extends AsyncTask<String, Void, Boolean> implements ModelTrainerInterface {
    BlobDBHelper blobDBHelper;
    Context c;
    SQLiteDatabase writableDB;
    final static String LOG_TAG = "BLOBDB_TRAINER";
    int totalLoops = numScales * numRot * FAST.TOTAL_PATCHES;
    int loopsDone = 0;

    ProgressBar pb;
    public void setPb(ProgressBar pb){
        this.pb = pb;
        this.pb.setMax(totalLoops);
    }
    public DatabaseBlobTrainer(Context context){
        c = context;
        blobDBHelper = BlobDBHelper.getInstance(context);
        writableDB = blobDBHelper.getWritableDatabase();
    }
    @Override
    protected Boolean doInBackground(String... strings) {
        String filelocation = strings[0];
        String label = strings[1];
        train(filelocation,label);
        return true;
    }

    @Override
    public void train(String imageLocation, String label) {
        HashMap<String, BlobHistogram> tempBlobs = new HashMap<>();
        Log.d(LOG_TAG, "START OF TRAINING");
        double startTime = System.currentTimeMillis();
        if(writableDB.isOpen()){
            float scaleStep = 0.5f/numScales; // 1 -  the left number is how big the final scale will be.
            Image image = new Image(imageLocation,maxDimension);
            for(int scale = 0; scale < numScales; scale++){
                Image scaledImage = image.scaleImage(1 - (scale * scaleStep));
                for(int rot = 0; rot < numRot; rot++){
                    float rotation = ((180.0f/numRot)*rot) - 90;
                    Image tempImage = scaledImage.rotateImage(rotation);
                    PriorityQueue<FASTFeature> fastPoints = FAST.calculateFASTPoints(tempImage);
                    for(int i = 0; i < FAST.TOTAL_PATCHES && fastPoints.peek() != null; i++){
                        FASTFeature point = fastPoints.poll();
                        String featureString = BriefPatches.calcDescriptorString(image,point);
                        if (!tempBlobs.containsKey(featureString)) {
                            tempBlobs.put(featureString, new BlobHistogram());
                        }
                        BlobHistogram bh = tempBlobs.get(featureString);
                        bh.bump(label);
                        loopsDone++;
                    }
                    this.publishProgress();
                }
                Log.d(LOG_TAG, "Finished Image scale " + (1 - (scale * scaleStep)));
            }
            insertToDatabase(tempBlobs);
            Log.d(LOG_TAG, "FINISHED TRAINING " + imageLocation + " ms: " + (System.currentTimeMillis() - startTime));

        } else {
            Log.e(LOG_TAG,"Database is not open: TRAIN");
        }
    }
    private void insertToDatabase(HashMap<String, BlobHistogram> tempMap) {
        String[] projection = {
                DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB
        };
        String selection = DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + "=?";
        for(String key : tempMap.keySet()){
            // Grab the right blob, update, and put it back in
            String[] args = new String[]{key};
            Cursor cursor = writableDB.query(
                    DbContract.RestructuredBlobEntry.TABLE_NAME,
                    projection,
                    selection,          // columns for WHERE clause
                    args,               // values for WHERE clause
                    null,               // group rows?
                    null,               // filter
                    null,               // Sort order
                    "1"                 // Sort Limit
            );
            BlobHistogram newBlob = tempMap.get(key);
            if (cursor.getCount() > 0){
                // Update Existing blob
                Log.d(LOG_TAG, "COLUMNS: " + Arrays.toString(cursor.getColumnNames()));
                cursor.moveToFirst();
                byte[] oldBlob = cursor.getBlob(cursor.getColumnIndex(DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB));
                try {
                    BlobHistogram oldBlobHist = Serializer.deserializeBlob(oldBlob);
                    newBlob = oldBlobHist.mergeMakeNewCopy(newBlob);
                    ContentValues cv = new ContentValues();
                    cv.put(DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB,Serializer.serialize(newBlob));
                    writableDB.update(
                            DbContract.RestructuredBlobEntry.TABLE_NAME,
                            cv,
                            DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + "=?",
                            args
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "ERROR DESERIALIZING BLOB");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "WAS NOT THE BLOB WE WERE EXPECTING??? IS THE PROJECTION AND CURSOR RIGHT?");
                } catch (NullPointerException e){
                    Log.d(LOG_TAG, "CANNOT FIND THE RIGHT KEY, HOW DID I GET HERE?");
                }
            } else {
                try {
                    // insert new blob
                    ContentValues cv = new ContentValues();
                    cv.put(DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE,key);
                    cv.put(DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB,Serializer.serialize(newBlob));
                    writableDB.insert(
                            DbContract.RestructuredBlobEntry.TABLE_NAME,
                            null,
                            cv
                    );

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
        }
    }

    @Override
    public ModelTrainerInterface fromString() {
        return null;
    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        pb.setMax(totalLoops);
        pb.setProgress(loopsDone);
        super.onProgressUpdate(values);
    }
}
