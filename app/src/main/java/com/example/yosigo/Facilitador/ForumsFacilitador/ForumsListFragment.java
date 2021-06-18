package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    private ForumsListViewModel ViewModelForums;
    private View root;
    private ListView list;
    private EditText searchBar;

    private List<String> nameList = new ArrayList<>();
    private Map<String, String> forumsMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewModelForums =
                new ViewModelProvider(this).get(ForumsListViewModel.class);
        root = inflater.inflate(R.layout.list_forum_facilitador, container, false);

        list = root.findViewById(R.id.list_forums);
        searchBar = root.findViewById(R.id.search_forum_name);

        ViewModelForums.getText().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> strings) {
                forumsMap = strings;
                nameList.clear();
                for (String key : strings.keySet()) {
                    nameList.add( (String) key);
                }

                ArrayAdapter<String> adapter = new ForumListAdapter(root.getContext(), strings, nameList);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putString("param1", "Forum");
                        bundle.putString("param2", forumsMap.get(nameList.get(position)));
                        Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
                    }
                });
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
                filterForums(newText);
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

    private void filterForums(String text) {
        if(text.isEmpty()){
            nameList.clear();
            for (String key : forumsMap.keySet()) {
                nameList.add( (String) key);
            }

            ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), forumsMap, nameList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", "Forum");
                    bundle.putString("param2", forumsMap.get(nameList.get(position)));
                    Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
                }
            });
        } else {
            Map<String, String> filterMap = new HashMap<>();
            nameList.clear();
            for (String key : forumsMap.keySet()) {
                if( key.toLowerCase().contains(text.toLowerCase()) ) {
                    nameList.add((String) key);
                    filterMap.put(key, forumsMap.get(key));
                }
            }
            ArrayAdapter<String> adapter = new ActivityListAdapter(root.getContext(), filterMap, nameList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", "Forum");
                    bundle.putString("param2", filterMap.get(nameList.get(position)));
                    Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
                }
            });
        }
    }
}