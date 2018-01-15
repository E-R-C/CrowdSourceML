package edu.hendrix.huynhem.seniorthesis.UI;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.hendrix.huynhem.seniorthesis.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrainOrClassifyInterface} interface
 * to handle interaction events.
 * Use the {@link TrainOrClassifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainOrClassifyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String photoFileNameKey = "param1";
    private static final String LOG_TAG = "TRAIN_OR_CLASSIFY_FRAGMENT";
    private String photoFileName;

    private TrainOrClassifyInterface mListener;

    public TrainOrClassifyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 File name of the photo
     * @return A new instance of fragment TrainOrClassifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainOrClassifyFragment newInstance(String param1) {
        TrainOrClassifyFragment fragment = new TrainOrClassifyFragment();
        Bundle args = new Bundle();
        args.putString(photoFileNameKey, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoFileName = getArguments().getString(photoFileNameKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train_or_classify, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button trainButton = view.findViewById(R.id.Train_Button);
        Button testButton = view.findViewById(R.id.Test_Button);

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.toTrainingFragment(photoFileName);
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.toTestingFragment(photoFileName);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrainOrClassifyInterface) {
            mListener = (TrainOrClassifyInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TrainOrClassifyInterface");
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
    public interface TrainOrClassifyInterface {
        // TODO: Update argument type and name
        void toTrainingFragment(String filename);
        void toTestingFragment(String filename);
    }
}
