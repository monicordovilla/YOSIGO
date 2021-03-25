package com.example.yosigo.Facilitador.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.yosigo.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button btn_activities, btn_forums;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_facilitador, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        btn_activities = (Button) root.findViewById(R.id.button_facilitador_activities);
        btn_forums = (Button) root.findViewById(R.id.button_facilitador_forum);
        btn_activities.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_activity, null));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_forums, null));


        return root;
    }
}