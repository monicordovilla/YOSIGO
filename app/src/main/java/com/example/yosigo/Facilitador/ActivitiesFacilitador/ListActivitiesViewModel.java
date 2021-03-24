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
import java.util.List;

public class ListActivitiesViewModel extends ViewModel {

    private MutableLiveData<List<String>> mText;
    private static final String TAG = "ACTIVIDADES ";

    public ListActivitiesViewModel() {
        mText = new MutableLiveData<>();
        loadActivities();
    }

    public LiveData<List<String>> getText() {
        return mText;
    }

    private void loadActivities(){
        List <String> activitiesList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                activitiesList.add((String) document.getData().get("Nombre"));

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