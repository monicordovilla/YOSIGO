package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yosigo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFacilitadorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFacilitadorFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public FeedbackFacilitadorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedbackFacilitadorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFacilitadorFragment newInstance(String param1, String param2) {
        FeedbackFacilitadorFragment fragment = new FeedbackFacilitadorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        return inflater.inflate(R.layout.fragment_feedback_facilitador, container, false);
    }
}