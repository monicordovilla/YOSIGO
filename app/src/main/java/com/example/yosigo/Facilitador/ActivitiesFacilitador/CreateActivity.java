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
import android.widget.Toast;

import com.example.yosigo.Facilitador.Category.CategoryListViewModel;
import com.example.yosigo.Facilitador.Goals.GoalsListViewModel;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CreateActivity extends Fragment {

    private final String TAG = "Crear actividad";
    private CategoryListViewModel categoryListViewModel;
    private GoalsListViewModel goalsListViewModel;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private View root;
    private String picto, meta, categoría;
    private EditText nombre_actividad;
    private Button btn_picto, btn_actividad, btn_create;
    private Spinner spinner_categoria, spinner_meta;
    private ImageView preview_picto;
    private ListView list;

    private static final int PICTO_INTENT = 1;
    private static final int ACT_INTENT = 2;
    private Uri uri_picto;
    private ArrayList<Uri> uri_activities = new ArrayList<>();
    private ArrayList<String> activities = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> goals = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        categoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        goalsListViewModel = new ViewModelProvider(this).get(GoalsListViewModel.class);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_create_activity, container, false);
        Context context = this.getContext();

        //Obtenemos referencia del Edit Text
        nombre_actividad = (EditText) root.findViewById(R.id.input_name_activity);

        //Obtenemos la referencia a los botones
        btn_picto = (Button) root.findViewById(R.id.btn_up_picto);
        btn_actividad = (Button) root.findViewById(R.id.btn_up_actividad);
        btn_create = (Button) root.findViewById(R.id.btn_create_activity);

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
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_categoria.setAdapter(adapter);
            }
        });

        goalsListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                goals = strings;
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, goals);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_meta.setAdapter(adapter);
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

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarActividad();
            }
        });

        return root;
    }

    private void agregarActividad(){
        //Guardar nombre
        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", nombre_actividad.getText().toString());

        String sesion = MainActivity.sesion;
        data.put("Facilitador", sesion);

        //Guardar fotos en firestore storage
        if (uri_picto == null) {
            Toast.makeText(getContext(), "No se ha introducido pictograma descriptivo", Toast.LENGTH_LONG).show();
            return;
        }

        StorageReference filePath_picto = storageRef.child("pictogramas").child(uri_picto.getLastPathSegment());

        filePath_picto.putFile(uri_picto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                picto = filePath_picto.getPath();
                data.put("Pictograma", picto);
                Log.d(TAG, "Agregado: " + picto);

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

                //Agregar tarea
                data.put("Tarea", activities);
                FirebaseFirestore.getInstance().collection("activities")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Toast.makeText(getContext(), "Se creado la actividad con exito", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(root).navigate(R.id.action_createActivity_to_nav_activity);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "No se ha podido subir", e);
            }
        });
    }

    private void setList(List<String> Tarea){
        MaterialAdapter adapter = new MaterialAdapter(
                root.getContext(),
                null,
                Tarea
        );
        list.setAdapter(adapter);
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

                    setList(activities);
                }
            });
        }
    }

}