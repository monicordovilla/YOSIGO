package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.MainActivity;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AsociateForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AsociateForumFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ASOCIAR FORO" ;
    private PersonasViewModel mViewModel;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String mParam1;
    private List<String> users = new ArrayList<>();
    private Map<String, String> userMap = new HashMap<>();
    private View root;
    private ListView list;
    private Button btn_asociate;

    public AsociateForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AsociateForumFragment.
     */
    public static AsociateForumFragment newInstance(String param1) {
        AsociateForumFragment fragment = new AsociateForumFragment();
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
        //Obtener personas asociadas al facilitador
        mViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_asociate_forum, container, false);

        //Obtener elementos del layout
        list = root.findViewById(R.id.list_asociate_forum);
        btn_asociate = root.findViewById(R.id.btn_asociate_forum);

        btn_asociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAsociados();
            }
        });

        getAsociados();

        return root;
    }

    private void getAsociados(){
        fb.collection("forums")
                .document(mParam1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task_facilitador) {
                        if (task_facilitador.isSuccessful()) {
                            DocumentSnapshot document_facilitador = task_facilitador.getResult();
                            if (document_facilitador.exists()) {
                                List<String> idArray = (List<String>) document_facilitador.get("Personas");

                                mViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
                                    @Override
                                    public void onChanged(List<String> strings) {
                                        users = strings;
                                        list.setAdapter(new ArrayAdapter<String>(
                                                root.getContext(),
                                                android.R.layout.simple_list_item_multiple_choice,
                                                users
                                                )
                                        );
                                        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                                        if(idArray != null) {
                                            for (int i = 0; i < users.size(); i++) {
                                                for (String id : idArray) {
                                                    if (strings.get(i).equals(id)) {
                                                        list.setItemChecked(i, true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void setAsociados(){
        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>(){
            @Override
            public void onChanged(Map<String, String> strings) {
                userMap = strings;
            }
        });

        SparseBooleanArray checked = list.getCheckedItemPositions();
        int len = checked.size();
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String user = userMap.get(users.get(i));
                selected.add(user);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("Personas", selected);

        fb.collection("forums")
                .document(mParam1)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}