package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static java.util.Objects.isNull;

public class CreateActivity extends Fragment {

    private EditText nombre_actividad;
    private final String TAG = "Crear actividad";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private String picto, meta, categoría;
    private Button btn_picto, btn_meta, btn_categoria, btn_actividad, btn_create;
    private static final int PICTO_INTENT = 1;
    private static final int META_INTENT = 2;
    private static final int CAT_INTENT = 3;
    private static final int ACT_INTENT = 4;
    private View root;
    private ArrayList<String> activities = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_create_activity, container, false);

        //Obtenemos referencia del Edit Text
        nombre_actividad = (EditText) root.findViewById(R.id.input_name_activity);
        //Obtenemos la referencia a los botones
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto);
        btn_meta = (Button) root.findViewById(R.id.btn_up_meta);
        btn_categoria = (Button) root.findViewById(R.id.btn_up_category);
        btn_actividad = (Button) root.findViewById(R.id.btn_up_actividad);
        btn_create = (Button) root.findViewById(R.id.btn_create_activity);

        btn_picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, PICTO_INTENT);
            }
        });

        btn_meta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, META_INTENT);
            }
        });

        btn_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, CAT_INTENT);
            }
        });

        btn_actividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                startActivityForResult(intent, ACT_INTENT);
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarActividad();
            }
        });

        return root;
    }

    private void agregarActividad() {
        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", nombre_actividad.getText().toString());
        data.put("Pictograma", picto);
        data.put("Meta", meta);
        data.put("Categoria", categoría);
        data.put("Tarea", activities);

        Log.d(TAG, "Actividad: " + nombre_actividad);
        Log.d(TAG, "Se va a subir: " + data);

        FirebaseFirestore.getInstance().collection("activities")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTO_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("pictogramas").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se ha subido la foto con éxito", Toast.LENGTH_SHORT).show();
                    picto = filePath.getPath();
                }
            });
        } else if(requestCode == META_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("metas").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se ha subido la foto con éxito", Toast.LENGTH_SHORT).show();
                    meta = filePath.getPath();
                }
            });
        } else if(requestCode == CAT_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("categoria").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se ha subido la foto con éxito: " + filePath.getPath() , Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Get: " + filePath.getPath());
                    categoría = filePath.getPath();
                }
            });
        } else if(requestCode == ACT_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("actividades").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se ha la actividad con exito", Toast.LENGTH_SHORT).show();
                    String activityPath = filePath.getPath();
                    Log.w(TAG, "Subido: " + activityPath);
                    activities.add(activityPath);
                }
            });
        }
    }
}