package com.example.yosigo.Persona.CalendarPersona;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CalendarFragment extends Fragment {

    private CalendarViewModel mViewModel;
    Button btn_l, btn_m , btn_x, btn_j, btn_v, btn_s, btn_d;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.calendar_fragment, container, false);

        btn_l = (Button) root.findViewById(R.id.button_lunes);
        btn_m = (Button) root.findViewById(R.id.button_martes);
        btn_x = (Button) root.findViewById(R.id.button_miercoles);
        btn_j= (Button) root.findViewById(R.id.button_jueves);
        btn_v = (Button) root.findViewById(R.id.button_viernes);
        btn_s= (Button) root.findViewById(R.id.button_sabado);
        btn_d= (Button) root.findViewById(R.id.button_domingo);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        mViewModel.getActivities().observe(getViewLifecycleOwner(), new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(Map<String, Integer> activitiesMap) {
                for (Map.Entry<String, Integer> entry : activitiesMap.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());

                    FirebaseFirestore.getInstance()
                            .collection("activities")
                            .document(entry.getKey())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                        }
                    });
                }
            }
        });
    }

}