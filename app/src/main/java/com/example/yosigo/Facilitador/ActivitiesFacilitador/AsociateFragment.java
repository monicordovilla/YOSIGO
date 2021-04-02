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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsociateFragment extends Fragment implements View.OnClickListener {

    private PersonasViewModel mViewModel;
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ASOCIAR ACTIVIDAD" ;
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
            Log.d(TAG, "Recibido id: " + mParam1);
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
                if(fecha_inicio == null){
                    Toast.makeText(getContext(), "No se ha escogido fecha inicio", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(fecha_fin == null){
                    Toast.makeText(getContext(), "No se ha escogido fecha final", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Escoger dias de la semana
                SparseBooleanArray checked = list_dias_semana.getCheckedItemPositions();
                int len = checked.size();
                int dias_semana = 0;
                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        dias_semana += Math.pow(2,i);
                    }
                }
                Log.w(TAG, "Dias de la semana: " + dias_semana);

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
                        Log.d(TAG, " Item => " + item);
                        asociados.add(userMap.get(item));

                        Map<String, Object> data = new HashMap<>();
                        data.put("Fecha Inicio", fecha_inicio.getText());
                        data.put("Fecha Fin", fecha_fin.getText());
                        data.put("Dias semana", dias_semana);
                        Log.w(TAG, "Datos introducidos: " + dias_semana);
                        /*FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(item)
                                .collection("activities")
                                .document(mParam1)
                                .set(data)
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
                                });*/
                    }
                }
                Log.d(TAG, " Seleccionados => " + asociados);
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
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                edit_text.setText(selectedDate);
            }
        });

        newFragment.show( getActivity().getFragmentManager(), "Date Picker");
    }
}