package com.example.yosigo.Persona.HomePersona;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.yosigo.MainActivity;
import com.example.yosigo.R;

public class HomePersonaFragment extends Fragment {

    private Button btn_calendar, btn_forums, btn_activities;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_persona, container, false);

        btn_calendar = (Button) root.findViewById(R.id.button_persona_calendar);
        btn_forums = (Button) root.findViewById(R.id.button_persona_forum);
        btn_activities = (Button) root.findViewById(R.id.button_persona_activities);

        btn_calendar.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_calendar));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_slideshow));
        btn_activities.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_activities));

        /*MainActivity mainActivity = new MainActivity();
        mainActivity.setToolbar_image(R.drawable.inicio);*/

        return root;
    }
}