package edu.hendrix.huynhem.seniorthesis.Threading;

import android.content.Context;
import android.util.Log;

/**
 * Created on 1/29/2018.
 */

public class TrainTask implements TrainRunnable.pbListener{
    private static final String LOG_TAG = "TRAINTASK";
    private TrainRunnable tr;
    private int loopsDone, maxLoops;
    // Todo: Attach views to this class so that we can have a list of tasks being done
    TrainTask (String filename, String label, Context c){
        tr = new TrainRunnable(filename,label,c);
        maxLoops = 100;
        Log.d(LOG_TAG, "Initialized noew TrainTask");
    }
    @Override
    public void publishProgress(int totalLoops, int loopsDone) {
        this.loopsDone = loopsDone;
        this.maxLoops = totalLoops;
        Log.d(LOG_TAG, "PROGRESS WAS PUBLISHED");
    }

    public int getLoopsDone(){
        return loopsDone;
    }
    public int getMaxLoops(){
        return maxLoops;
    }
    TrainRunnable getRunnable(){
        return tr;
    }
}
