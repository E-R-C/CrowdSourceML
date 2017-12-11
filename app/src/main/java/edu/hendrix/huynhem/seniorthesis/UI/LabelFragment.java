package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import edu.hendrix.huynhem.seniorthesis.Models.DatabaseBlobClassifier;
import edu.hendrix.huynhem.seniorthesis.Models.DatabaseBlobTrainer;
import edu.hendrix.huynhem.seniorthesis.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LabelFragmentNavigation} interface
 * to handle interaction events.
 * Use the {@link LabelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LabelFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String MOSTRECENTPICTUREKEY = "UNIQUEKEY1";
    public static final String LOG_TAG = "LABEL_FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mFileName;

    private LabelFragmentNavigation mListener;

    private ImageView iView = null;
    private Spinner spinner = null;

    public LabelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LabelFragment.
     */
    public static LabelFragment newInstance(String param1) {
        LabelFragment fragment = new LabelFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_label, container, false);
    }

    //  Initialize buttons here
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        iView = view.findViewById(R.id.imageView);
        iView.setImageBitmap(BitmapFactory.decodeFile(mFileName));
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.buildings, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button back = view.findViewById(R.id.Back);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
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
//                Toast.makeText(view.getContext(),"Training: " + mFileName, Toast.LENGTH_LONG).show();
            }
        });
        Button classifyButton = view.findViewById(R.id.classify_button);
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseBlobClassifier dc = new DatabaseBlobClassifier(getActivity().getApplicationContext());
                dc.setProgressBar(pb);
                dc.execute(mFileName);
//                Toast.makeText(view.getContext(),"Classifying " + mFileName, Toast.LENGTH_SHORT).show();
            }
        });

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

    private void setupSpinner(){

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
        // TODO: Update argument type and name
        void goToCurrentJobs();
        void goToMenu();
    }

}
