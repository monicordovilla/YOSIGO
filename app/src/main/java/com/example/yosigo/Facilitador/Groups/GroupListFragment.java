package com.example.yosigo.Facilitador.Groups;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListFragment extends Fragment {

    private static final String TAG = "METAS";
    private GroupListViewModel mViewModel;
    private View root;
    private ListView list;
    private Map<String, String> groupsMap = new HashMap<>();
    private List<String> groupsNameList = new ArrayList<>();

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        root =  inflater.inflate(R.layout.group_list_fragment, container, false);

        list = root.findViewById(R.id.list_groups);

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                groupsNameList.clear();
                groupsNameList = strings;
                Log.d(TAG, "Lista: " + groupsNameList);

                ArrayAdapter<String> adapter = new ArrayAdapter(
                        root.getContext(),
                        android.R.layout.simple_list_item_1,
                        groupsNameList
                );
                list.setAdapter(adapter);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab_groups);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_group_to_createGroupFragment);
            }
        });

        return root;
    }

}