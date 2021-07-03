package com.example.yosigo.Persona.HomePersona;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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

import com.example.yosigo.LoginActivity;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomePersonaFragment extends Fragment {

    private Button btn_calendar, btn_forums, btn_activities, btn_logout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_persona, container, false);

        btn_calendar = (Button) root.findViewById(R.id.button_persona_calendar);
        btn_forums = (Button) root.findViewById(R.id.button_persona_forum);
        btn_activities = (Button) root.findViewById(R.id.button_persona_activities);
        btn_logout = (Button) root.findViewById(R.id.button_persona_logout);

        btn_calendar.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_calendar));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_slideshow));
        btn_activities.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_activities));

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Log.d("CERRAR SESION",
                        String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
            }
        });

        return root;
    }
}