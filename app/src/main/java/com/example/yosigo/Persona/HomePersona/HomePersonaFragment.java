package com.example.yosigo.Persona.HomePersona;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yosigo.R;

public class HomePersonaFragment extends Fragment {

    private HomePersonaViewModel homePersonaViewModel;
    private Button btn_activities, btn_forums;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homePersonaViewModel =
                new ViewModelProvider(this).get(HomePersonaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_persona, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homePersonaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btn_activities = (Button) root.findViewById(R.id.button_persona_activities);
        btn_forums = (Button) root.findViewById(R.id.button_persona_forum);
        btn_activities.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_gallery, null));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_slideshow, null));


        return root;
    }
}