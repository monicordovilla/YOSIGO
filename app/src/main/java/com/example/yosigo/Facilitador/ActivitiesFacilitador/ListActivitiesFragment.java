package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivitiesFragment extends Fragment {

    private ListActivitiesViewModel listActivitiesViewModel;
    private View root;
    private ListView list;
    private EditText searchBar;

    private Map<String, String> activitiesMap = new HashMap<>();
    private List<String> nameList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listActivitiesViewModel =
                new ViewModelProvider(this).get(ListActivitiesViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.list_activities_facilitador, container, false);

        list = root.findViewById(R.id.list_activities);
        searchBar = root.findViewById(R.id.search_activity_name);

        listActivitiesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> strings) {
                activitiesMap = strings;
                nameList.clear();
                for (String key : strings.keySet()) {
                    nameList.add( (String) key);
                }

                ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), activitiesMap, nameList);
                list.setAdapter(adapter);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                filterActivities(newText);
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

    private void filterActivities(String text) {
        if(text.isEmpty()){
            nameList.clear();
            for (String key : activitiesMap.keySet()) {
                nameList.add( (String) key);
            }

            ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), activitiesMap, nameList);
            list.setAdapter(adapter);
        } else {
            Map<String, String> filterMap = new HashMap<>();
            nameList.clear();
            for (String key : activitiesMap.keySet()) {
                if( key.toLowerCase().contains(text.toLowerCase()) ) {
                    nameList.add((String) key);
                    filterMap.put(key, activitiesMap.get(key));
                }
            }
            ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), filterMap, nameList);
            list.setAdapter(adapter);
        }
    }
}