package com.example.yosigo.Persona.ActivitiesPersona;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFeedbackFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private View root;
    private static final int FEEDBACK_INTENT = 5;
    private final String TAG = "CREATE FEEDBACK";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String mParam1;
    private final List<String> id = new ArrayList<>();
    private Map<String, String> map_file = new HashMap<>();
    private Map<String, String> map_type = new HashMap<>();
    private Map<String, Date> map_date = new HashMap<>();
    private ImageButton btn_send_feedback, btn_save_feedback;
    private ListView list_previous_feedback;
    private EditText text_input;
    private Uri uri_feedback;

    public CreateFeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CreateFeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFeedbackFragment newInstance(String param1, String param2) {
        CreateFeedbackFragment fragment = new CreateFeedbackFragment();
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
        root = inflater.inflate(R.layout.fragment_create_feedback, container, false);

        btn_send_feedback = root.findViewById(R.id.upload_file_feedback);
        btn_save_feedback = root.findViewById(R.id.upload_feedback);
        text_input = root.findViewById(R.id.text_input_feedback);
        list_previous_feedback = root.findViewById(R.id.show_activity_feedback_persona);

        btn_save_feedback.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                agregarActividad();
            }
        });

        btn_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                startActivityForResult(intent, FEEDBACK_INTENT);
            }
        });

        getAnteriores();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FEEDBACK_INTENT && resultCode == RESULT_OK) {
            uri_feedback = data.getData();
        }
    }

    private void agregarActividad(){
        //Guardar datos
        Map<String, Object> data = new HashMap<>();
        data.put("Persona", MainActivity.sesion);
        data.put("Fecha", Timestamp.now());

        if(uri_feedback != null) {
            data.put("Tipo", "Archivo");
            Log.d(TAG, "Archivo: " + uri_feedback.getLastPathSegment());
            StorageReference filePath_picto = storageRef.child("feedback").child(uri_feedback.getLastPathSegment());

            filePath_picto.putFile(uri_feedback).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    data.put("Archivo", filePath_picto.getPath());
                    Log.d(TAG, "Agregado: " + filePath_picto.getPath());

                    //Agregar tarea
                    fb.collection("activities")
                            .document(mParam1)
                            .collection("Feedback")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
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
        } else if (!text_input.getText().toString().isEmpty()){
            data.put("Tipo", "Texto");
            data.put("Archivo", text_input.getText().toString());
            Log.d(TAG, "Agregado: " + text_input.getText().toString());

            //Agregar tarea
            FirebaseFirestore.getInstance().collection("activities")
                    .document(mParam1)
                    .collection("Feedback")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        } else {
            //Si no hay datos introducidos
            Toast.makeText(getContext(), "No se ha introducido nada", Toast.LENGTH_LONG).show();
            return;
        }

        //Volvemos a obtener las anteriores
        getAnteriores();
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