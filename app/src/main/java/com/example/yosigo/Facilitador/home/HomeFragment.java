package com.example.yosigo.Facilitador.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.yosigo.LoginActivity;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private Button btn_activities, btn_forums, btn_categories, btn_goals, btn_group, btn_logout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_facilitador, container, false);

        btn_activities = (Button) root.findViewById(R.id.button_facilitador_activities);
        btn_forums = (Button) root.findViewById(R.id.button_facilitador_forum);
        btn_categories = (Button) root.findViewById(R.id.button_facilitador_categoria);
        btn_goals = (Button) root.findViewById(R.id.button_facilitador_metas);
        btn_group = (Button) root.findViewById(R.id.button_facilitador_group);
        btn_logout = (Button) root.findViewById(R.id.button_facilitador_logout);

        btn_activities.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_activity, null));
        btn_forums.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_forums, null));
        btn_categories.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_category, null));
        btn_goals.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_goals, null));
        btn_group.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_group, null));

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                eliminarSesion();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Log.d("CERRAR SESION",
                        String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));
            }
        });

        return root;
    }

    public void eliminarSesion() {
        //Obtenemos referencia del archivo datos
        SharedPreferences preferencias=getActivity().getSharedPreferences("datos",Context.MODE_PRIVATE);
        //Creamos un editor para editar el archivo
        SharedPreferences.Editor editor=preferencias.edit();
        //Almacenamos en el editor el valor de la sesion
        editor.putString("sesion", "");
        //Grabamos en el archivo el contenido de la sesion
        editor.commit();
    }
}