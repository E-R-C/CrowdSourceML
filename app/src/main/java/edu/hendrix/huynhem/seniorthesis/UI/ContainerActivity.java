package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;

import edu.hendrix.huynhem.seniorthesis.R;

public class ContainerActivity extends AppCompatActivity implements CapturePhotoMenu.onPictureCapture, TrainFragment.LabelFragmentNavigation, TrainOrClassifyFragment.TrainOrClassifyInterface, TestFragment.TestFragemntNavigation {

    HashMap<String,Fragment> mapOfFragments = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapOfFragments.put(CapturePhotoMenu.LOG_TAG, CapturePhotoMenu.newInstance());
        getFragmentManager().beginTransaction()
                .add(R.id.FragmentView,mapOfFragments.get(CapturePhotoMenu.LOG_TAG)).commit();

        setContentView(R.layout.activity_container);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void pictureCaptured(String filename) {
        TrainFragment lf;
        if (mapOfFragments.containsKey(TrainFragment.LOG_TAG)){
            lf = (TrainFragment) mapOfFragments.get(TrainFragment.LOG_TAG);
        } else {
            lf = new TrainFragment();
        }
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
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
        CapturePhotoMenu mmf;
        if (mapOfFragments.containsKey(TrainFragment.LOG_TAG)){
            mmf = (CapturePhotoMenu) mapOfFragments.get(TrainFragment.LOG_TAG);
        } else {
            mmf = new CapturePhotoMenu();
        }
        Bundle args = new Bundle();
        mmf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.FragmentView, mmf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void goToTest(String filename) {
        TestFragment tf;
        if (mapOfFragments.containsKey(TrainFragment.LOG_TAG)){
            tf = (TestFragment) mapOfFragments.get(TestFragment.LOG_TAG);
        } else {
            tf = new TestFragment();
        }
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
        tf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.FragmentView, tf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toTrainingFragment(String filename) {
        TrainFragment lf;
        if (mapOfFragments.containsKey(TrainFragment.LOG_TAG)){
            lf = (TrainFragment) mapOfFragments.get(TrainFragment.LOG_TAG);
        } else {
            lf = new TrainFragment();
        }
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
        lf.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.FragmentView, lf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void toTestingFragment(String filename) {
        goToTest(filename);
    }

    @Override
    public void pickNewPhoto() {
        goToMenu();
    }
}
