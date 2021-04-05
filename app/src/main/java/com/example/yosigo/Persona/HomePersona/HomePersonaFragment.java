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

    private Button btn_calendar, btn_forums;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_persona, container, false);

        btn_calendar = (Button) root.findViewById(R.id.button_persona_calendar);
        btn_forums = (Button) root.findViewById(R.id.button_persona_forum);
        btn_calendar.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_calendar));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_slideshow));

        return root;
    }
}