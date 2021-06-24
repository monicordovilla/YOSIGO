package com.example.yosigo.Facilitador.Groups;

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

public class GroupListViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> groups;
    private MutableLiveData<List<String>> nameList;
    private static final String TAG = "GRUPOS View Model";

    public GroupListViewModel() {
        groups = new MutableLiveData<>();
        nameList = new MutableLiveData<>();
        loadGroups();
    }

    public LiveData<Map<String, String>> getGroups() {
        return groups;
    }
    public LiveData<List<String>> getNames() {
        return nameList;
    }

    private void loadGroups(){
        Map<String, String> groupsMap = new HashMap<>();
        List<String> groupsList = new ArrayList<>();
        List<String> idArray = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("groups")
                .whereEqualTo("Facilitador" , MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                groupsMap.put((String) document.getData().get("Nombre"), (String) document.getId());
                                groupsList.add((String) document.getData().get("Nombre"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        groups.setValue(groupsMap);
                        nameList.setValue(groupsList);
                    }
                });
    }
}