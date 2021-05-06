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

import com.example.yosigo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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

    private PersonasViewModel mViewModel;
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ASOCIAR ACTIVIDAD";
    private String mParam1;
    private ListView list, list_dias_semana;
    EditText fecha_inicio, fecha_fin;
    Button btn_asociar;
    Map<String, String> userMap = new HashMap<>();
    List<String> users = new ArrayList<>();
    List<String> dias_semana = new ArrayList<String>(
            Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    );
    List<String> asociados = new ArrayList<>();
    private View root;

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
        mViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        root = inflater.inflate(R.layout.asociate_fragment, container, false);

        list = root.findViewById(R.id.list_asociate_user);
        btn_asociar = root.findViewById(R.id.button_asociar_actividad);
        fecha_inicio = root.findViewById(R.id.editTextInicio);
        fecha_fin = root.findViewById(R.id.editTextFin);
        fecha_inicio.setOnClickListener(this);
        fecha_fin.setOnClickListener(this);

        list_dias_semana = root.findViewById(R.id.list_asociate_dias);
        list_dias_semana.setAdapter(new ArrayAdapter<String>(
                        root.getContext(),
                        android.R.layout.simple_list_item_multiple_choice,
                        dias_semana
                )
        );
        list_dias_semana.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                userMap = stringStringMap;
            }
        });

        mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
            @Override
            public void onChanged(List<String> strings) {
                users = strings;

                list.setAdapter(new ArrayAdapter<String>(
                                root.getContext(),
                                android.R.layout.simple_list_item_multiple_choice,
                                strings
                        )
                );
                list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }
        });

        btn_asociar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comprobar si se han introducido las fechas
                Timestamp timestamp_inicio, timestamp_fin;
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
                Log.d(TAG, "Fecha inicio: " + timestamp_inicio);
                Log.d(TAG, "Fecha fin: " + timestamp_fin);

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
                int dias_semana = 0;
                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        dias_semana += Math.pow(2,i);
                    }
                }

                if(dias_semana == 0){
                    Toast.makeText(getContext(), "No se ha escogido día de la semana", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Agregar datos a cada usuario
                asociados.clear();
                checked = list.getCheckedItemPositions();
                len = checked.size();
                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        String item = users.get(i);
                        Log.d(TAG, "Item=>: " + item);
                        String id_user = userMap.get(item);
                        asociados.add(id_user);
                        Log.d(TAG, "Asociados: " + asociados);

                        Map<String, Object> data = new HashMap<>();
                        data.put("Fecha Inicio", timestamp_inicio);
                        data.put("Fecha Fin", timestamp_fin);
                        data.put("Dias semana", dias_semana);
                        Log.d(TAG, "Datos introducidos: " + data);
                        FirebaseFirestore.getInstance()
                                .collection("users")
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
                }
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