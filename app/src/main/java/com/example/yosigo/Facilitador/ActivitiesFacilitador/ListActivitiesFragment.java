package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.yosigo.R;

import java.util.List;

public class ListActivitiesFragment extends Fragment {

    private ListActivitiesViewModel listActivitiesViewModel;
    private ListView list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listActivitiesViewModel =
                new ViewModelProvider(this).get(ListActivitiesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        list = root.findViewById(R.id.list_activities);
        listActivitiesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), strings);
                list.setAdapter(adapter);
            }
        });

        return root;
    }
}