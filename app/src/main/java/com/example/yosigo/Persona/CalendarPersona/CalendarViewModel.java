package com.example.yosigo.Persona.CalendarPersona;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarViewModel extends ViewModel {
    // TODO: Llamar a las actividades de una persona
    private MutableLiveData<Map<String, Integer>> activities;
    private Map<String, Integer> id_activities = new HashMap<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ACTIVIDADES ";
    private String uuid;
    private Date date;

    public CalendarViewModel(String sesion) {
        activities = new MutableLiveData<>();
        uuid = sesion;
        date = new Date();
        getUserActivities();
    }

    public LiveData<Map<String, Integer>> getActivities() { return activities; }


    private  void getUserActivities(){
        db.collection("users").document("sesion").collection("activities")
                .whereGreaterThanOrEqualTo("Fecha inicio", date)
                .whereLessThanOrEqualTo("Fecha fin", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                id_activities.put(document.getId(), (Integer) document.getData().get("Dias Semana"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        activities.setValue(id_activities);
    }
}