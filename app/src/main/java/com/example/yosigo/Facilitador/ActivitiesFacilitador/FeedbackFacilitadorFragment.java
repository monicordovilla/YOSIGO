package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.MainActivity;
import com.example.yosigo.Persona.ActivitiesPersona.FeedbackAdapter;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFacilitadorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFacilitadorFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private final String TAG = "FEEDBACK";
    private final FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String mParam1;
    private View root;
    private ListView list;
    private TextView textView;
    private List<String> id = new ArrayList<>();
    private Map<String, String> map_file = new HashMap<>();
    private Map<String, String> map_type = new HashMap<>();
    private Map<String, Date> map_date = new HashMap<>();
    private Map<String, String> map_user = new HashMap<>();

    public FeedbackFacilitadorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedbackFacilitadorFragment.
     */
    public static FeedbackFacilitadorFragment newInstance(String param1, String param2) {
        FeedbackFacilitadorFragment fragment = new FeedbackFacilitadorFragment();
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
        root = inflater.inflate(R.layout.fragment_feedback_facilitador, container, false);
        list = root.findViewById(R.id.list_feedback_facilitador);
        textView = root.findViewById(R.id.name_activity_feedback);

        getFeedbacks();
        getName();

        return root;
    }

    private void getName(){
        fb.collection("activities")
                .document(mParam1).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                textView.setText(document.getData().get("Nombre").toString());
                            }
                        }
                    }
                });
    }

    private void getFeedbacks(){
        id = new ArrayList<>();
        map_file.clear();
        map_type.clear();
        map_date.clear();
        map_user.clear();

        fb.collection("activities")
                .document(mParam1).collection("Feedback")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Obtener id
                                String doc_id = document.getId();
                                id.add(doc_id);

                                //Obtener usuario
                                fb.collection("users")
                                        .document(document.getData().get("Persona").toString()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task_user) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot doc_user = task_user.getResult();
                                                    String full_name = doc_user.getData().get("Nombre") + " " +
                                                            doc_user.getData().get("Apellidos") + " (" +
                                                            doc_user.getData().get("Apodo") + ")";

                                                    //Guardar datos
                                                    map_user.put(doc_id , full_name);
                                                }
                                                Log.d(TAG, doc_id);
                                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                map_date.put(doc_id , document.getDate("Fecha", behavior));
                                                map_type.put(doc_id , document.getData().get("Tipo").toString());
                                                map_file.put(doc_id , document.getData().get("Archivo").toString());

                                                FeedbackFacilitadorAdapter adapter = new FeedbackFacilitadorAdapter(
                                                        root.getContext(),
                                                        id,
                                                        map_date,
                                                        map_file,
                                                        map_type,
                                                        map_user
                                                );
                                                list.setAdapter(adapter);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}