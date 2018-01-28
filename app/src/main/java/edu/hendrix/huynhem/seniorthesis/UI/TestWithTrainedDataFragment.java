package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.hendrix.huynhem.seniorthesis.Database.BlobDBHelper;
import edu.hendrix.huynhem.seniorthesis.Models.DatabaseBlobClassifier;
import edu.hendrix.huynhem.seniorthesis.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestWithTrainedDataInter} interface
 * to handle interaction events.
 * Use the {@link TestWithTrainedDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestWithTrainedDataFragment extends Fragment {
    public static final String LOG_TAG = "TESTWITHTRAINEDFRAG";
    private Spinner spinner;
    private Button testButton;
    private ProgressBar progressBar;
    private TextView resultsTextView, debugTextView;

    private TestWithTrainedDataInter mListener;
    private BlobDBHelper dbHelper;

    public TestWithTrainedDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TestWithTrainedDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestWithTrainedDataFragment newInstance() {
        TestWithTrainedDataFragment fragment = new TestWithTrainedDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_with_trained_data, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dbHelper = BlobDBHelper.getInstance(view.getContext());
        debugTextView = view.findViewById(R.id.TestWTrainedDebugTV);
        progressBar = view.findViewById(R.id.TestAllProgressbar);
        testButton = view.findViewById(R.id.TestWithTrainedButton);
        spinner = view.findViewById(R.id.TestAllSpinner);
        updateSpinner(view);
        resultsTextView = view.findViewById(R.id.TestAllResultsTextView);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = (String) spinner.getSelectedItem();
                DatabaseBlobClassifier dbc = new DatabaseBlobClassifier(view.getContext());
                HashMap<String, String> selection;
                if (label.equals("ALL")) {
                    selection = dbHelper.getAllFilesAndLabels();
                } else {
                    ArrayList<String> input = new ArrayList<>();
                    input.add(label);
                    selection = dbHelper.getFileLabelGivenLabels(input);
                }
                dbc.setTruePositiveLabel(label);
                dbc.setProgressBar(progressBar);
                dbc.setOutputTextView(resultsTextView);
                dbc.setDebugTextView(debugTextView);
                dbc.execute(selection.keySet());
            }
        });
    }
    // This updates the spinner from the locations database.
    private void updateSpinner(View view){
        // This method calls ensures that the database has been created at least
        dbHelper.getWritableDatabase();
        ArrayList<String> options = dbHelper.getLocations();
        options.add(0,"ALL");
        ArrayAdapter<String> locationDat = new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                options);
        spinner.setAdapter(locationDat);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestWithTrainedDataInter) {
            mListener = (TestWithTrainedDataInter) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TestWithTrainedDataInter");
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
    public interface TestWithTrainedDataInter {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
