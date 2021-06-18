package com.example.yosigo.Persona.ForumsPersona;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yosigo.Facilitador.ForumsFacilitador.ForumListAdapter;
import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListForumsPersona extends Fragment {

    private static final String TAG = "LIST FOROS ";
    private ListForumsViewModel listForumsViewModel;
    private View root;
    private GridView list;
    private EditText searchBar;

    private List<String> ids = new ArrayList<>();
    Map<String, String> pictos = new HashMap<>();
    Map<String, String> nombres = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listForumsViewModel =
                new ViewModelProvider(this).get(ListForumsViewModel.class);
        root = inflater.inflate(R.layout.list_forum_persona, container, false);
        list = root.findViewById(R.id.grid_forum);
        searchBar = root.findViewById(R.id.search_forum_name);

        //Obtener datos
        listForumsViewModel.getNombre().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(@Nullable Map<String, String> names) {
                nombres = names;
                Log.d(TAG, "Nombres" + " => " + nombres + ".\n " + names );
                listForumsViewModel.getPictos().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                    @Override
                    public void onChanged(@Nullable Map<String, String> photos) {
                        pictos = photos;
                        listForumsViewModel.getIds().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                            @Override
                            public void onChanged(@Nullable List<String> idList) {
                                ids = idList;

                                GridAdapter adapter = new GridAdapter(
                                        root.getContext(),
                                        ids,
                                        nombres,
                                        pictos
                                );
                                list.setAdapter(adapter);
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("param1", "Forum");
                                        bundle.putString("param2", ids.get(position));
                                        Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatPersonaFragment, bundle);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        //Filtrar foros
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                filterNames(newText);
            }
        });

        return root;
    }

    private void filterNames(String text) {
        if(text.isEmpty()){
            GridAdapter adapter = new GridAdapter(
                    root.getContext(),
                    ids,
                    nombres,
                    pictos
            );
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", "Forum");
                    bundle.putString("param2", ids.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatPersonaFragment, bundle);
                }
            });
        } else {
            List<String> filterIdsList = new ArrayList<>();
            Map<String, String> filterNameMap = new HashMap<>();
            Map<String, String> filterPictosMap = new HashMap<>();

            for (String key : nombres.keySet()) {
                String name = nombres.get(key);
                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    filterIdsList.add(key);
                    filterNameMap.put(key, nombres.get(key));
                    filterPictosMap.put(key, pictos.get(key));
                }
            }

            GridAdapter adapter = new GridAdapter(
                    root.getContext(),
                    filterIdsList,
                    filterNameMap,
                    filterPictosMap
            );
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", "Forum");
                    bundle.putString("param2", filterIdsList.get(position));
                    Log.d(TAG, "ID selected: " + filterIdsList.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatPersonaFragment, bundle);
                }
            });
        }
    }
}