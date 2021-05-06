package com.example.yosigo.Persona.ForumsPersona;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.yosigo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListForumsViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> pictos;
    private MutableLiveData<Map<String, String>> nombres;
    private MutableLiveData<List<String>> ids;
    private static final String TAG = "FOROS ";

    public ListForumsViewModel() {
        pictos = new MutableLiveData<>();
        nombres = new MutableLiveData<>();
        ids = new MutableLiveData<>();

        loadForums();
    }

    public LiveData<Map<String, String>> getPictos() {
        return pictos;
    }
    public LiveData<Map<String, String>> getNombre() {
        return nombres;
    }
    public LiveData<List<String>> getIds() {
        return ids;
    }

    private void loadForums() {
        Map<String, String> nameMaps = new HashMap<>();
        Map<String, String> pictosMaps = new HashMap<>();
        List<String> idList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("forums")
                .whereArrayContains("Personas", MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idList.add(document.getId());
                                nameMaps.put((String) document.getId(), (String) document.getData().get("Nombre"));
                                pictosMaps.put((String) document.getId(), (String) document.getData().get("Pictograma"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        pictos.setValue(pictosMaps);
                        nombres.setValue(nameMaps);
                        ids.setValue(idList);

                    }
                });
    }
}