package com.example.yosigo.Facilitador.Category;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yosigo.Facilitador.ForumsFacilitador.ForumListAdapter;
import com.example.yosigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryListFragment extends Fragment {

    private CategoryListViewModel mViewModel;
    private View root;
    private ListView list;
    private Map<String, String> categoryMap = new HashMap<>();
    private List<String> categoryNameList = new ArrayList<>();

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.category_list_fragment, container, false);

        list = root.findViewById(R.id.list_categories);

        ArrayAdapter<String> adapter = new ArrayAdapter(
                root.getContext(),
                android.R.layout.simple_list_item_1,
                categoryNameList
        );
        list.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_categoryListFragment_to_createCategoryFragment);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);

        mViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> strings) {
                categoryMap.clear();
                categoryMap = strings;
            }
        });

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                categoryNameList.clear();
                categoryNameList = strings;
            }
        });
    }
}