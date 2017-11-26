package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

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

public class DatabaseNearestMatchTrainer extends AsyncTask<String, Void, Boolean> implements ModelTrainerInterface {
    static final String LOG_TAG = "DATABASE_NEAREST_MATCH";
    Context c;
    DBHelper dbHelper;
    SQLiteDatabase writableDB;
    int loopsDone = 0;
    ProgressBar pb;
    int totalLoops = numScales * numRot * FAST.TOTAL_PATCHES;
    public static final int maxDimension = 200;

    // The BRIEF paper said that it can handle about 10-15 degrees in rotation. Therefore
    // Since I am assuming that nobody is going to take an upside down picture,
    // We only need to account for rotations from 0 to 180 degrees. Since 180/15 == 12, we choose 12.
    static final int numRot = 3;
    static final int numScales = 1;

    public DatabaseNearestMatchTrainer(Context context){
        c = context;
        dbHelper = DBHelper.getInstance(context);
        writableDB = dbHelper.getWritableDatabase();
    }
    /*
     * I want to rotate each image from -90 to 90
     * I want to scale each image from 100% to 20%
     */
    public void setPb(ProgressBar pb){
        this.pb = pb;
        this.pb.setMax(totalLoops);
    }
    @Override
    public void train(String imageLocation, String label) {
        // THIS IS TAKING 5 MINUTES TO CALCULATE ONE SET OF FASTPOINTS. THERE ARE 60 TOTAL VALUES TO CALCULATE, AT THIS RATE IT WILL TAKE 60 * 5 MINUTES OR 300 MINUTES OR 5 HOURS TO COMPLETE.
        // THIS IS UNNACCEPTABLE, AND I PROBABLY NEED TO CONSULT DR. FERRER BEFORE MOVING ON.
        Log.d(LOG_TAG, "START OF TRAINING");
        loopsDone = 0;
        double startTime = System.currentTimeMillis();
        if(writableDB.isOpen()){
            float scaleStep = 0.8f/numScales;
            Image image = new Image(imageLocation,maxDimension);

            for(int scale = 0; scale < numScales; scale++){
                Image scaledImage = image.scaleImage(1 - (scale * scaleStep));
                for(int rot = 0; rot < numRot; rot++){
                    float rotation = ((180.0f/numRot)*rot) - 90;
                    Image tempImage = scaledImage.rotateImage(rotation);
                    PriorityQueue<FASTFeature> fastPoints = FAST.calculateFASTPoints(tempImage);
                    for(int i = 0; i < FAST.TOTAL_PATCHES; i++){
                        FASTFeature point = fastPoints.poll();
                        if(point != null){
                            ContentValues content = new ContentValues();
                            content.put(DbContract.LocationEntry.COLUMN_NAME_LABEL, label);
                            content.put(DbContract.LocationEntry.COLUMN_NAME_IMAGE_NAME, imageLocation);
                            content.put(DbContract.LocationEntry.COLUMN_NAME_FASTX, point.X());
                            content.put(DbContract.LocationEntry.COLUMN_NAME_FASTY, point.Y());
                            content.put(DbContract.LocationEntry.COLUMN_NAME_FEATURE, BriefPatches.calcDescriptorString(image,point));
                            content.put(DbContract.LocationEntry.COLUMN_NAME_IMAGE_ROT, rotation);
                            writableDB.insert(DbContract.LocationEntry.TABLE_NAME,null,content);
                            loopsDone++;
                        } else {
                            break;
                        }

                    }
                    this.publishProgress();

                }
                Log.d(LOG_TAG, "Finished Image scale " + (1 - (scale * scaleStep)));
            }
            Log.d(LOG_TAG, "FINISHED TRAINING " + imageLocation + " ms: " + (System.currentTimeMillis() - startTime));

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

    @Override
    protected Boolean doInBackground(String... strings) {
        String filelocation = strings[0];
        String label = strings[1];
        train(filelocation,label);
        return true;
    }
}
