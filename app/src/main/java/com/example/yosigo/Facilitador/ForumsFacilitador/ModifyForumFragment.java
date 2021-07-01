package com.example.yosigo.Facilitador.ForumsFacilitador;

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

import com.bumptech.glide.Glide;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyForumFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final int PICTO_INTENT = 1;
    private final String TAG = "Crear foro";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private View root;
    private ImageView imagen;
    private EditText nombre;
    private Button btn_picto, btn_modify;

    private String mParam1;
    private Uri uri_picto;
    private String picto_antigua;

    public ModifyForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ModifyForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModifyForumFragment newInstance(String param1, String param2) {
        ModifyForumFragment fragment = new ModifyForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_modify_forum, container, false);

        //Obtenemos referencia del Edit Text
        nombre = (EditText) root.findViewById(R.id.input_name_forum);
        //Obtenemos referencia de la imagen
        imagen = (ImageView) root.findViewById(R.id.image_modify_forum);
        //Obtenemos la referencia a los botones
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto_forum);
        btn_modify = (Button) root.findViewById(R.id.btn_modify_forum);

        btn_picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, PICTO_INTENT);
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarForo();
            }
        });

        loadForum();
        return root;
    }

    private void loadForum() {
        Map<String, String> forumsList = new HashMap<>();

        FirebaseFirestore.getInstance().collection("forums").document(mParam1)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Error getting documents");
                    return;
                } else if (!document.exists()) {
                    Log.w(TAG, "No such document");
                    return;
                }
                nombre.setText((String) document.getData().get("Nombre"));

                if(document.getData().get("Pictograma") != null) {
                    picto_antigua = (String) document.getData().get("Pictograma");
                    storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(root)
                                    .load(uri)
                                    .into(imagen);
                        }
                    });
                }
            }
        });
    }

    private void modificarForo(){
        Map<String, Object> data = new HashMap<>();

        //Comprobar si se ha introducido una nueva foto
        if (uri_picto == null) {
            setData(data);
        } else {
            storageRef.child(picto_antigua).delete();
            StorageReference filePath_picto = storageRef.child("foros").child(uri_picto.getLastPathSegment());
            filePath_picto.putFile(uri_picto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    data.put("Pictograma", filePath_picto.getPath());
                    setData(data);
                }
            });
        }
    }

    private void setData(Map<String, Object> data){
        data.put("Nombre", nombre.getText().toString());

        fb.collection("forums")
                .document(mParam1).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Navigation.findNavController(root).navigate(R.id.action_modifyForumFragment_to_nav_forums);
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
            uri_picto = data.getData();
            imagen.setImageURI(uri_picto);
        }
    }
}