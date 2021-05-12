package com.example.yosigo.Facilitador.Category;

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

public class CategoryListViewModel extends ViewModel {
    private MutableLiveData<Map<String, String>> categories;
    private MutableLiveData<List<String>> nameList;
    private static final String TAG = "CATEGORIA ";

    public CategoryListViewModel() {
        categories = new MutableLiveData<>();
        nameList = new MutableLiveData<>();
        loadCategories();
    }

    public LiveData<Map<String, String>> getCategories() {
        return categories;
    }
    public LiveData<List<String>> getNames() {
        return nameList;
    }

    private void loadCategories(){
        Map<String, String> categoryMap = new HashMap<>();
        List<String> categoryList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("categories")
                .whereEqualTo("Facilitador" , MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                categoryMap.put((String) document.getId(), (String) document.getData().get("Nombre"));
                                categoryList.add((String) document.getData().get("Nombre"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        categories.setValue(categoryMap);
                        nameList.setValue(categoryList);
                    }
                });
    }
}