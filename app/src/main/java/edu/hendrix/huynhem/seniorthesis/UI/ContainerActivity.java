package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import edu.hendrix.huynhem.seniorthesis.R;
import edu.hendrix.huynhem.seniorthesis.Threading.TrainerManager;

public class ContainerActivity extends AppCompatActivity
        implements CapturePhotoMenu.capturePhotoMenuInteractions,
        TrainFragment.LabelFragmentNavigation, TrainOrClassifyFragment.TrainOrClassifyInterface,
        TestFragment.TestFragemntNavigation, TestWithTrainedDataFragment.TestWithTrainedDataInter, TrainerManager.NotificationInterface {
    public final static int NOTIFICATION_ID = 1;

    private Notification.Builder notification;
    private static final String LOG_TAG = "CONTAINER_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapturePhotoMenu frag = CapturePhotoMenu.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.FragmentView,frag).commit();

        setContentView(R.layout.activity_container);
        notification = buildNotification();
        TrainerManager.setmListener(this);
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
        args.putStringArray(TrainFragment.PICTUREARRAY,new String[]{filename});
        replaceFragment(lf, args);
    }

    @Override
    public void goToTestWithTrained() {
        TestWithTrainedDataFragment tf = new TestWithTrainedDataFragment();
        replaceFragment(tf, new Bundle());
    }

    @Override
    public void goToTrainMany(String[] files) {
        TrainFragment lf = new TrainFragment();
        Bundle args = new Bundle();
        args.putStringArray(TrainFragment.PICTUREARRAY,files);
        replaceFragment(lf, args);

    }

    @Override
    public void goToCurrentJobs() {
        // TODO: Add a job queue
    }

    @Override
    public void goToMenu() {
        CapturePhotoMenu frag = new CapturePhotoMenu();
        Bundle args = new Bundle();
        replaceFragment(frag, args);
    }

    @Override
    public void goToTest(String filename) {
        TestFragment tf = new TestFragment();
        Bundle args = new Bundle();
        args.putString(TrainFragment.PICTUREARRAY,filename);
        replaceFragment(tf, args);
    }

    @Override
    public void toTrainingFragment(String filename) {
        TrainFragment tf = new TrainFragment();
        Bundle args = new Bundle();

        args.putStringArray(TrainFragment.PICTUREARRAY,new String[]{filename});
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

    private Notification.Builder buildNotification(){
        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                true);
        Intent intent = new Intent(this, ContainerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("Training Jobs running");
        builder.setContentText("Waiting for Jobs");
        builder.setSubText("0 Jobs");
        builder.setContentIntent(pendingIntent);
        builder.setTicker("Fancy Notification");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setLargeIcon(bm);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
//        builder.setOngoing(false);
        builder.setProgress(0,0,true);
        Notification notification = builder.build();
        NotificationManager notificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManger != null;
        notificationManger.notify(NOTIFICATION_ID, notification);
        return builder;
    }

    @Override
    public void setProgressVals(int done, int total) {

        NotificationManager notificationManger =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(done == total){
            notification.setSubText("No jobs running");
            notification.setContentText("");
            notification.setProgress(0,0,false);
        } else {
            notification.setSubText(total - done + " jobs remaining");
            notification.setContentText(String.format("%d jobs done out of %d total", done, total));
            notification.setProgress(total,done,false);
        }

        notificationManger.notify(NOTIFICATION_ID,notification.build());
    }
}
