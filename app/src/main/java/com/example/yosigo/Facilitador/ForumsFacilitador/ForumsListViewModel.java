package com.example.yosigo.Facilitador.ForumsFacilitador;

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

import java.util.HashMap;
import java.util.Map;

public class ForumsListViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> mText;
    private static final String TAG = "FOROS ";

    public ForumsListViewModel() {
        mText = new MutableLiveData<>();
        loadForums();
    }

    public LiveData<Map<String, String>> getText() {
        return mText;
    }

    private void loadForums() {
        Map<String, String> forumsList = new HashMap<>();

        FirebaseFirestore.getInstance().collection("forums")
                .whereEqualTo("Facilitador" , MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                forumsList.put((String) document.getData().get("Nombre"), (String) document.getId());

                                Log.d(TAG, document.getId() + " => " + document.getData() + ". Cuyo nombre es: " + document.getData().get("Nombre"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        mText.setValue(forumsList);
                    }
                });
    }
}