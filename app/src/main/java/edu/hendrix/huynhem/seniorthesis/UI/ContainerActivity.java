package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

import edu.hendrix.huynhem.seniorthesis.R;

public class ContainerActivity extends AppCompatActivity implements TrainMenu.onPictureCapture, LabelFragment.LabelFragmentNavigation, TrainOrClassifyFragment.TrainOrClassifyInterface {

    HashMap<String,Fragment> mapOfFragments = new HashMap<>();
    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapOfFragments.put(TrainMenu.LOG_TAG, TrainMenu.newInstance());
        getFragmentManager().beginTransaction()
                .add(R.id.FragmentView,mapOfFragments.get(TrainMenu.LOG_TAG)).commit();

        setContentView(R.layout.activity_container);

        mContentView = findViewById(R.id.FragmentView);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void pictureCaptured(String filename) {
        LabelFragment lf;
        if (mapOfFragments.containsKey(LabelFragment.LOG_TAG)){
            lf = (LabelFragment) mapOfFragments.get(LabelFragment.LOG_TAG);
        } else {
            lf = new LabelFragment();
        }
        Bundle args = new Bundle();
        args.putString(LabelFragment.MOSTRECENTPICTUREKEY,filename);
        lf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.FragmentView, lf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void goToCurrentJobs() {
        // TODO: Add a job queue
    }

    @Override
    public void goToMenu() {
        TrainMenu mmf;
        if (mapOfFragments.containsKey(LabelFragment.LOG_TAG)){
            mmf = (TrainMenu) mapOfFragments.get(LabelFragment.LOG_TAG);
        } else {
            mmf = new TrainMenu();
        }
        Bundle args = new Bundle();
        mmf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.MainFragmentContainer, mmf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toTrainingFragment() {
        TrainMenu mmf;
        if (mapOfFragments.containsKey(LabelFragment.LOG_TAG)){
            mmf = (TrainMenu) mapOfFragments.get(LabelFragment.LOG_TAG);
        } else {
            mmf = new TrainMenu();
        }
        Bundle args = new Bundle();
        mmf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.MainFragmentContainer, mmf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toTestingFragment() {

    }
}
