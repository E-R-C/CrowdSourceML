package edu.hendrix.huynhem.seniorthesis.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;

import edu.hendrix.huynhem.seniorthesis.Database.BlobDBHelper;
import edu.hendrix.huynhem.seniorthesis.Database.BlobHistogram;
import edu.hendrix.huynhem.seniorthesis.Database.DbContract;
import edu.hendrix.huynhem.seniorthesis.Database.Serializer;
import edu.hendrix.huynhem.seniorthesis.Imaging.BriefPatches;
import edu.hendrix.huynhem.seniorthesis.Imaging.FASTFeature;
import edu.hendrix.huynhem.seniorthesis.Imaging.Image;
import edu.hendrix.huynhem.seniorthesis.Util.ClassificationReport;

import static edu.hendrix.huynhem.seniorthesis.Models.LearnerSettings.maxDimension;

/**
 * This class is going to be modified to take in an arbitrary amount of input and mass outputTextView
 */

public class DatabaseBlobClassifier extends AsyncTask<Collection<String>, Integer, HashMap<String, String>> implements ModelClassifierInterface{
    final static String LOG_TAG = "DATABASE_BLOB_CLASS";
    BlobDBHelper blobDBHelper;
    Context c;
    SQLiteDatabase writableDB;
    StringBuilder debugStringBuilder;

    String truePositiveLabel, latestImage;
    TextView outputTextView, debugTextView;
    ProgressBar pb;
    int pbMax, pbStatus, totalMatches, maxFAST = 0;
    public DatabaseBlobClassifier(Context context){
        c = context;
        blobDBHelper = BlobDBHelper.getInstance(context);
        writableDB = blobDBHelper.getWritableDatabase();
        debugStringBuilder = new StringBuilder();

    }
    public void setProgressBar(ProgressBar pb){
        this.pb = pb;
    }

    public void setTruePositiveLabel(String s){
        truePositiveLabel = s;
    }

    public void setOutputTextView(TextView outputTextView){
        this.outputTextView = outputTextView;
    }

    public void setDebugTextView(TextView debugTextView){
        this.debugTextView = debugTextView;
    }

    @Override
    public String classify(String imageLocation) {
        latestImage = imageLocation;

        if(writableDB.isOpen()){
            BlobHistogram resultingHist = new BlobHistogram();
            Image image = new Image(imageLocation, maxDimension);
            PriorityQueue<FASTFeature> fastpts = image.getFastPoints();
            String[] projection = {
                    DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB
            };
            String selection = DbContract.RestructuredBlobEntry.COLUMN_NAME_FEATURE + " =  ?";
            maxFAST = fastpts.size();
            for (; fastpts.size() > 0 ; ){
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
                if (cursor.getCount() != 0){
                    totalMatches++;
                    byte[] blobBytes = cursor.getBlob(cursor.getColumnIndex(DbContract.RestructuredBlobEntry.COLUMN_NAME_COUNTBLOB));

                    try {
                        BlobHistogram bh = Serializer.deserializeBlob(blobBytes);
                        resultingHist = resultingHist.mergeMakeNewCopy(bh);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
    protected void onProgressUpdate(Integer... values) {
        if (pb != null){
            pb.setMax(pbMax);
            pb.setProgress(pbStatus);
        }

    }

    /**
     * @param collections of Filenames to train on
     * @return A HashMap of <Filename, labelGuess>
     */
    @Override
    protected HashMap<String, String> doInBackground(Collection<String>[] collections) {
        Collection<String> collection = collections[0];
        pbMax = collection.size();
        HashMap<String, String> result = new HashMap<>();
        for(String s: collection){
            totalMatches = 0;
            Log.d(LOG_TAG, "Starting to classify " + s);
            String output = classify(s);
            debugStringBuilder.append(output);
            debugStringBuilder.append(" patches found out of: ");
            debugStringBuilder.append(totalMatches);
            debugStringBuilder.append("/");
            debugStringBuilder.append(maxFAST);
            debugStringBuilder.append("\n");

            result.put(s, output);
            pbStatus++;
            publishProgress();
        }
        return result;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> fileLabelOutput) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(fileLabelOutput.keySet());
        HashMap<String, String> actual = blobDBHelper.getFileLabelGivenFiles(list);
        for(String key: list){
            Log.d(LOG_TAG, "Key: " + key);
            Log.d(LOG_TAG, "value: " + fileLabelOutput.get(key));
        }
        if (actual.isEmpty()){
            Log.d(LOG_TAG, fileLabelOutput.get(latestImage) + " " + totalMatches + "/" + maxFAST);
            Toast.makeText(c,fileLabelOutput.get(latestImage) + " " + totalMatches + "/" + maxFAST,Toast.LENGTH_SHORT).show();
            return;
        }
        ClassificationReport cr = new ClassificationReport(actual, fileLabelOutput, truePositiveLabel);
        if(outputTextView != null){
            outputTextView.setText(cr.toConfusionMatrix());
        } else {
            Log.d(LOG_TAG, debugStringBuilder.toString());
            Toast.makeText(c,fileLabelOutput.get(latestImage) + " " + totalMatches + "/" + maxFAST,Toast.LENGTH_SHORT).show();
        }
        if (debugTextView != null){
            debugTextView.setText(debugStringBuilder.toString());
        }
    }

}
