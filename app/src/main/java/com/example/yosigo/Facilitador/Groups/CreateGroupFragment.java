package com.example.yosigo.Facilitador.Groups;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupFragment extends Fragment {

    private final String TAG = "Crear grupo";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private ViewModel usersViewModel;

    private View root;
    private EditText nombre;
    private ListView list;
    private Button btn_create;

    Map<String, String> userMap = new HashMap<>();
    List<String> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        usersViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        root = inflater.inflate(R.layout.fragment_create_group, container, false);

        nombre = root.findViewById(R.id.input_name_goal);
        list = root.findViewById(R.id.list_selected_users);
        btn_create = root.findViewById(R.id.btn_create_goal);

        getUsers();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarGrupo();
            }
        });

        return root;
    }

    private void agregarGrupo(){
        if(nombre.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "No se han introducido nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        //Guardar nombre
        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", nombre.getText().toString());

        //Guardar facilitador
        String sesion = MainActivity.sesion;
        data.put("Facilitador", sesion);

        //Guardar usuarios seleccionados
        SparseBooleanArray checked = list.getCheckedItemPositions();
        int len = checked.size();
        List<String> usuarios = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String usuario = userMap.get(users.get(i));
                usuarios.add(usuario);
            }
        }

        if(usuarios.size() < 2){
            Toast.makeText(getContext(), "Debe introducir al menos 2 usuarios", Toast.LENGTH_LONG).show();
            return;
        }

        data.put("Usuarios", usuarios);

        fb.collection("groups")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Navigation.findNavController(root).navigate(R.id.action_createGroupFragment_to_nav_group);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void getUsers(){
        fb.collection("users")
                .document(MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task_facilitador) {
                        if (task_facilitador.isSuccessful()) {
                            DocumentSnapshot document_facilitador = task_facilitador.getResult();
                            if (document_facilitador.exists()) {
                                List<String> idArray = (List<String>) document_facilitador.get("Personas");
                                Log.d(TAG, "Array user ids: " + idArray);
                                for (String id:idArray) {
                                    Log.d(TAG, "Viendo: " + id);
                                    fb.collection("users")
                                            .document(id)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task_persona) {
                                                    DocumentSnapshot document_persona = task_persona.getResult();
                                                    String full_name = document_persona.getData().get("Nombre") + " " +
                                                            document_persona.getData().get("Apellidos") + " (" +
                                                            document_persona.getData().get("Apodo") + ")";

                                                    Log.d(TAG, "Añadir: " + full_name);
                                                    users.add(full_name);
                                                    userMap.put(full_name, document_persona.getId());

                                                    list.setAdapter(new ArrayAdapter<String>(
                                                                    root.getContext(),
                                                                    android.R.layout.simple_list_item_multiple_choice,
                                                                    users
                                                            )
                                                    );
                                                    list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task_facilitador.getException());
                        }
                    }
                });
    }
}