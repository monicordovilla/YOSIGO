package com.example.yosigo.Facilitador.Groups;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyGroupsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private final String TAG = "Modificar grupo";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private ViewModel usersViewModel;

    private View root;
    private EditText nombre;
    private ListView list;
    private Button btn_modify;

    private String mParam1;
    Map<String, String> userMap = new HashMap<>();
    List<String> users = new ArrayList<>();

    public ModifyGroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ModifyGroupsFragment.
     */
    public static ModifyGroupsFragment newInstance(String param1) {
        ModifyGroupsFragment fragment = new ModifyGroupsFragment();
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
        usersViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        root = inflater.inflate(R.layout.fragment_modify_groups, container, false);

        nombre = root.findViewById(R.id.input_name_group);
        list = root.findViewById(R.id.list_selected_users);
        btn_modify = root.findViewById(R.id.btn_modify_group);

        getUsers();

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarGrupo();
            }
        });

        return root;
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
                                for (String id:idArray) {
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
                                getData();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task_facilitador.getException());
                        }
                    }
                });
    }

    private void getData(){
        fb.collection("groups")
                .document(mParam1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Listen failed.");
                    return;
                } else if (!document.exists()) {
                    Log.w(TAG, "No such document");
                    return;
                }
                nombre.setText(document.getData().get("Nombre").toString());
                List<String> idArray = (List<String>) document.get("Usuarios");

                for (int i = 0; i < users.size(); i++) {
                    for (String id : idArray) {
                        Log.d("TAG", "Usuario: " + userMap.get(users.get(i)) + " id: " + id);
                        if (userMap.get(users.get(i)).equals(id)) {
                            Log.d("TAG", "Seleccionada: " + users.get(i));
                            list.setItemChecked(i, true);
                        }
                    }
                }
            }
        });
    }

    private void modificarGrupo(){
        if(nombre.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "No se han introducido nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        //Guardar nombre
        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", nombre.getText().toString());

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
                .document(mParam1).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Navigation.findNavController(root).navigate(R.id.action_modifyGroupsFragment_to_nav_group);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}