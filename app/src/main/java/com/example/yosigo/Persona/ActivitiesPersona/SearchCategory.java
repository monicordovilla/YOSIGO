package com.example.yosigo.Persona.ActivitiesPersona;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCategory extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "LIST ACTIVITIES";

    private SearchCategoryViewModel mViewModel;
    private String mParam1;

    private View root;
    private GridView list;
    private ImageButton btn_anterior;

    private List<String> ids = new ArrayList<>();
    Map<String, String> pictos = new HashMap<>();
    Map<String, String> nombres = new HashMap<>();

    public static SearchCategory newInstance(String param1) {
        SearchCategory fragment = new SearchCategory();
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
        mViewModel = new ViewModelProvider(this).get(SearchCategoryViewModel.class);
        root = inflater.inflate(R.layout.search_category_fragment, container, false);
        list = root.findViewById(R.id.grid_category);
        btn_anterior = root.findViewById(R.id.btn_back_activity_list);

        btn_anterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_searchCategory_to_nav_activities, bundle);
            }
        });

        //Cargar listado de categor??as
        mViewModel.getCategoty_ids().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> stringList) {
                ids = stringList;

                mViewModel.getCategoty_names().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                    @Override
                    public void onChanged(Map<String, String> stringStringMap) {
                        nombres = stringStringMap;

                        mViewModel.getCategoty_pictos().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                            @Override
                            public void onChanged(Map<String, String> stringStringMap) {
                                pictos = stringStringMap;

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
                                        if (mParam1.equals("0")) {
                                            bundle.putString("param1", ids.get(position));
                                            Navigation.findNavController(view).navigate(R.id.action_searchCategory_to_nav_activities, bundle);
                                        } else {
                                            bundle.putString("param1", mParam1);
                                            bundle.putString("param2", ids.get(position));
                                            Navigation.findNavController(view).navigate(R.id.action_searchCategory_to_selectDayFragment, bundle);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        return root;
    }
}