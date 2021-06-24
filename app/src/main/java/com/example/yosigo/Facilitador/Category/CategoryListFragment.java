package com.example.yosigo.Facilitador.Category;

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
import android.widget.ListView;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.ActivityListAdapter;
import com.example.yosigo.Facilitador.ForumsFacilitador.ForumListAdapter;
import com.example.yosigo.Facilitador.itemAdapter;
import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryListFragment extends Fragment {

    private static final String TAG = "CATEGORIA ";
    private CategoryListViewModel mViewModel;
    private View root;
    private ListView list;
    private EditText searchBar;

    private Map<String, String> categoryMap = new HashMap<>();
    private List<String> categoryNameList = new ArrayList<>();

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        root = inflater.inflate(R.layout.category_list_fragment, container, false);

        list = root.findViewById(R.id.list_categories);
        searchBar = root.findViewById(R.id.search_category_name);

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                categoryNameList = strings;

                mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                    @Override
                    public void onChanged(Map<String, String> stringMap) {
                        categoryMap = stringMap;
                        String tipo = "Category";

                        itemAdapter adapter = new itemAdapter(
                                root.getContext(),
                                tipo,
                                categoryMap,
                                categoryNameList
                        );
                        list.setAdapter(adapter);
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
                filterCategories(newText);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_categoryListFragment_to_createCategoryFragment);
            }
        });

        return root;
    }

    private void filterCategories(String text) {
        if(text.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter(
                    root.getContext(),
                    android.R.layout.simple_list_item_1,
                    categoryNameList
            );
            list.setAdapter(adapter);
        } else {
            List<String> filterList = new ArrayList<>();
            filterList.clear();
            for (String name : categoryNameList) {
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