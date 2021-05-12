package com.example.yosigo.Facilitador.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCategoryFragment extends Fragment {

    private static final int PICTO_INTENT = 1;
    private final String TAG = "Crear categoría";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private View root;
    private ImageView imagen;
    private EditText nombre;
    private Button btn_picto, btn_create;
    private Uri uri_picto;
    private String picto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_create_category, container, false);

        //Obtenemos referencia del Edit Text
        nombre = (EditText) root.findViewById(R.id.input_name_category);
        //Obtenemos referencia de la imagen
        imagen = (ImageView) root.findViewById(R.id.image_create_category);
        //Obtenemos la referencia a los botones
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto_category);
        btn_create = (Button) root.findViewById(R.id.btn_create_category);

        btn_picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, PICTO_INTENT);
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarCategoría();
            }
        });

        return root;
    }

    private void agregarCategoría(){
        //Guardar nombre
        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", nombre.getText().toString());

        //Guardar facilitador
        String sesion = MainActivity.sesion;
        data.put("Facilitador", sesion);

        //Guardar fotos en firestore storage
        if (uri_picto == null) {
            Toast.makeText(getContext(), "No se ha introducido pictograma descriptivo", Toast.LENGTH_LONG).show();
            return;
        }

        StorageReference filePath_picto = storageRef.child("categoría").child(uri_picto.getLastPathSegment());
        filePath_picto.putFile(uri_picto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picto = filePath_picto.getPath();
                data.put("Pictograma", picto);
                fb.collection("categories")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Navigation.findNavController(root).navigate(R.id.action_createCategoryFragment_to_nav_category);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTO_INTENT && resultCode == RESULT_OK ){
            uri_picto = data.getData();
            imagen.setImageURI(uri_picto);
        }
    }
}