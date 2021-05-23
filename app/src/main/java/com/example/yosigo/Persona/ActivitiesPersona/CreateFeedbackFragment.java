package com.example.yosigo.Persona.ActivitiesPersona;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
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
import android.widget.VideoView;

import com.bumptech.glide.Glide;
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

    private static final String ARG_PARAM1 = "param1";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    private View root;
    private static final int FEEDBACK_INTENT = 5;
    private final String TAG = "CREATE FEEDBACK";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String mParam1;
    private ImageButton btn_send_feedback, btn_save_feedback, btn_previous, btn_photo, btn_video, btn_preview;
    private ImageView imageView, imagePreview;
    private EditText text_input;
    private Uri uri_feedback;
    private String type = "texto";

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

        //Guardar datos
        btn_send_feedback = root.findViewById(R.id.upload_file_feedback);
        btn_save_feedback = root.findViewById(R.id.upload_feedback);
        btn_previous = root.findViewById(R.id.button_go_previous_feedback);
        btn_photo = root.findViewById(R.id.upload_photo);
        btn_video = root.findViewById(R.id.upload_video);
        text_input = root.findViewById(R.id.text_input_feedback);

        //Previsualizar datos
        btn_preview = root.findViewById(R.id.file_feedback_preview);
        imagePreview = root.findViewById(R.id.image_feedback_preview);

        //Mostrar icono de actividad
        imageView = root.findViewById(R.id.image_activity_feedback);
        getImage();

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

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_createFeedbackFragment_to_viewFeedbackPersonaFragment, bundle);
            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FEEDBACK_INTENT && resultCode == RESULT_OK) {
            uri_feedback = data.getData();
            type = "archivo";
            btn_preview.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri_feedback);
                    getActivity().startActivity(launchBrowser);
                }
            });

            btn_preview.setVisibility(View.VISIBLE);
            imagePreview.setVisibility(View.GONE);
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            uri_feedback = data.getData();
            type = "video";
            btn_preview.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri_feedback);
                    getActivity().startActivity(launchBrowser);
                }
            });

            btn_preview.setVisibility(View.VISIBLE);
            imagePreview.setVisibility(View.GONE);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);
            type = "imagen";
            uri_feedback = data.getData();

            btn_preview.setVisibility(View.GONE);
            imagePreview.setVisibility(View.VISIBLE);
        }
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

                    //Agregar feedback
                    fb.collection("activities")
                            .document(mParam1)
                            .collection("Feedback")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    Bundle bundle = new Bundle();
                                    bundle.putString("param1", mParam1);
                                    Navigation.findNavController(root).navigate(R.id.action_createFeedbackFragment_to_activityViewFragment2, bundle);
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

            //Agregar feedback
            FirebaseFirestore.getInstance().collection("activities")
                    .document(mParam1)
                    .collection("Feedback")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            Bundle bundle = new Bundle();
                            bundle.putString("param1", mParam1);
                            Navigation.findNavController(root).navigate(R.id.action_createFeedbackFragment_to_activityViewFragment2, bundle);
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
    }


}