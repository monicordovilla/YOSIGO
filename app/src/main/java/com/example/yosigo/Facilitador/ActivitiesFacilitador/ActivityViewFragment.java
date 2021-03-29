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
import android.webkit.WebView;
import android.widget.Adapter;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityViewFragment#newInstance} factory method to
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
    private ListView list;

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
            Log.d(TAG, "Recibido id: " + mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_activity_view, container, false);

        text_name = (TextView) root.findViewById(R.id.show_activity_name);
        img_picto = (ImageView) root.findViewById(R.id.show_picto_activity);
        img_meta = (ImageView) root.findViewById(R.id.show_meta_pic);
        img_cat = (ImageView) root.findViewById(R.id.show_cat_pic);
        list = root.findViewById(R.id.show_activity_pic);

        getDatos();

        return root;
    }

    private void getDatos(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("activities").document(mParam1);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        text_name.setText(document.getData().get("Nombre").toString());

                        // Create a reference to a file from a Google Cloud Storage URI
                        storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "Mostando: " + document.getData().get("Pictograma").toString());
                                Glide.with(root)
                                        .load(uri)
                                        .into(img_picto);
                            }
                        });

                        storageRef.child((String) document.getData().get("Categoria")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "Mostando: " + document.getData().get("Categoria").toString());
                                Glide.with(root)
                                        .load(uri)
                                        .into(img_cat);
                            }
                        });

                        storageRef.child((String) document.getData().get("Meta")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "Mostando: " + document.getData().get("Meta").toString());
                                Glide.with(root)
                                        .load(uri)
                                        .into(img_meta);
                            }
                        });

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                root.getContext(),
                                android.R.layout.simple_list_item_1,
                                (List<String>) document.getData().get("Tarea")
                        );
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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