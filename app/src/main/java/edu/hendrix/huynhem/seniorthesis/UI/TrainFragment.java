package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import edu.hendrix.huynhem.seniorthesis.Database.BlobDBHelper;
import edu.hendrix.huynhem.seniorthesis.Models.DatabaseBlobClassifier;
import edu.hendrix.huynhem.seniorthesis.Models.DatabaseBlobTrainer;
import edu.hendrix.huynhem.seniorthesis.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LabelFragmentNavigation} interface
 * to handle interaction events.
 * Use the {@link TrainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String MOSTRECENTPICTUREKEY = "UNIQUEKEY1";
    public static final String LOG_TAG = "LABEL_FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mFileName;

    private LabelFragmentNavigation mListener;

    private ImageView iView = null;
    private Spinner spinner = null;
    private BlobDBHelper dbHelper;

    public TrainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Name of Photo.
     * @return A new instance of fragment TrainFragment.
     */
    public static TrainFragment newInstance(String param1) {
        TrainFragment fragment = new TrainFragment();
        Bundle args = new Bundle();
        args.putString(MOSTRECENTPICTUREKEY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileName = getArguments().getString(MOSTRECENTPICTUREKEY);
        }
        dbHelper = BlobDBHelper.getInstance(getActivity().getApplication().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    //  Initialize buttons here
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        spinner = view.findViewById(R.id.spinner);
        showImage(view);
        updateSpinner(view);
        final EditText locationTextBox = view.findViewById(R.id.NewLocationTextBox);
        Button back = view.findViewById(R.id.Back_Button);
        Button addNewLocation = view.findViewById(R.id.Add_New_Loc);
        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.insertNewLocation(locationTextBox.getText().toString());
                updateSpinner(view);
                locationTextBox.setText("");
            }
        });
        final ProgressBar pb = view.findViewById(R.id.progressBar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToMenu();

            }
        });
        Button saveAndTrainButton = view.findViewById(R.id.saveAndTrainButton);
        saveAndTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseBlobTrainer n = new DatabaseBlobTrainer(getActivity().getApplicationContext());
                n.setPb(pb);
                n.execute(mFileName, (String) spinner.getSelectedItem());
                // Goal for 1/12/2018: create a fragment that classifies all of the trained images
            }
        });
        Button classifyButton = view.findViewById(R.id.classify_button);
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseBlobClassifier dc = new DatabaseBlobClassifier(getActivity().getApplicationContext());
                dc.setProgressBar(pb);
                ArrayList<String> input = new ArrayList<>();
                input.add(mFileName);
                dc.execute(input);
            }
        });
    }
    // This updates the spinner from the locations database.
    private void updateSpinner(View view){
        // This method calls ensures that the database has been created at least
        dbHelper.getWritableDatabase();
        ArrayAdapter<String> locationDat = new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                dbHelper.getLocations());
        spinner.setAdapter(locationDat);
    }

    // The following 4 functions use code from this stack overflow link to fix the rotation of the image
    // https://stackoverflow.com/questions/31925712/android-getting-an-image-from-gallery-comes-rotated
    // Note, this will possible lead to memory overflow errors. if this happens, I'll need to set a maximum resolution
    private void showImage(View view){
        iView = view.findViewById(R.id.imageView);
        try {
            iView.setImageBitmap(getRotatedImage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Unable to load image!");
        }
        Toast.makeText(view.getContext(),mFileName,Toast.LENGTH_LONG).show();
    }
    private Bitmap getRotatedImage() throws IOException {
        ExifInterface exif = new ExifInterface(mFileName);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Bitmap bitmap = BitmapFactory.decodeFile(mFileName);
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }
    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LabelFragmentNavigation) {
            mListener = (LabelFragmentNavigation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LabelFragmentNavigation");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface LabelFragmentNavigation {
        void goToCurrentJobs();
        void goToMenu();
        void goToTest(String fileName);
    }

}
