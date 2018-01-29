package edu.hendrix.huynhem.seniorthesis.UI;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.hendrix.huynhem.seniorthesis.Database.BlobDBHelper;
import edu.hendrix.huynhem.seniorthesis.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link capturePhotoMenuInteractions} interface
 * to handle interaction events.
 * Use the {@link CapturePhotoMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CapturePhotoMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final int REQUEST_IMAGE_CAPTURE = 12345;
    private static final int REQUEST_IMAGE_FROM_GAL = 1337;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "Failed to get all permissions";
    public static final String LOG_TAG = "CAPTURE_PHOTO_MENU";
    private static final String ALBUM_NAME = "CrowdSourceML";
    String MOST_RECENT_PHOTO_PATH;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private capturePhotoMenuInteractions pictureListener;

    private String mCurrentPhotoPath;
    public CapturePhotoMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment CapturePhotoMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static CapturePhotoMenu newInstance() {
        CapturePhotoMenu fragment = new CapturePhotoMenu();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture_photo, container, false);
    }


    // Initialize UI here
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button chooseFromGallery = view.findViewById(R.id.ChooseFromGalButton);
        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_IMAGE_FROM_GAL);
            }
        });
        Button captureNewImageButton = view.findViewById(R.id.TakePictureButton);
        captureNewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        Button testWithTrainedImages = view.findViewById(R.id.TestWithTrainedButton);
        testWithTrainedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureListener.goToTestWithTrained();
            }
        });
        Button deleteDatabase = view.findViewById(R.id.ResetDatabaseButton);
        deleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlobDBHelper dbHelper = BlobDBHelper.getInstance(getActivity().getApplicationContext());
                dbHelper.DeleteDatabase();
                Toast.makeText(getActivity().getApplicationContext(),"Deleted Database", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void requestPermissions(){
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
    }

    private void requestPermissions(String[] permissions){
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != grantResults[0]) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private boolean hasPermission(String manifestDescription){
        return ContextCompat.checkSelfPermission(getActivity(), manifestDescription)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void captureImage(){
        if (!hasPermission(Manifest.permission.CAMERA) || !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d(LOG_TAG, "Asking for permission!");
            requestPermissions();
            return;
        }
        Log.d(LOG_TAG, "Starting intent");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File outputFile;
        try {
            outputFile = createNextFile();
            MOST_RECENT_PHOTO_PATH = outputFile.getAbsolutePath();
            Uri photoLocationUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "edu.hendrix.huynhem",
                    outputFile
                    );
            Log.d(LOG_TAG, "LOCATION OF FILE: " + photoLocationUri.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoLocationUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed to create file, This is why your program is failing right now");
        }


    }

    private File createNextFile() throws IOException {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), ALBUM_NAME);
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + ALBUM_NAME);
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(LOG_TAG, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prefix = "IMG_" + timeStamp;
        File storageDir = mediaStorageDir;
        File image = File.createTempFile(
                prefix,
                ".jpg",
                storageDir
        );
        MOST_RECENT_PHOTO_PATH = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            pictureListener.pictureCaptured(MOST_RECENT_PHOTO_PATH);
        } else if (requestCode == REQUEST_IMAGE_FROM_GAL){
            if (data.getClipData() != null){
                int numberOfImages = data.getClipData().getItemCount();
                String[] links = new String[numberOfImages];

                for (int i = 0; i < numberOfImages; i++) {
                    Log.d(LOG_TAG, data.getClipData().getItemAt(i).getUri().toString());
                    links[i] = getRealPathFromURI(data.getClipData().getItemAt(i).getUri());
                    Log.d(LOG_TAG, "Added " + links[i]);
                }
            }
            else {
                Log.d(LOG_TAG, "getClipData is null");
            }
            final Uri imageUri = data.getData();
            MOST_RECENT_PHOTO_PATH =  getRealPathFromURI(imageUri);
            pictureListener.pictureCaptured(MOST_RECENT_PHOTO_PATH);
        }
        Log.d(LOG_TAG, "Activty Result was " + resultCode);
    }



    // From https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
    private String getRealPathFromURI(Uri contentUri) {
        Context c = getActivity().getApplicationContext();
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(c, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof capturePhotoMenuInteractions) {
            pictureListener = (capturePhotoMenuInteractions) context;


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPictureCapture");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pictureListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface capturePhotoMenuInteractions {
        void pictureCaptured(String filename);
        void goToTestWithTrained();
        void goToTrainMany(String[] files);
    }

}
