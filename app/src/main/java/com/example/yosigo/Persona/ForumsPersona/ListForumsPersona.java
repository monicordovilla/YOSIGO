package com.example.yosigo.Persona.ForumsPersona;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.yosigo.R;

public class ListForumsPersona extends Fragment {

    private ListForumsViewModel listForumsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listForumsViewModel =
                new ViewModelProvider(this).get(ListForumsViewModel.class);
        View root = inflater.inflate(R.layout.list_forum_facilitador, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        listForumsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}