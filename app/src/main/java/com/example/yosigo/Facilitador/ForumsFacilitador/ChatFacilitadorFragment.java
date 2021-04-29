package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.MainActivity;
import com.example.yosigo.MessageAdapter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFacilitadorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFacilitadorFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CHAT";
    private static final int FOTO_INTENT = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private String mParam1, mParam2;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private View root;
    private TextView text_name;
    private EditText mensaje;
    private ImageButton btn_audio, btn_foto, btn_send;
    private MediaRecorder mRecorder;
    private String tipo, ruta;
    private Uri uri_audio, uri_photo;
    private RecyclerView list;

    public ChatFacilitadorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFacilitadorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFacilitadorFragment newInstance(String param1, String param2) {
        ChatFacilitadorFragment fragment = new ChatFacilitadorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat_facilitador, container, false);
        text_name = (TextView) root.findViewById(R.id.textView_chat_facilitador);
        mensaje = root.findViewById(R.id.EditText_chat_facilitador);
        list = root.findViewById(R.id.list_chat_persona);

        //Buttons
        btn_audio = root.findViewById(R.id.btn_enviar_audio_facilitador);
        btn_foto = root.findViewById(R.id.btn_enviar_foto_facilitador);
        btn_send = root.findViewById(R.id.btn_enviar_mensaje_facilitador);

        getNombre();
        getMensajes();

        return root;
    }

    private void getMensajes(){
        fb.collection("activities")
                .document(mParam1)
                .collection("Chat")
                .document(MainActivity.sesion)
                .collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> idList = new ArrayList<>();
                            Map<String, Date> dateMap = new HashMap<>();
                            Map<String, String> emisorMap = new HashMap<>();
                            Map<String, String> emisorId = new HashMap<>();
                            Map<String, String> tipoMap = new HashMap<>();
                            Map<String, String> contenidoMap = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                //Obtener usuario
                                fb.collection("users")
                                        .document(document.getData().get("Emisor").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task_user) {
                                                if (task_user.isSuccessful()){
                                                    DocumentSnapshot doc_user = task_user.getResult();
                                                    String full_name = doc_user.getData().get("Nombre") + " " +
                                                            doc_user.getData().get("Apellidos") + " (" +
                                                            doc_user.getData().get("Apodo") + ")";

                                                    //Guardar datos
                                                    idList.add(document.getId());
                                                    DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                    dateMap.put(document.getId(), document.getDate("Fecha", behavior));
                                                    emisorMap.put(document.getId(), full_name);
                                                    emisorId.put(document.getId(), document.getData().get("Emisor").toString());
                                                    tipoMap.put(document.getId(), document.getData().get("Tipo").toString());
                                                    contenidoMap.put(document.getId(), document.getData().get("Contenido").toString());
                                                }
                                            }
                                        });
                            }

                            MessageAdapter adapter = new MessageAdapter(
                                    root.getContext(),
                                    idList,
                                    dateMap,
                                    emisorId,
                                    emisorMap,
                                    tipoMap,
                                    contenidoMap
                            );
                            LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            list.setLayoutManager(llm);
                            list.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void getNombre(){
        fb.collection("activities")
                .document(mParam1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                text_name.setText(document.getData().get("Nombre").toString());
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enviar_mensaje_facilitador:
                tipo = "Texto";
                Map<String, Object> data = new HashMap<>();
                data.put("Fecha", Timestamp.now());
                data.put("Emisor", MainActivity.sesion);
                data.put("Tipo", tipo);
                data.put("Contenido", mensaje.getText().toString());
                sendData(data);
                mensaje.setText("");
                break;

            case R.id.btn_enviar_foto_facilitador:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, FOTO_INTENT);
                break;

            case R.id.btn_enviar_audio_facilitador:
                Recorder();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FOTO_INTENT && resultCode == RESULT_OK ){
            uri_photo = data.getData();
            tipo = "Imagen";
            SavePhoto();
        }
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
            uri_audio = Uri.fromFile(new File(ruta));
            tipo = "Audio";
            SaveAudio();
        }
    }

    private void SaveAudio(){
        StorageReference filePath = storageRef.child("chat_audio" + tipo).child(uri_audio.getLastPathSegment());
        filePath.putFile(uri_audio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Map<String, Object> data = new HashMap<>();
                data.put("Fecha", Timestamp.now());
                data.put("Emisor", MainActivity.sesion);
                data.put("Tipo", tipo);
                data.put("Contenido", filePath.getPath());
                sendData(data);
                uri_audio=null;
            }
        });
    }

    private void SavePhoto(){
        StorageReference filePath = storageRef.child("chat_foto" + tipo).child(uri_photo.getLastPathSegment());
        filePath.putFile(uri_photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Map<String, Object> data = new HashMap<>();
                data.put("Fecha", Timestamp.now());
                data.put("Emisor", MainActivity.sesion);
                data.put("Tipo", tipo);
                data.put("Contenido", filePath.getPath());
                sendData(data);
                uri_photo=null;
            }
        });
    }

    private void sendData(Map<String, Object> data){
        fb.collection("activities")
                .document(mParam1)
                .collection("Chat")
                .document(MainActivity.sesion)
                .collection("Messages")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()); }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}