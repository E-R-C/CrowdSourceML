package edu.hendrix.huynhem.seniorthesis.Threading;

import android.content.Context;
import android.util.Log;

/**
 * Created on 1/29/2018.
 */

public class TrainTask{
    private static final String LOG_TAG = "TRAINTASK";
    private TrainRunnable tr;
    // Todo: Attach views to this class so that we can have a list of tasks being done
    TrainTask (String filename, String label, Context c){
        tr = new TrainRunnable(filename,label,c);
        Log.d(LOG_TAG, "Initialized new TrainTask");
    }


    TrainRunnable getRunnable(){
        return tr;
    }
}
