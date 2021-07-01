package com.example.yosigo.Facilitador.ForumsFacilitador;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Map;

public class ForumListAdapter extends ArrayAdapter {

    private final Context context;
    private final Map<String,String> valuesList;
    List<String> nameList;

    public ForumListAdapter(Context context, Map<String,String> valuesList, List<String>nameList) {
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
        Button btn_modify = (Button) rowView.findViewById(R.id.modify_forum);
        btn_modify.setVisibility(View.VISIBLE);

        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", "Forum");
                bundle.putString("param2", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
            }
        });

        btn_asociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_asociateForumFragment, bundle);
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_modifyForumFragment, bundle);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference ForoDocument = FirebaseFirestore.getInstance().collection("forums").document(valuesList.get(name));

                ForoDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String pictograma = (String) document.get("Pictograma");

                                borrarMensajes(valuesList.get(name));

                                ForoDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseStorage.getInstance().getReference().child(pictograma).delete();
                                        Navigation.findNavController(view).navigate(R.id.action_nav_forums_self);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "No se ha podido eliminar " + name, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        return rowView;
    }

    private void borrarMensajes(String ForoID){
        FirebaseFirestore.getInstance()
                .collection("forums").document(ForoID)
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
                                        .collection("forums").document(ForoID)
                                        .collection("Messages").document(document.getId())
                                        .delete();
                            }
                        }
                    }
                });
    }
}
