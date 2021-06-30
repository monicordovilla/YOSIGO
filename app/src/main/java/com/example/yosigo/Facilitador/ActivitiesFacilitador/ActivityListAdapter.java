package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityListAdapter extends ArrayAdapter {

    private static final String TAG = "ITEM ADAPTER";
    private final Context context;
    private final Map<String,String> valuesList;
    List<String> nameList;

    public ActivityListAdapter(Context context, Map<String,String> valuesList, List<String>nameList) {
        super(context, R.layout.list_activities_item, nameList);

        this.context = context;
        this.valuesList = valuesList;
        this.nameList = nameList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_activities_item, parent, false);

        String name = nameList.get(position);

        //Text
        TextView textView = (TextView) rowView.findViewById(R.id.textViewAlias);
        textView.setText(name);
        textView.setId(position);

        //Buttons
        Button btn_watch = (Button) rowView.findViewById(R.id.watch_activity);
        Button btn_asociate = (Button) rowView.findViewById(R.id.asociate_activity);
        Button btn_delete = (Button) rowView.findViewById(R.id.delete_activity);

        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_activity_to_activityViewFragment, bundle);
            }
        });

        btn_asociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_activity_to_asociateFragment, bundle);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference ActivityDocument = FirebaseFirestore.getInstance().collection("activities").document(valuesList.get(name));

                ActivityDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String pictograma = (String) document.get("Pictograma");
                                List<String> tareasArray = (List<String>) document.getData().get("Tarea");

                                borrarValoracion(valuesList.get(name));
                                borrarEntrega(valuesList.get(name));
                                borrarChat(valuesList.get(name));

                                ActivityDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseStorage.getInstance().getReference().child(pictograma).delete();
                                        for(String tarea: tareasArray){
                                            FirebaseStorage.getInstance().getReference().child(tarea).delete();
                                        }

                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(MainActivity.sesion)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task_facilitador) {
                                                        if (task_facilitador.isSuccessful()) {
                                                            DocumentSnapshot document_facilitador = task_facilitador.getResult();
                                                            if (document_facilitador.exists()) {
                                                                List<String> idUserArray = (List<String>) document_facilitador.get("Personas");

                                                                for(String id: idUserArray) {
                                                                    FirebaseFirestore.getInstance().collection("users")
                                                                            .document(id).collection("activities")
                                                                            .document(valuesList.get(name)).delete();
                                                                }
                                                            }
                                                        }
                                                        Navigation.findNavController(view).navigate(R.id.action_nav_activity_self);
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
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        return rowView;
    }

    private void borrarValoracion(String Activityid){
        FirebaseFirestore.getInstance()
                .collection("activities").document(Activityid)
                .collection("Assessment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("Sugerencia") != null){
                                    FirebaseStorage.getInstance().getReference()
                                            .child((String) document.getData().get("Sugerencia")).delete();
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("activities").document(Activityid)
                                        .collection("Assessment").document(document.getId())
                                        .delete();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void borrarEntrega(String Activityid){
        FirebaseFirestore.getInstance()
                .collection("activities").document(Activityid)
                .collection("Feedback").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("Archivo") != null){
                                    FirebaseStorage.getInstance().getReference()
                                            .child((String) document.getData().get("Archivo")).delete();
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("activities").document(Activityid)
                                        .collection("Feedback").document(document.getId())
                                        .delete();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void borrarChat(String Activityid){
        FirebaseFirestore.getInstance()
                .collection("activities").document(Activityid)
                .collection("Chat").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                borrarMensajes(Activityid, document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void borrarMensajes(String Activityid, String ChatID){
        FirebaseFirestore.getInstance()
                .collection("activities").document(Activityid)
                .collection("Chat").document(ChatID)
                .collection("Messages").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getData().get("Tipo").equals("Texto")){
                                    FirebaseStorage.getInstance().getReference().child((String) document.getData().get("Contenido")).delete();
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("forums").document(ChatID)
                                        .collection("Messages").document(document.getId())
                                        .delete();
                            }

                            FirebaseFirestore.getInstance()
                                    .collection("activities").document(Activityid)
                                    .collection("Chat").document(ChatID)
                                    .delete();
                        }
                    }
                });
    }
}
