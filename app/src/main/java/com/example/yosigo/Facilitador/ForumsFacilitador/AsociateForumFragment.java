package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AsociateForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AsociateForumFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private PersonasViewModel mViewModel;

    private String mParam1;
    private ListView list;
    private Button btn_asociate;

    public AsociateForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AsociateForumFragment.
     */
    public static AsociateForumFragment newInstance(String param1) {
        AsociateForumFragment fragment = new AsociateForumFragment();
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
        //Obtener personas asociadas al facilitador
        mViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_asociate_forum, container, false);

        //Obtener elementos del layout
        list = root.findViewById(R.id.list_asociate_forum);
        btn_asociate = (Button) root.findViewById(R.id.btn_asociate_forum);

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
            @Override
            public void onChanged(List<String> strings) {
                list.setAdapter(new ArrayAdapter<String>(
                                root.getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                strings
                        )
                );
                list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });

        return root;
    }
}