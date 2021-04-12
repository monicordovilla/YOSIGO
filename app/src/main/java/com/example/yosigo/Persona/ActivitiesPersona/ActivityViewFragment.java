package com.example.yosigo.Persona.ActivitiesPersona;

import androidx.lifecycle.ViewModelProvider;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityViewFragment} factory method to
 * create an instance of this fragment.
 */
public class ActivityViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "VER ACTIVIDAD" ;

    private String mParam1;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private View root;

    private TextView text_name;
    private ImageView img_picto, img_meta, img_cat;
    private ListView list_actividades;
    private ImageButton btn_feedback, btn_email;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_view_fragment, container, false);

        text_name = (TextView) root.findViewById(R.id.name_activity_persona);
        img_picto = (ImageView) root.findViewById(R.id.picto_activity_persona);
        img_meta = (ImageView) root.findViewById(R.id.show_picto_meta_persona);
        list_actividades = root.findViewById(R.id.list_array_activities_persona);
        btn_feedback = root.findViewById(R.id.btn_feedback_persona);

        btn_feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_activityViewFragment2_to_createFeedbackFragment, bundle);
            }
        });

        getDatosTarea();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    private void getDatosTarea(){
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

                        if(document.getData().get("Tarea") != null) {
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