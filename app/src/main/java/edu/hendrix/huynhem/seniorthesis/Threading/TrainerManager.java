package edu.hendrix.huynhem.seniorthesis.Threading;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 1/29/2018.
 */

public class TrainerManager {
    private static final String LOG_TAG = "TRAINER_MANAGER";
    static final int TRAINING_DONE = 0;
    static final int TRAINING_UPDATE = 1;


    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A queue of Runnables for the image decoding pool
    private final BlockingQueue<Runnable> mTrainQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mTrainThreadPool;


    private static final TrainerManager sInstance = new TrainerManager();

    private Handler mHandler;
    public static TrainerManager getInstance() {
        return sInstance;
    }

    private TrainerManager() {
        mTrainQueue = new LinkedBlockingDeque<>();
        mTrainThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
                mTrainQueue);

    }

    static public TrainTask startNewTrainTask(String filename, String label, Context c){
        TrainTask tt = new TrainTask(filename, label, c);
        getInstance().mTrainThreadPool.execute(tt.getRunnable());
        Log.d(LOG_TAG,"Should Have Started");
        return tt;
    }
}
