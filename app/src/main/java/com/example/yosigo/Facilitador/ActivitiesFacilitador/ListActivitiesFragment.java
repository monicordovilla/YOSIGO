package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListActivitiesFragment extends Fragment {

    private ListActivitiesViewModel listActivitiesViewModel;
    private ListView list;
    private List<String> nameList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listActivitiesViewModel =
                new ViewModelProvider(this).get(ListActivitiesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        list = root.findViewById(R.id.list_activities);

        listActivitiesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> strings) {
                for (String key : strings.keySet()) {
                    System.out.println(key + " : " + strings.get(key).toString());
                    nameList.add( (String) strings.get(key).toString() );
                }

                ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), strings, nameList);
                list.setAdapter(adapter);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_gallery_to_createActivity);
            }
        });

        return root;
    }
}