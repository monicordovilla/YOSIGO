package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yosigo.Facilitador.Groups.GroupListViewModel;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsociateFragment extends Fragment implements View.OnClickListener {

    private PersonasViewModel personasViewModel;
    private GroupListViewModel groupListViewModel;
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ASOCIAR ACTIVIDAD";
    private final FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private View root;
    private ListView list_personas, list_grupos, list_dias_semana;
    private EditText fecha_inicio, fecha_fin;
    private TextView name_activity;
    private Button btn_asociar;

    private Map<String, String> userMap = new HashMap<>();
    private Map<String, String> groupMap = new HashMap<>();
    private List<String> users = new ArrayList<>();
    private List<String> grupos = new ArrayList<>();
    private Timestamp timestamp_inicio, timestamp_fin;
    private String mParam1;
    private int flag_dias_semana;

    List<String> dias_semana = new ArrayList<String>(
            Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    );

    public static AsociateFragment newInstance(String param1) {
        AsociateFragment fragment = new AsociateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        personasViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        groupListViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        root = inflater.inflate(R.layout.asociate_fragment, container, false);

        //Obtener nombre de actividad
        name_activity = root.findViewById(R.id.asociate_activity_name);
        getName();

        //Obtener listas de grupos y usuarios
        list_personas = root.findViewById(R.id.list_asociate_user);
        list_grupos = root.findViewById(R.id.list_asociate_group);

        //Selector de fecha de inicio y fin
        fecha_inicio = root.findViewById(R.id.editTextInicio);
        fecha_fin = root.findViewById(R.id.editTextFin);
        fecha_inicio.setOnClickListener(this);
        fecha_fin.setOnClickListener(this);

        //Lista de días de la semana
        list_dias_semana = root.findViewById(R.id.list_asociate_dias);
        list_dias_semana.setAdapter(new ArrayAdapter<String>(
                        root.getContext(),
                        android.R.layout.simple_list_item_multiple_choice,
                        dias_semana
                )
        );
        list_dias_semana.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Boton asociar actividad a los usuarios seleccionados
        btn_asociar = root.findViewById(R.id.button_asociar_actividad);
        btn_asociar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedUsers();
            }
        });

        groupListViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                groupMap = stringStringMap;
            }
        });

        personasViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                userMap = stringStringMap;
            }
        });

        groupListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
            @Override
            public void onChanged(List<String> strings) {
                grupos = strings;

                list_grupos.setAdapter(new ArrayAdapter<String>(
                                root.getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                strings
                        )
                );
                list_grupos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });

        personasViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
            @Override
            public void onChanged(List<String> strings) {
                users = strings;

                list_personas.setAdapter(new ArrayAdapter<String>(
                                root.getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                strings
                        )
                );
                list_personas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editTextInicio:
                showDatePickerDialog(fecha_inicio);
                break;

            case R.id.editTextFin:
                showDatePickerDialog(fecha_fin);
                break;
        }
    }

    private void getName(){
        fb.collection("activities")
                .document(mParam1).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                name_activity.setText(document.getData().get("Nombre").toString());
                            }
                        }
                    }
                });
    }

    private void getSelectedUsers(){
        //Comprobar si se han introducido las fechas
        if(fecha_inicio.equals("")){
            Toast.makeText(getContext(), "No se ha escogido fecha inicio", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String inDate = fecha_inicio.getText().toString();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            try {
                date = df.parse(inDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "No se ha escogido fecha inicio", Toast.LENGTH_SHORT).show();
            }

            timestamp_inicio = new Timestamp(date);
        }
        if(fecha_fin.equals("")){
            Toast.makeText(getContext(), "No se ha escogido fecha final", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String inDate = fecha_fin.getText().toString();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            try {
                date = df.parse(inDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "No se ha escogido fecha final", Toast.LENGTH_SHORT).show();
            }

            timestamp_fin = new Timestamp(date);
        }

        //Escoger dias de la semana
        SparseBooleanArray checked = list_dias_semana.getCheckedItemPositions();
        int len = checked.size();
        /*BIT FLAG DIAS DE LA SEMANA
         * LUNES - 1
         * MARTES - 2
         * MIERCOLES - 4
         *
         * Los días son potencias de 2
         * */
        flag_dias_semana = 0;
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                flag_dias_semana += Math.pow(2,i);
            }
        }

        if(flag_dias_semana == 0){
            Toast.makeText(getContext(), "No se ha escogido día de la semana", Toast.LENGTH_SHORT).show();
            return;
        }

        //Agregar datos a cada usuario
        checked = list_personas.getCheckedItemPositions();
        len = checked.size();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String item = users.get(i);
                String id_user = userMap.get(item);
                asociateActivity(id_user);
            }
        }

        checked = list_grupos.getCheckedItemPositions();
        len = checked.size();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String item = grupos.get(i);
                String id_group = groupMap.get(item);

                fb.collection("groups")
                        .document(id_group)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ArrayList<String> userlist = (ArrayList<String>) document.getData().get("Usuarios");

                                        Log.d(TAG, userlist.toString());
                                        for (String id_user: userlist){
                                            asociateActivity(id_user);
                                        }
                                    }
                                }
                            }
                        });
            }
        }
        Toast.makeText(
                getContext(),
                "Se asociado la actividad a los participantes seleccionados con éxito",
                Toast.LENGTH_LONG
        ).show();
    }

    private void asociateActivity(String id_user){
        Map<String, Object> data = new HashMap<>();
        data.put("Fecha Inicio", timestamp_inicio);
        data.put("Fecha Fin", timestamp_fin);
        data.put("Dias semana", flag_dias_semana);
        fb.collection("users")
                .document(id_user)
                .collection("activities")
                .document(mParam1)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void showDatePickerDialog(EditText edit_text) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate =
                                twoDigits(day) + "/" +
                                twoDigits(month+1) +
                                "/" + year;
                edit_text.setText(selectedDate);
            }
        });

        newFragment.show( getActivity().getFragmentManager(), "Date Picker");
    }

    private String twoDigits(int number){
        if (number <= 9) return "0" + number;
        else return String.valueOf(number);
    }
}