package com.example.yosigo.Persona.ForumsPersona;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yosigo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatPersonaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatPersonaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ACTIVITY_ID = "param1";
    private static final String ACTIVITY_IMAGE = "param2";

    // TODO: Rename and change types of parameters
    private String activity_id;
    private String activity_image;

    public ChatPersonaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param activity_id Parameter 1.
     * @param activity_image Parameter 2.
     * @return A new instance of fragment ChatPersonaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatPersonaFragment newInstance(String activity_id, String activity_image) {
        ChatPersonaFragment fragment = new ChatPersonaFragment();
        Bundle args = new Bundle();
        args.putString(ACTIVITY_ID, activity_id);
        args.putString(ACTIVITY_IMAGE, activity_image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity_id = getArguments().getString(ACTIVITY_ID);
            activity_image = getArguments().getString(ACTIVITY_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_persona, container, false);
    }
}