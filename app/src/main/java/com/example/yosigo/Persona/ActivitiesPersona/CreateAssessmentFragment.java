package com.example.yosigo.Persona.ActivitiesPersona;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAssessmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAssessmentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private static final int AUDIO_INTENT = 2;
    private static final int PHOTO_INTENT = 3;
    private static final int FILE_INTENT = 4;

    private final String TAG = "CREATE ASSESSMENT";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private View root;
    private EditText text_assessment;
    private ImageButton btn_difficult_high, btn_difficult_medium, btn_difficult_low,
            btn_utility_high, btn_utility_medium, btn_utility_low,
            btn_audio, btn_photo, btn_file, btn_send;

    private String mParam1;
    private int difficult = 0;
    private int utility = 0;
    private String tipo, ruta;
    private Uri uri;
    private MediaRecorder mRecorder;

    public CreateAssessmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CreateAssessmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAssessmentFragment newInstance(String param1, String param2) {
        CreateAssessmentFragment fragment = new CreateAssessmentFragment();
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
        root = inflater.inflate(R.layout.fragment_create_assessment, container, false);

        btn_send = root.findViewById(R.id.btn_send_assessment);
        text_assessment = root.findViewById(R.id.editTextTextMultiLine_assessment);
        tipo="";

        //Botones de valoración
        btn_difficult_high = root.findViewById(R.id.btn_assess_difficult_high);
        btn_difficult_medium = root.findViewById(R.id.btn_assess_difficult_medium);
        btn_difficult_low = root.findViewById(R.id.btn_assess_difficult_low);
        btn_utility_high = root.findViewById(R.id.btn_assess_utility_high);
        btn_utility_medium = root.findViewById(R.id.btn_assess_utility_medium);
        btn_utility_low = root.findViewById(R.id.btn_assess_utility_low);

        //Botones de sugerenvia
        btn_audio = root.findViewById(R.id.btn_assess_add_audio);
        btn_photo = root.findViewById(R.id.btn_assess_add_photo);
        btn_file = root.findViewById(R.id.btn_assess_add_file);

        //Enviar valoración
        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                agregarValoracion();
            }
        });

        //Listeners de valoración
        btn_difficult_high.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                difficult = 3;
                btn_difficult_high.setBackgroundColor(getResources().getColor(R.color.black));
                btn_difficult_medium.setBackgroundColor(getResources().getColor(R.color.white));
                btn_difficult_low.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btn_difficult_medium.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                difficult = 2;
                btn_difficult_high.setBackgroundColor(getResources().getColor(R.color.white));
                btn_difficult_medium.setBackgroundColor(getResources().getColor(R.color.black));
                btn_difficult_low.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btn_difficult_low.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                difficult = 2;
                btn_difficult_high.setBackgroundColor(getResources().getColor(R.color.white));
                btn_difficult_medium.setBackgroundColor(getResources().getColor(R.color.white));
                btn_difficult_low.setBackgroundColor(getResources().getColor(R.color.black));
            }
        });

        btn_utility_high.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                utility = 3;
                btn_utility_high.setBackgroundColor(getResources().getColor(R.color.black));
                btn_utility_medium.setBackgroundColor(getResources().getColor(R.color.white));
                btn_utility_low.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btn_utility_medium.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                utility = 2;
                btn_utility_high.setBackgroundColor(getResources().getColor(R.color.white));
                btn_utility_medium.setBackgroundColor(getResources().getColor(R.color.black));
                btn_utility_low.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        btn_utility_low.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                utility = 2;
                btn_utility_high.setBackgroundColor(getResources().getColor(R.color.white));
                btn_utility_medium.setBackgroundColor(getResources().getColor(R.color.white));
                btn_utility_low.setBackgroundColor(getResources().getColor(R.color.black));
            }
        });

        //Enviar sugerencia
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, PHOTO_INTENT);
            }
        });

        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                startActivityForResult(intent, FILE_INTENT);
            }
        });

        //El usuario debe dar permiso expreso de que quiere que se guarde el audio
        /*if (ContextCompat.checkSelfPermission(root.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO},
                    1000);

        }*/

        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recorder();
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) btn_audio.setEnabled(false);
    }

    public void Recorder(){
        Drawable imageButtonAudio;
        if( mRecorder == null){
            //uri = data.getData();
            ruta = getActivity().getExternalCacheDir().getAbsolutePath() + "Grabación.mp3";
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(ruta);
            mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

            try {
                mRecorder.prepare();
                mRecorder.start();
                imageButtonAudio = getResources().getDrawable(R.drawable.parar);
                btn_audio.setImageDrawable(imageButtonAudio);
            }catch (IOException e){
                Log.e(TAG, "Error al grabar audio", e);
            }
        } else {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            imageButtonAudio = getResources().getDrawable(R.drawable.microfono);
            btn_audio.setImageDrawable(imageButtonAudio);
            uri = Uri.fromFile(new File(ruta));
            tipo = "audio";
        }
    }

    private void agregarValoracion(){
        //Guardar datos
        Map<String, Object> data = new HashMap<>();
        data.put("Persona", MainActivity.sesion);
        data.put("Fecha", Timestamp.now());

        if(difficult != 0 && utility != 0) {
            data.put("Dificultad", difficult);
            data.put("Utilidad", utility);

            if(tipo != "") {
                data.put("Tipo", tipo);
                StorageReference filePath = storageRef.child("assessment_" + tipo).child(uri.getLastPathSegment());
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        data.put("Sugerencia", filePath.getPath());

                        fb.collection("activities")
                                .document(mParam1)
                                .collection("Assessment")
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
                });
            } else {
                if(!text_assessment.getText().toString().isEmpty()) {
                    data.put("Tipo", "text");
                    data.put("Sugerencia", text_assessment.getText().toString());
                }

                fb.collection("activities")
                        .document(mParam1)
                        .collection("Assessment")
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
        } else {
            //Si no hay datos introducidos
            Toast.makeText(getContext(), "No se ha introducido valoración", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_INTENT && resultCode == RESULT_OK ){
            uri = data.getData();
            tipo = "imagen";
        } else if(requestCode == FILE_INTENT && resultCode == RESULT_OK ){
            uri = data.getData();
            tipo = "archivo";
        }
    }
}