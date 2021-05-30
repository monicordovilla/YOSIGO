package com.example.yosigo.Facilitador.Goals;

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

public class GoalsListViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> goals;
    private MutableLiveData<List<String>> nameList;
    private static final String TAG = "CATEGORIA View Model";

    public GoalsListViewModel() {
        goals = new MutableLiveData<>();
        nameList = new MutableLiveData<>();
        loadGoals();
    }

    public LiveData<Map<String, String>> getGoals() { return goals; }
    public LiveData<List<String>> getNames() {
        return nameList;
    }

    private void loadGoals(){
        Map<String, String> goalsMap = new HashMap<>();
        List<String> goalsList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("goals")
                .whereEqualTo("Facilitador" , MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                goalsMap.put((String) document.getData().get("Nombre"), (String) document.getId());
                                goalsList.add((String) document.getData().get("Nombre"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        goals.setValue(goalsMap);
                        nameList.setValue(goalsList);
                    }
                });
    }
}