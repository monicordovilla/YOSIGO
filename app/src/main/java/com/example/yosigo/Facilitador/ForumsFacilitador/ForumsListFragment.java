package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.ActivityListAdapter;
import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumsListFragment extends Fragment {

    private ForumsListViewModel slideshowForums;
    private ListView list;
    private List<String> nameList = new ArrayList<>();
    Map<String, String> forumsMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowForums =
                new ViewModelProvider(this).get(ForumsListViewModel.class);
        View root = inflater.inflate(R.layout.list_forum_facilitador, container, false);

        list = root.findViewById(R.id.list_forums);

        slideshowForums.getText().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> strings) {
                forumsMap = strings;
                nameList.clear();
                for (String key : strings.keySet()) {
                    nameList.add( (String) key);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(
                        root.getContext(),
                        android.R.layout.simple_list_item_1,
                        nameList
                );
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override//
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putString("param1", "Forum");
                        bundle.putString("param2", forumsMap.get(nameList.get(position)));
                        Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
                    }
                });
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab_forum);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_createForumFragment);
            }
        });

        return root;
    }
}