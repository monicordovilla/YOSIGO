package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
 * A fragment representing a list of Items.
 */
public class ViewAssessmentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private final String TAG = "FEEDBACK";
    private final FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String mParam1;
    private View root;
    private ListView list;
    private TextView textView;
    private EditText searchBar;

    private List<String> id = new ArrayList<>();
    private Map<String, String> map_file = new HashMap<>();
    private Map<String, String> map_type = new HashMap<>();
    private Map<String, Date> map_date = new HashMap<>();
    private Map<String, String> map_user = new HashMap<>();
    private Map<String, Integer> map_difficult = new HashMap<>();
    private Map<String, Integer> map_utility = new HashMap<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewAssessmentFragment() {
    }

    public static ViewAssessmentFragment newInstance(String param1, String param2) {
        ViewAssessmentFragment fragment = new ViewAssessmentFragment();
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
        root = inflater.inflate(R.layout.fragment_view_assessment_list, container, false);
        list = root.findViewById(R.id.list_assessment_facilitador);
        textView = root.findViewById(R.id.name_activity_assessment);
        searchBar = root.findViewById(R.id.search_assessment_name);

        getName();
        getAssessments();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                filterNames(newText);
            }
        });

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

    private void getAssessments(){
        id = new ArrayList<>();
        map_file.clear();
        map_type.clear();
        map_date.clear();
        map_user.clear();

        fb.collection("activities")
                .document(mParam1).collection("Assessment")
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
                                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                map_date.put(doc_id , document.getDate("Fecha", behavior));
                                                map_difficult.put(doc_id, Integer.parseInt(document.getData().get("Dificultad").toString()));
                                                map_utility.put(doc_id, Integer.parseInt(document.getData().get("Utilidad").toString()));
                                                if (document.getData().get("Tipo") == null){
                                                    map_type.put(doc_id, "");
                                                    map_file.put(doc_id, "");
                                                } else {
                                                    map_type.put(doc_id, document.getData().get("Tipo").toString());
                                                    map_file.put(doc_id, document.getData().get("Sugerencia").toString());
                                                }

                                                AssessmentViewItemAdapter adapter = new AssessmentViewItemAdapter(
                                                        root.getContext(),
                                                        id,
                                                        map_date,
                                                        map_difficult,
                                                        map_utility,
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

    private void filterNames(String text) {
        if(text.isEmpty()){
            AssessmentViewItemAdapter adapter = new AssessmentViewItemAdapter(
                    root.getContext(),
                    id,
                    map_date,
                    map_difficult,
                    map_utility,
                    map_file,
                    map_type,
                    map_user
            );
            list.setAdapter(adapter);
        } else {
            List<String> FilterId = new ArrayList<>();
            Map<String, String> FilterFileMap = new HashMap<>();
            Map<String, String> FilterTypeMap = new HashMap<>();
            Map<String, Date> FilterDateMap = new HashMap<>();
            Map<String, String> FilterUserMap = new HashMap<>();
            Map<String, Integer> FilterDifficultMap = new HashMap<>();
            Map<String, Integer> FilterUtilityMap = new HashMap<>();

            for (String key : id) {
                String name = map_user.get(key);

                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    FilterId.add(key);
                    FilterFileMap.put(key, map_file.get(key));
                    FilterTypeMap.put(key, map_type.get(key));
                    FilterDateMap.put(key, map_date.get(key));
                    FilterUserMap.put(key, name);
                    FilterDifficultMap.put(key, map_difficult.get(key));
                    FilterUtilityMap.put(key, map_utility.get(key));
                }
            }
            AssessmentViewItemAdapter adapter = new AssessmentViewItemAdapter(
                    root.getContext(),
                    FilterId,
                    FilterDateMap,
                    FilterDifficultMap,
                    FilterUtilityMap,
                    FilterFileMap,
                    FilterTypeMap,
                    FilterUserMap
            );
            list.setAdapter(adapter);
        }
    }
}