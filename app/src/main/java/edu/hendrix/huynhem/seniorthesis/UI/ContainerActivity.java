package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import edu.hendrix.huynhem.seniorthesis.R;

public class ContainerActivity extends AppCompatActivity
        implements CapturePhotoMenu.capturePhotoMenuInteractions,
        TrainFragment.LabelFragmentNavigation, TrainOrClassifyFragment.TrainOrClassifyInterface,
        TestFragment.TestFragemntNavigation, TestWithTrainedDataFragment.TestWithTrainedDataInter {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapturePhotoMenu frag = CapturePhotoMenu.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.FragmentView,frag).commit();

        setContentView(R.layout.activity_container);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void pictureCaptured(String filename) {
        galleryAddPic(filename);
        TrainFragment lf = new TrainFragment();
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
        replaceFragment(lf, args);
    }

    @Override
    public void goToTestWithTrained() {
        TestWithTrainedDataFragment tf = new TestWithTrainedDataFragment();
        replaceFragment(tf, new Bundle());
    }

    @Override
    public void goToCurrentJobs() {
        // TODO: Add a job queue
    }

    @Override
    public void goToMenu() {
        CapturePhotoMenu frag = new CapturePhotoMenu();
        Bundle args = new Bundle();
        frag.setArguments(args);
        replaceFragment(frag, args);
    }

    @Override
    public void goToTest(String filename) {
        TestFragment tf = new TestFragment();
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
        replaceFragment(tf, args);
    }

    @Override
    public void toTrainingFragment(String filename) {
        TrainFragment tf = new TrainFragment();
        Bundle args = new Bundle();
        args.putString(TrainFragment.MOSTRECENTPICTUREKEY,filename);
        replaceFragment(tf, args);
    }

    @Override
    public void toTestingFragment(String filename) {
        goToTest(filename);
    }

    @Override
    public void pickNewPhoto() {
        goToMenu();
    }

    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void replaceFragment(Fragment f, Bundle args){
        f.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.FragmentView, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
