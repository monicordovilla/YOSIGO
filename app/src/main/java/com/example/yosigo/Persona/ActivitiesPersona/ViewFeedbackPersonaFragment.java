package com.example.yosigo.Persona.ActivitiesPersona;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewFeedbackPersonaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewFeedbackPersonaFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private final List<String> id = new ArrayList<>();
    private Map<String, String> map_file = new HashMap<>();
    private Map<String, String> map_type = new HashMap<>();
    private Map<String, Date> map_date = new HashMap<>();

    private String mParam1;
    private View root;
    private ListView list_previous_feedback;
    private ImageView imageView;

    public ViewFeedbackPersonaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewFeedbackPersonaFragment.
     */
    public static ViewFeedbackPersonaFragment newInstance(String param1) {
        ViewFeedbackPersonaFragment fragment = new ViewFeedbackPersonaFragment();
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
        root = inflater.inflate(R.layout.fragment_view_feedback_persona, container, false);
        list_previous_feedback = root.findViewById(R.id.show_activity_feedback_persona);

        //Mostrar icono de actividad
        imageView = root.findViewById(R.id.image_activity_previous_feedback);
        getImage();

        //Volvemos a obtener las anteriores
        getAnteriores();

        return root;
    }

    private void getImage(){
        fb.collection("activities")
                .document(mParam1).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(document.getData().get("Pictograma") != null) {
                                    storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(root)
                                                    .load(uri)
                                                    .into(imageView);
                                        }
                                    });
                                } else {
                                    imageView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }

    private void getAnteriores(){
        //Limpiamos datos
        id.clear();
        map_date.clear();
        map_type.clear();
        map_file.clear();

        //Obtenemos datos de base de datos
        fb.collection("activities")
                .document(mParam1)
                .collection("Feedback")
                .whereEqualTo("Persona", MainActivity.sesion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                String doc_id = document.getId();
                                id.add(doc_id);
                                map_date.put(doc_id , document.getDate("Fecha", behavior));
                                map_type.put(doc_id , document.getData().get("Tipo").toString());
                                map_file.put(doc_id , document.getData().get("Archivo").toString());
                            }
                            FeedbackAdapter adapter = new FeedbackAdapter(
                                    root.getContext(),
                                    id,
                                    map_date,
                                    map_file,
                                    map_type
                            );
                            list_previous_feedback.setAdapter(adapter);
                        }
                    }
                });
    }
}