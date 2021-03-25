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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class CreateActivity extends Fragment {

    private EditText nombre_actividad;
    private final String TAG = "Crear actividad";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private Button btn_picto, btn_meta, btn_categoria, btn_actividad;
    private static final int GALLERY_INTENT = 1;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_create_activity, container, false);

        //Obtenemos referencia del Edit Text
        nombre_actividad = (EditText) root.findViewById(R.id.editTextTextEmailAddress);
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto);
        btn_meta = (Button) root.findViewById(R.id.btn_up_meta);
        btn_categoria = (Button) root.findViewById(R.id.btn_up_category);
        btn_actividad = (Button) root.findViewById(R.id.btn_up_actividad);

        btn_picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        btn_meta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        btn_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        btn_actividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        return root;
    }

    private void agregarActividad(Task activity) {
        FirebaseFirestore.getInstance().collection("cities")
                .add(activity)
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

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("pictogramas").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se ha subido la foto con éxito", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}