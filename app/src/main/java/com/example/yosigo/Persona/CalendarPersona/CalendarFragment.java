package com.example.yosigo.Persona.CalendarPersona;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.Persona.itemGrid;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CALENDARIO" ;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private Button btn_l, btn_m , btn_x, btn_j, btn_v, btn_s, btn_d;
    private GridView listado;
    private View root;
    private List<String> actividades_id = new ArrayList<>();
    private Map<String, String> actividades_nombre = new HashMap<>();
    private Map<String, String> actividades_pictos = new HashMap<>();
    private Map<String, Integer> dias_actividades = new HashMap<>();
    private List<String> actividades_id_filtradas = new ArrayList<>();
    private Map<String, String> actividades_nombre_filtradas = new HashMap<>();
    private Map<String, String> actividades_pictos_filtradas = new HashMap<>();

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
        btn_l = (Button) root.findViewById(R.id.button_lunes);
        btn_m = (Button) root.findViewById(R.id.button_martes);
        btn_x = (Button) root.findViewById(R.id.button_miercoles);
        btn_j= (Button) root.findViewById(R.id.button_jueves);
        btn_v = (Button) root.findViewById(R.id.button_viernes);
        btn_s= (Button) root.findViewById(R.id.button_sabado);
        btn_d= (Button) root.findViewById(R.id.button_domingo);

        //Listado
        listado= (GridView) root.findViewById(R.id.grid_calendar);

        //Cargar datos
        getActivities();

        //Cargar acciones de los botones
        btn_l.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & LUNES) == LUNES){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "LUNES => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_m.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & MARTES) == MARTES){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "MARTES => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_x.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & MIERCOLES) == MIERCOLES){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "MIERCOLES => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_j.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & JUEVES) == JUEVES){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "JUEVES => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & VIERNES) == VIERNES){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "VIERNES => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_s.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & SABADO) == SABADO){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "SABADO => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });
        btn_d.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actividades_nombre_filtradas.clear();
                actividades_pictos_filtradas.clear();
                actividades_id_filtradas.clear();

                for(String id: actividades_id){
                    Integer dias = dias_actividades.get(id);
                    if((dias & DOMINGO) == DOMINGO){
                        actividades_id_filtradas.add(id);
                        actividades_nombre_filtradas.put(id, actividades_nombre.get(id));
                        actividades_pictos_filtradas.put(id, actividades_pictos.get(id));
                    }
                }
                Log.d(TAG, "DOMINGO => " + actividades_nombre_filtradas);
                GridAdapter adapter = new GridAdapter(
                        root.getContext(),
                        actividades_id_filtradas,
                        actividades_nombre_filtradas,
                        actividades_pictos_filtradas
                );
                listado.setAdapter(adapter);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void getActivities(){
        //Obtenemos la referencia al archvivo datos
        SharedPreferences preferencias= getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        //Extraemos del archivo datos el campo email, si este no existe se devuelve un campo vacío
        String sesion = preferencias.getString("sesion","");
        Log.d(TAG, "uuid => " + sesion);
        db.collection("users").document(sesion).collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                Log.d(TAG, id + " => " + document.getData());
                                dias_actividades.put(id, Integer.valueOf(document.getData().get("Dias semana").toString()));

                                db.collection("activities").document(id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> activity) {
                                        if (activity.isSuccessful()) {
                                            DocumentSnapshot document_activity = activity.getResult();
                                            if (document_activity.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document_activity.getData());
                                                itemGrid item_grid = new itemGrid(
                                                        (String) document_activity.getData().get("Nombre"),
                                                        document_activity.getId(),
                                                        document_activity.getData().get("Pictograma").toString()
                                                );
                                                actividades_id.add(id);
                                                actividades_nombre.put(id, document_activity.getData().get("Nombre").toString());
                                                actividades_pictos.put(id, document_activity.getData().get("Pictograma").toString());
                                                Log.d(TAG, "Actividades" + actividades_nombre);
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", activity.getException());
                                        }
                                        Log.d(TAG, "Dia se la semana" + Calendar.DAY_OF_WEEK);
                                        GridAdapter adapter = new GridAdapter(root.getContext(), actividades_id, actividades_nombre, actividades_pictos);
                                        listado.setAdapter(adapter);
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}