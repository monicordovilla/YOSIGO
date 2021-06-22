package com.example.yosigo.Persona.ActivitiesPersona;

import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCategoryViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> categoty_names;
    private MutableLiveData<Map<String, String>> categoty_pictos;
    private MutableLiveData<List<String>> categoty_ids;
    private static final String TAG = "PERSONA CATEGORY View Model";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SearchCategoryViewModel() {
        categoty_names = new MutableLiveData<>();
        categoty_pictos = new MutableLiveData<>();
        categoty_ids = new MutableLiveData<>();

        loadCategories();
    }

    public MutableLiveData<List<String>> getCategoty_ids() { return categoty_ids; }
    public MutableLiveData<Map<String, String>> getCategoty_names() { return categoty_names; }
    public MutableLiveData<Map<String, String>> getCategoty_pictos() { return categoty_pictos; }

    private void loadCategories(){
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
                            //Insertar identificador de actividad
                            String id_activity = document.getId();

                            //Obtener la información de cada actividad que tiene asingada
                            db.collection("activities").document(id_activity).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> activity) {
                                        if (activity.isSuccessful()) {
                                            DocumentSnapshot document_activity = activity.getResult();
                                            if (document_activity.exists()) {
                                                String id_category = document_activity.getData().get("Categoria").toString();
                                                Log.d(TAG, "Array : " + id_category + " contenido en \n"
                                                + idsList + " ? " + idsList.contains(id_category));

                                                if (!idsList.contains(id_category)) {
                                                    idsList.add(id_category);
                                                    db.collection("categories").document(id_category).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> category) {
                                                                    if (category.isSuccessful()) {
                                                                        DocumentSnapshot document_category = category.getResult();
                                                                        if (document_category.exists()) {
                                                                            namesMap.put(id_category, (String) document_category.getData().get("Nombre"));
                                                                            pictosMap.put(id_category, (String) document_category.getData().get("Pictograma"));

                                                                            Log.d(TAG, "ID: " + id_category + "=>" + document_category.getData());
                                                                        } else {
                                                                            Log.d(TAG, "No such document");
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "No such document");
                                                                    }
                                                                    categoty_ids.setValue(idsList);
                                                                    categoty_names.setValue(namesMap);
                                                                    categoty_pictos.setValue(pictosMap);
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
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