package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityViewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "VER ACTIVIDAD" ;
    private PersonasViewModel mViewModel;

    private String mParam1;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private View root;

    private TextView text_name;
    private ImageView img_picto, img_meta, img_cat;
    private ListView list_actividades, list_usurarios;
    private List<String> participantes = new ArrayList<>();

    public ActivityViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ActivityViewFragment.
     */
    public static ActivityViewFragment newInstance(String param1) {
        ActivityViewFragment fragment = new ActivityViewFragment();
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
        root = inflater.inflate(R.layout.fragment_activity_view, container, false);
        mViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);

        text_name = (TextView) root.findViewById(R.id.show_activity_name);
        img_picto = (ImageView) root.findViewById(R.id.show_picto_activity);
        img_meta = (ImageView) root.findViewById(R.id.show_meta_pic);
        img_cat = (ImageView) root.findViewById(R.id.show_cat_pic);
        list_actividades = root.findViewById(R.id.show_activity_pic);
        list_usurarios = root.findViewById(R.id.show_activity_name_users);

        getDatos();
        getParticipantes();

        return root;
    }

    private void getParticipantes(){
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("Rol", "Persona")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String full_name = document.getData().get("Nombre") + " " +
                                        document.getData().get("Apellidos") + " (" +
                                        document.getData().get("Apodo") + ")";

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(document.getId())
                                        .collection("activities")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> act) {
                                                if (act.isSuccessful()) {
                                                    for (QueryDocumentSnapshot doc : act.getResult()) {
                                                        if(mParam1.equals(doc.getId())) {
                                                            /*Marca de firebase
                                                            * url problema: https://stackoverflow.com/questions/47771044/firestore-timestamp-getting-null
                                                            * */
                                                            DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                            Date fecha_inicio = doc.getDate("Fecha Inicio", behavior) ;
                                                            Date fecha_fin = doc.getDate("Fecha Fin", behavior) ;

                                                            Log.d(TAG, "Fecha inicio: " + fecha_inicio + " ¿posterior? " + new Date().after(fecha_inicio));
                                                            Log.d(TAG, "Fecha fin: " + fecha_fin + " ¿anterior? " + new Date().before(fecha_fin));

                                                            if (new Date().after(fecha_inicio) && new Date().before(fecha_fin)) {
                                                                participantes.add(full_name);
                                                                Log.d(TAG, "Participante: " + full_name);
                                                            }
                                                        }
                                                    }

                                                    ArrayAdapter<String> adapter = new ArrayAdapter(
                                                            root.getContext(),
                                                            android.R.layout.simple_list_item_1,
                                                            participantes
                                                    );
                                                    list_usurarios.setAdapter(adapter);
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", act.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getDatos(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("activities").document(mParam1);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        text_name.setText(document.getData().get("Nombre").toString());

                        // Create a reference to a file from a Google Cloud Storage URI
                        if(document.getData().get("Pictograma") != null) {
                            storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(root)
                                            .load(uri)
                                            .into(img_picto);
                                }
                            });
                        }

                        if(document.getData().get("Categoria") != null) {
                            storageRef.child((String) document.getData().get("Categoria")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(root)
                                            .load(uri)
                                            .into(img_cat);
                                }
                            });
                        }

                        if(document.getData().get("Meta") != null) {
                            storageRef.child((String) document.getData().get("Meta")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(root)
                                            .load(uri)
                                            .into(img_meta);
                                }
                            });
                        }

                        if(document.getData().get("Meta") != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    root.getContext(),
                                    android.R.layout.simple_list_item_1,
                                    (List<String>) document.getData().get("Tarea")
                            );
                            list_actividades.setAdapter(adapter);
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
                            });
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
}