package com.example.yosigo.Facilitador.gallery;

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

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<List<String>> mText;
    private static final String TAG = "ACTIVIDADES ";

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        List <String> activitiesList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                /*TextView new_text = new TextView(getApplicationContext());
                                ConstraintSet set = new ConstraintSet();
                                //set.connect(new_text.getId(), ConstraintSet.LEFT, clayout.getId(), ConstraintSet.LEFT, 8);
                                //set.connect(new_text.getId(), ConstraintSet.RIGHT, clayout.getId(), ConstraintSet.RIGHT, 8);

                                String s = document.getData().get("id") + ": Producto Nº"
                                        + document.getData().get("Nombre");
                                new_text.setText(s);*/
                                //clayout.addView(new_text);*/
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

    public LiveData<List<String>> getText() {
        return mText;
    }
}