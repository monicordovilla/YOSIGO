package com.example.yosigo.Facilitador.Goals;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.ActivityListAdapter;
import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalsListFragment extends Fragment {

    private static final String TAG = "METAS";
    private GoalsListViewModel mViewModel;
    private View root;
    private ListView list;
    private EditText searchBar;

    private Map<String, String> goalsMap = new HashMap<>();
    private List<String> goalsNameList = new ArrayList<>();

    public static GoalsListFragment newInstance() {
        return new GoalsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(GoalsListViewModel.class);
        root = inflater.inflate(R.layout.goals_list_fragment, container, false);

        list = root.findViewById(R.id.list_goals);
        searchBar = root.findViewById(R.id.search_goal_name);

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                goalsNameList.clear();
                goalsNameList = strings;
                Log.d(TAG, "Lista: " + goalsNameList);

                ArrayAdapter<String> adapter = new ArrayAdapter(
                        root.getContext(),
                        android.R.layout.simple_list_item_1,
                        goalsNameList
                );
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
                filterGoals(newText);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab_goals);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_goals_to_createGoalFragment);
            }
        });

        return root;
    }

    private void filterGoals(String text) {
        if(text.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter(
                    root.getContext(),
                    android.R.layout.simple_list_item_1,
                    goalsNameList
            );
            list.setAdapter(adapter);
        } else {
            List<String> filterList = new ArrayList<>();
            filterList.clear();
            for (String name : goalsNameList) {
                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    filterList.add(name);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(
                    root.getContext(),
                    android.R.layout.simple_list_item_1,
                    filterList
            );
            list.setAdapter(adapter);
        }
    }

}