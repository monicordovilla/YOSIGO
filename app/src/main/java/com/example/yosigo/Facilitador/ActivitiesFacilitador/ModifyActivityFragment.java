package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yosigo.Facilitador.Category.CategoryListViewModel;
import com.example.yosigo.Facilitador.Goals.GoalsListViewModel;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModifyActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyActivityFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "MODIFICAR ACTIVIDAD";
    private final FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final int PICTO_INTENT = 1;
    private static final int ACT_INTENT = 2;

    private CategoryListViewModel categoryListViewModel;
    private GoalsListViewModel goalsListViewModel;
    private String mParam1;
    private String picto_antigua, meta, categoría;
    private Uri uri_picto;
    private Integer index=0;
    private List<String> categories = new ArrayList<>();
    private List<String> goals = new ArrayList<>();
    private ArrayList<String> activities = new ArrayList<>();

    private View root;
    private Context context;
    private Spinner spinner_categoria, spinner_meta;
    private ArrayAdapter adapter_categoria, adapter_meta;
    private EditText nombre_actividad;
    private ImageView preview_picto;
    private ListView list;
    private Button btn_picto, btn_actividad, btn_modify;

    public ModifyActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ModifyActivityFragment.
     */
    public static ModifyActivityFragment newInstance(String param1) {
        ModifyActivityFragment fragment = new ModifyActivityFragment();
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
            categoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
            goalsListViewModel = new ViewModelProvider(this).get(GoalsListViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_modify_activity, container, false);
        context = this.getContext();

        //Obtenemos referencia del Edit Text
        nombre_actividad = (EditText) root.findViewById(R.id.input_name_activity);

        //Obtenemos la referencia a los botones
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto);
        btn_actividad = (Button) root.findViewById(R.id.btn_up_actividad);
        btn_modify = (Button) root.findViewById(R.id.btn_create_activity);

        //Obtenemos referencia de los spinners
        spinner_meta = root.findViewById(R.id.spinner_up_meta);
        spinner_categoria = root.findViewById(R.id.spinner_up_category);

        //Obtenemos referencia de las previsualizacione
        preview_picto = root.findViewById(R.id.activity_preview_picto);
        list = root.findViewById(R.id.material_list);

        categoryListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                categories = strings;
            }
        });

        goalsListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                goals = strings;
                adapter_meta = new ArrayAdapter(context, android.R.layout.simple_spinner_item, goals);
                adapter_meta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_meta.setAdapter(adapter_meta);
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarActividad();
            }
        });

        btn_picto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, PICTO_INTENT);
            }
        });

        btn_actividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                startActivityForResult(intent, ACT_INTENT);
            }
        });

        getDatos();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActualizarLista();
    }

    public void ActualizarLista(){
        final DocumentReference docRef = fb.collection("activities").document(mParam1);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(snapshot.getData().get("Tarea") != null) {
                        setList((List<String>) snapshot.getData().get("Tarea"));
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void getDatos(){
        fb.collection("activities")
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
                nombre_actividad.setText(document.getData().get("Nombre").toString());

                if(document.getData().get("Pictograma") != null) {
                    picto_antigua = (String) document.getData().get("Pictograma");
                    storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(root)
                                    .load(uri)
                                    .into(preview_picto);
                        }
                    });
                }

                if(document.getData().get("Categoria") != null) {
                    categoryListViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                        @Override
                        public void onChanged(Map<String, String> categoriesMap) {
                            for(String value: categoriesMap.values()){
                                if ( value.equals(document.getData().get("Categoria")) ){
                                    for(String key: categoriesMap.keySet()){
                                        if(categoriesMap.get(key).equals(value)){
                                            categoryListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                                                @Override
                                                public void onChanged(List<String> strings) {
                                                    categories = strings;
                                                    adapter_categoria = new ArrayAdapter(context, android.R.layout.simple_spinner_item, categories);
                                                    adapter_categoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    spinner_categoria.setAdapter(adapter_categoria);
                                                    spinner_categoria.setSelection(categories.indexOf(key));
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    });
                }

                if(document.getData().get("Meta") != null) {
                    goalsListViewModel.getGoals().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
                        @Override
                        public void onChanged(Map<String, String> goalsMap) {
                            for(String value: goalsMap.values()){
                                if ( value.equals(document.getData().get("Meta")) ){
                                    for(String key: goalsMap.keySet()){
                                        if(goalsMap.get(key).equals(value)){
                                            goalsListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                                                @Override
                                                public void onChanged(List<String> strings) {
                                                    goals = strings;
                                                    adapter_meta = new ArrayAdapter(context, android.R.layout.simple_spinner_item, goals);
                                                    adapter_meta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    spinner_meta.setAdapter(adapter_meta);
                                                    spinner_meta.setSelection(goals.indexOf(key));
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    });
                }

                if(document.getData().get("Tarea") != null) {
                    setList((List<String>) document.getData().get("Tarea"));
                }
            }
        });
    }

    private void setList(List<String> Tarea){
        MaterialAdapter adapter = new MaterialAdapter(
                root.getContext(),
                mParam1,
                Tarea
        );
        list.setAdapter(adapter);
    }

    private void modificarActividad(){
        //Guardar nombre
        Map<String, Object> data = new HashMap<>();

        //Guardar fotos en firestore storage
        if (uri_picto != null) {
            if(picto_antigua != null ) {
                storageRef.child(picto_antigua).delete();
            }
            StorageReference filePath_picto = storageRef.child("pictogramas").child(uri_picto.getLastPathSegment());
            filePath_picto.putFile(uri_picto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    data.put("Pictograma", filePath_picto.getPath());
                    setData(data);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "No se ha podido subir", e);
                }
            });
        } else {
            setData(data);
        }
    }

    private void setData(Map<String, Object> data){
        data.put("Nombre", nombre_actividad.getText().toString());

        //Agregar meta
        goalsListViewModel.getGoals().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> categoriesMap) {
                meta = categoriesMap.get(spinner_meta.getSelectedItem());
                data.put("Meta", meta);
            }
        });

        //Agregar categoría
        categoryListViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> categoriesMap) {
                categoría = categoriesMap.get(spinner_categoria.getSelectedItem());
                data.put("Categoria", categoría);
            }
        });

        //Actualizar
        FirebaseFirestore.getInstance().collection("activities")
                .document(mParam1).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Bundle bundle = new Bundle();
                        bundle.putString("param1", mParam1);
                        Navigation.findNavController(root).navigate(R.id.action_modifyActivityFragment_to_activityViewFragment, bundle);
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
            Glide.with(root)
                    .load(uri_picto)
                    .into(preview_picto);
        } else if(requestCode == ACT_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("actividades").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String activityPath = filePath.getPath();
                    Log.d(TAG, "Subido: " + activityPath);
                    activities.add(activityPath);

                    fb.collection("activities").document(mParam1)
                            .update("Tarea", FieldValue.arrayUnion(activityPath));
                }
            });
        }
    }
}