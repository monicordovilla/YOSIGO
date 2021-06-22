package com.example.yosigo.Persona.ActivitiesPersona;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.yosigo.Persona.CalendarPersona.SelectDayFragment;
import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivitiesPersonaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "LIST ACTIVITIES";

    private ListActivitiesPersonaViewModel mViewModel;
    private String mParam1;

    private View root;
    private GridView list;
    private EditText searchBar;
    private ImageButton btnFilter;

    private List<String> ids = new ArrayList<>();
    Map<String, String> pictos = new HashMap<>();
    Map<String, String> nombres = new HashMap<>();

    public static ListActivitiesPersonaFragment newInstance(String param1) {
        ListActivitiesPersonaFragment fragment = new ListActivitiesPersonaFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ListActivitiesPersonaViewModel.class);
        root = inflater.inflate(R.layout.list_activities_persona_fragment, container, false);
        list = root.findViewById(R.id.grid_activities);
        searchBar = root.findViewById(R.id.search_activity_name);
        btnFilter = root.findViewById(R.id.btn_filter_category);

        btnFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_activities_to_searchCategory);
            }
        });

        mViewModel.getActivities().observe(getViewLifecycleOwner(), new Observer<List<Activity>>() {
            @Override
            public void onChanged(List<Activity> activities) {
                getActivities(activities);
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
                filterNames(newText);
            }
        });

        return root;
    }

    private void getActivities(List<Activity> activities){
        ids.clear();
        nombres.clear();
        pictos.clear();

        for(Activity activity : activities) {
            if( mParam1.equals("0") || mParam1.equals(activity.getCategoria()) ) {
                ids.add(activity.getId());
                nombres.put(activity.getId(), activity.getNombre());
                pictos.put(activity.getId(), activity.getImagen());
            }
        }

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
                bundle.putString("param1", ids.get(position));
                Navigation.findNavController(view).navigate(R.id.action_nav_activities_to_activityViewFragment2, bundle);
            }
        });
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
                    bundle.putString("param1", ids.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_nav_activities_to_activityViewFragment2, bundle);
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
                    bundle.putString("param1", filterIdsList.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_nav_activities_to_activityViewFragment2, bundle);
                }
            });
        }
    }
}