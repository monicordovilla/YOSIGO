package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yosigo.R;

import java.util.List;
import java.util.Map;

public class AsociateFragment extends Fragment {

    private PersonasViewModel mViewModel;
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ASOCIAR ACTIVIDAD" ;
    private String mParam1;
    private ListView list;

    public static AsociateFragment newInstance(String param1) {
        AsociateFragment fragment = new AsociateFragment();
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
            Log.d(TAG, "Recibido id: " + mParam1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        View root = inflater.inflate(R.layout.asociate_fragment, container, false);

        list = root.findViewById(R.id.list_asociate_user);

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
            @Override
            public void onChanged(List<String> strings) {
                ArrayAdapter<String> adapter = new UserCheckBoxAdapter(
                        root.getContext(),
                        strings
                );

                list.setAdapter(adapter);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}