package com.example.yosigo.Facilitador.ActivitiesFacilitador;

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

public class ListActivitiesViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> mText;
    private MutableLiveData<List<String>> list;
    private static final String TAG = "ACTIVIDADES ";
    private String sesion;

    public ListActivitiesViewModel() {
        mText = new MutableLiveData<>();
        loadActivities();
        this.sesion = MainActivity.sesion;
    }

    public LiveData<Map<String, String>> getText() {
        return mText;
    }

    private void loadActivities(){
        Map<String, String> activitiesList = new HashMap<>();

        FirebaseFirestore.getInstance().collection("activities")
                .whereEqualTo("Facilitador" , MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                activitiesList.put((String) document.getData().get("Nombre"), (String) document.getId());

                                Log.d(TAG, document.getId() + " => " + document.getData() + ". Cuyo nombre es: " + document.getData().get("Nombre"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        mText.setValue(activitiesList);
                    }
                });
    }
}