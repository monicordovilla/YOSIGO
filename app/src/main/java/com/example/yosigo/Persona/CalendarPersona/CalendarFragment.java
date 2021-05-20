package com.example.yosigo.Persona.CalendarPersona;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CALENDARIO" ;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ImageButton btn_l, btn_m , btn_x, btn_j, btn_v, btn_s, btn_d;
    private View root;

    //Dias semana
    int flag; //Aqui guardas tus propiedades booleanas, también conocidas como banderas
    public static final int LUNES = 1;  //o sea 0000001
    public static final int MARTES = 2;  //o sea 0000010
    public static final int MIERCOLES = 4; //o sea 0000100
    public static final int JUEVES = 8; //o sea 0001000
    public static final int VIERNES = 16; //o sea 0010000
    public static final int SABADO = 32; //o sea 0100000
    public static final int DOMINGO = 64; //o sea 1000000

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.calendar_fragment, container, false);

        //Botones
        btn_l = root.findViewById(R.id.button_lunes);
        btn_m = root.findViewById(R.id.button_martes);
        btn_x = root.findViewById(R.id.button_miercoles);
        btn_j= root.findViewById(R.id.button_jueves);
        btn_v = root.findViewById(R.id.button_viernes);
        btn_s= root.findViewById(R.id.button_sabado);
        btn_d= root.findViewById(R.id.button_domingo);

        //Cargar acciones de los botones
        btn_l.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(LUNES);
            }
        });
        btn_m.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(MARTES);
            }
        });
        btn_x.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(MIERCOLES);
            }
        });
        btn_j.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(JUEVES);
            }
        });
        btn_v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(VIERNES);
            }
        });
        btn_s.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(SABADO);
            }
        });
        btn_d.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                filtrarActividades(DOMINGO);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void filtrarActividades(int dia){
        Bundle bundle = new Bundle();
        Log.d(TAG, "Paso :" + dia);
        bundle.putString("param1", String.valueOf(dia));
        Navigation.findNavController(root).navigate(R.id.action_nav_calendar_to_selectDayFragment, bundle);
    }
}