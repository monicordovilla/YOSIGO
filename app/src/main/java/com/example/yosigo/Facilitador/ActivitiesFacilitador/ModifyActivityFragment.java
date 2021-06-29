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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.Facilitador.Category.CategoryListViewModel;
import com.example.yosigo.Facilitador.Goals.GoalsListViewModel;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
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
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final int PICTO_INTENT = 1;
    private static final int ACT_INTENT = 2;

    private CategoryListViewModel categoryListViewModel;
    private GoalsListViewModel goalsListViewModel;
    private String mParam1;
    private String picto, meta, categoría;
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

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDatos();
    }

    private void getDatos(){
        fb.collection("activities").document(mParam1)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nombre_actividad.setText(document.getData().get("Nombre").toString());

                        // Create a reference to a file from a Google Cloud Storage URI
                        if(document.getData().get("Pictograma") != null) {
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
                                        Log.d(TAG, "id: " + value + " data: " + document.getData().get("Categoria")
                                        + " iguales? "+ value.equals(document.getData().get("Categoria")));
                                        if ( value.equals(document.getData().get("Categoria")) ){
                                            for(String key: categoriesMap.keySet()){
                                                if(categoriesMap.get(key).equals(value)){
                                                    categoryListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                                                        @Override
                                                        public void onChanged(List<String> strings) {
                                                            categories = strings;
                                                            int index_cat = categories.indexOf(key);
                                                            Log.d(TAG, "index key: " + categories.indexOf(key));
                                                            Log.d(TAG, "key: " + key +
                                                                    "\n value: " + value +
                                                                    "\n MapKey: " + categoriesMap.get(key) +
                                                                    "\n categories: " + categories);
                                                            Log.d(TAG, "index: " + index_cat);

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
                                        Log.d(TAG, "id: " + value + " data: " + document.getData().get("Meta")
                                                + " iguales? "+ value.equals(document.getData().get("Meta")));
                                        if ( value.equals(document.getData().get("Meta")) ){
                                            for(String key: goalsMap.keySet()){
                                                if(goalsMap.get(key).equals(value)){
                                                    goalsListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                                                        @Override
                                                        public void onChanged(List<String> strings) {
                                                            goals = strings;
                                                            int index_cat = goals.indexOf(key);
                                                            Log.d(TAG, "index key: " + goals.indexOf(key));
                                                            Log.d(TAG, "key: " + key +
                                                                    "\n value: " + value +
                                                                    "\n MapKey: " + goalsMap.get(key) +
                                                                    "\n goals: " + goals);
                                                            Log.d(TAG, "index: " + index_cat);

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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    root.getContext(),
                                    android.R.layout.simple_list_item_1,
                                    (List<String>) document.getData().get("Tarea")
                            );
                            /*list_actividades.setAdapter(adapter);
                            list_actividades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                ArrayList<String> tareas = (ArrayList<String>) document.getData().get("Tarea");

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    String tarea = tareas.get(position);
                                    storageRef.child((String) tarea).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(launchBrowser);
                                        }
                                    });
                                }
                            });*/
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTO_INTENT && resultCode == RESULT_OK ){
            uri_picto = data.getData();
            preview_picto.setImageURI(uri_picto);
            preview_picto.setVisibility(View.VISIBLE);
        } else if(requestCode == ACT_INTENT && resultCode == RESULT_OK ){
            Uri uri = data.getData();
            StorageReference filePath = storageRef.child("actividades").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String activityPath = filePath.getPath();
                    Log.d(TAG, "Subido: " + activityPath);
                    activities.add(activityPath);
                }
            });
        }
    }
}