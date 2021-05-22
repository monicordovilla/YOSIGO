package com.example.yosigo.Persona.ActivitiesPersona;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.yosigo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivitiesPersonaViewModel extends ViewModel {
    private MutableLiveData<List<Activity>> activities;
    private MutableLiveData<Map<String, String>> activities_names;
    private MutableLiveData<Map<String, String>> activities_pictos;
    private MutableLiveData<List<String>> activities_ids;
    private static final String TAG = "ACTIVIDAD View Model";private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ListActivitiesPersonaViewModel() {
        activities = new MutableLiveData<>();
        activities_names = new MutableLiveData<>();
        activities_pictos = new MutableLiveData<>();
        activities_ids = new MutableLiveData<>();

        loadActivities();
    }

    public MutableLiveData<List<Activity>> getActivities() { return activities; }
    public MutableLiveData<List<String>> getActivities_ids() { return activities_ids; }
    public MutableLiveData<Map<String, String>> getActivities_names() { return activities_names; }
    public MutableLiveData<Map<String, String>> getActivities_pictos() { return activities_pictos; }

    private void loadActivities(){
        List<Activity> activityList = new ArrayList<>();
        List<String> idsList = new ArrayList<>();
        Map<String, String> namesMap = new HashMap<>();
        Map<String, String> pictosMap = new HashMap<>();

        String sesion = MainActivity.sesion;
        //Comprobar las actividades que tiene asignadas
        db.collection("users").document(sesion).collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Activity new_activity = new Activity();
                                //Insertar fechas de inicio y fin de actividad
                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                Date fecha_inicio = document.getDate("Fecha Inicio", behavior);
                                Date fecha_fin = document.getDate("Fecha Fin", behavior);
                                new_activity.setFecha_fin(fecha_fin);
                                new_activity.setFecha_inicio(fecha_inicio);

                                //Insertar identificador de actividad
                                String id = document.getId();
                                new_activity.setId(id);
                                idsList.add(id);

                                //Guardar los días que tiene asignada la actividad actual
                                new_activity.setDias(Integer.valueOf(document.getData().get("Dias semana").toString()));

                                //Obtener la información de cada actividad que tiene asingada
                                db.collection("activities").document(id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> activity) {
                                                if (activity.isSuccessful()) {
                                                    DocumentSnapshot document_activity = activity.getResult();
                                                    if (document_activity.exists()) {
                                                        String nombre = document_activity.getData().get("Nombre").toString();
                                                        new_activity.setNombre(nombre);
                                                        namesMap.put(id, nombre);

                                                        String picto = document_activity.getData().get("Pictograma").toString();
                                                        new_activity.setImagen(picto);
                                                        pictosMap.put(id, picto);

                                                        Log.d(TAG, "ID: " + id + "=>" + document_activity.getData());
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }

                                                    activityList.add(new_activity);
                                                    activities.setValue(activityList);
                                                    activities_ids.setValue(idsList);
                                                    activities_names.setValue(namesMap);
                                                    activities_pictos.setValue(pictosMap);
                                                } else {
                                                    Log.d(TAG, "get failed with ", activity.getException());
                                                }
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