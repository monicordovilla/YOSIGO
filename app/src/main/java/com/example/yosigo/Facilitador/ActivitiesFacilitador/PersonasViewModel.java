package com.example.yosigo.Facilitador.ActivitiesFacilitador;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonasViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> mText;
    private MutableLiveData<List<String>> list;
    private static final String TAG = "ACTIVIDADES ";

    public PersonasViewModel() {
        mText = new MutableLiveData<>();
        list = new MutableLiveData<>();
        loadUsers();
    }

    public LiveData<Map<String, String>> getUsers() { return mText; }
    public LiveData<List<String>> getNames() {
        return list;
    }

    private void loadUsers(){
        Map<String, String> userMap = new HashMap<>();
        List<String> userList = new ArrayList<String>();

        // TODO: Cargar los asociados al facilitador actual (variable session de SharedPreferences)
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("Rol", "Persona")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String full_name = document.getData().get("Nombre") + " " +
                                        document.getData().get("Apellidos") + " (" +
                                        document.getData().get("Apodo") + ")";
                                userList.add(full_name);
                                userMap.put(full_name, document.getId());

                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                //Log.d(TAG,  "Guardo: " + full_name);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        mText.setValue(userMap);
                        list.setValue(userList);
                    }
                });
    }
}