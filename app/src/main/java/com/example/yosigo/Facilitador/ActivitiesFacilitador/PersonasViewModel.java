package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.util.Log;
import android.widget.ArrayAdapter;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonasViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> mText;
    private MutableLiveData<List<String>> list;
    private static final String TAG = "PERSONAS VIEW MODEL";
    FirebaseFirestore fb = FirebaseFirestore.getInstance();

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

        fb.collection("users")
                .document(MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task_facilitador) {
                    if (task_facilitador.isSuccessful()) {
                        DocumentSnapshot document_facilitador = task_facilitador.getResult();
                        if (document_facilitador.exists()) {
                            List<String> idArray = (List<String>) document_facilitador.get("Personas");
                            for (String id:idArray) {
                                Log.d(TAG, "Viendo: " + id);
                                fb.collection("users")
                                        .document(id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task_persona) {
                                                DocumentSnapshot document_persona = task_persona.getResult();
                                                String full_name = document_persona.getData().get("Nombre") + " " +
                                                        document_persona.getData().get("Apellidos") + " (" +
                                                        document_persona.getData().get("Apodo") + ")";

                                                userList.add(full_name);
                                                userMap.put(full_name, document_persona.getId());

                                                mText.setValue(userMap);
                                                list.setValue(userList);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task_facilitador.getException());
                    }
                }
            });
    }
}