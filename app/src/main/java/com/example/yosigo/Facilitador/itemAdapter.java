package com.example.yosigo.Facilitador;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class itemAdapter extends ArrayAdapter {

    private final Context context;
    private final Map<String,String> valuesList;
    private List<String> nameList;
    private String type;

    public itemAdapter(Context context, String type, Map<String,String> valuesList, List<String>nameList) {
        super(context, R.layout.item_adapter, nameList);

        this.context = context;
        this.valuesList = valuesList;
        this.nameList = nameList;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView =  inflater.inflate(R.layout.item_adapter, parent, false);

        String name = nameList.get(position);

        //Text
        TextView textView = (TextView) rowView.findViewById(R.id.textViewAlias);
        textView.setText(name);
        textView.setId(position);

        //Buttons
        Button btn_delete = (Button) rowView.findViewById(R.id.delete_activity);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equals("Groups")) {
                    FirebaseFirestore.getInstance().collection("groups").document(valuesList.get(name))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Navigation.findNavController(view).navigate(R.id.action_nav_group_self);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "No se ha podido eliminar " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
                } else if (type.equals("Category")) {
                    DocumentReference CategoryDocument = FirebaseFirestore.getInstance().collection("categories").document(valuesList.get(name));
                    CategoryDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d( "ITEM ADAPTER", "Ruta: " + document.get("Categoria"));
                                    FirebaseStorage.getInstance().getReference().child((String) document.get("Pictograma")).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            CategoryDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseFirestore.getInstance().collection("activities")
                                                            .whereEqualTo("Facilitador" , MainActivity.sesion)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            if( document.getData().get("Categoria").equals(valuesList.get(name))){
                                                                                DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                                                        .collection("activities")
                                                                                        .document(document.getId());
                                                                                Map<String,Object> updates = new HashMap<>();
                                                                                updates.put("Categoria", FieldValue.delete());
                                                                                documentReference.update(updates);
                                                                            }
                                                                        }
                                                                    }
                                                                    Navigation.findNavController(view).navigate(R.id.action_nav_category_self);
                                                                }
                                                            });
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "No se ha podido eliminar " + name, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(context, "No se ha eliminado la imagen" + name, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.e("ITEM ADAPTER", "get failed with ", task.getException());
                            }
                        }
                    });
                } else if (type.equals("Goal")) {
                    DocumentReference GoalDocument = FirebaseFirestore.getInstance().collection("goals").document(valuesList.get(name));

                    GoalDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    FirebaseStorage.getInstance().getReference().child((String) document.get("Pictograma")).delete();

                                    GoalDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseFirestore.getInstance().collection("activities")
                                                    .whereEqualTo("Facilitador" , MainActivity.sesion)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    if( document.getData().get("Meta").equals(valuesList.get(name))){
                                                                        DocumentReference documentReference = FirebaseFirestore.getInstance()
                                                                                .collection("activities")
                                                                                .document(document.getId());
                                                                        Map<String,Object> updates = new HashMap<>();
                                                                        updates.put("Meta", FieldValue.delete());
                                                                        documentReference.update(updates);
                                                                    }
                                                                }
                                                            }
                                                            Navigation.findNavController(view).navigate(R.id.action_nav_goals_self);
                                                        }
                                                    });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "No se ha podido eliminar " + name, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Log.e("ITEM ADAPTER", "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        return rowView;
    }
}