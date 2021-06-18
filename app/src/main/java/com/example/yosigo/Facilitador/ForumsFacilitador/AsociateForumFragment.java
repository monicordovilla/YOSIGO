package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.Facilitador.Groups.GroupListViewModel;
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
    private PersonasViewModel personasViewModel;
    private GroupListViewModel groupListViewModel;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private View root;
    private TextView forum_name;
    private ListView list_personas, list_grupos;
    private EditText searchBar;
    private Button btn_asociate;

    private Map<String, String> userMap = new HashMap<>();
    private Map<String, String> groupMap = new HashMap<>();
    private List<String> users = new ArrayList<>();
    private List<String> groups = new ArrayList<>();
    private String mParam1;

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
        personasViewModel = new ViewModelProvider(this).get(PersonasViewModel.class);
        groupListViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_asociate_forum, container, false);

        //Obtener elementos del layout
        list_personas = root.findViewById(R.id.list_asociate_personas_forum);
        list_grupos = root.findViewById(R.id.list_asociate_grupos_forum);
        searchBar = root.findViewById(R.id.search_name_forum);
        btn_asociate = root.findViewById(R.id.btn_asociate_forum);
        forum_name = root.findViewById(R.id.asociate_forum_name);

        getName();

        btn_asociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAsociados();
            }
        });

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

        getAsociados();

        return root;
    }

    private void getName() {
        fb.collection("forums")
                .document(mParam1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                forum_name.setText(document.getData().get("Nombre").toString());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
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

                                personasViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
                                    @Override
                                    public void onChanged(List<String> strings) {
                                        users = strings;
                                        list_personas.setAdapter(new ArrayAdapter<String>(
                                                root.getContext(),
                                                android.R.layout.simple_list_item_multiple_choice,
                                                users
                                                )
                                        );
                                        list_personas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                                        if(idArray != null) {
                                            for (int i = 0; i < users.size(); i++) {
                                                for (String id : idArray) {
                                                    if (strings.get(i).equals(id)) {
                                                        list_personas.setItemChecked(i, true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });

                                groupListViewModel.getNames().observe(getViewLifecycleOwner(), new Observer<List<String>>(){
                                    @Override
                                    public void onChanged(List<String> strings) {
                                        groups = strings;
                                        list_grupos.setAdapter(new ArrayAdapter<String>(
                                                        root.getContext(),
                                                        android.R.layout.simple_list_item_multiple_choice,
                                                        groups
                                                )
                                        );
                                        list_grupos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void setAsociados(){
        personasViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>(){
            @Override
            public void onChanged(Map<String, String> strings) {
                userMap = strings;
            }
        });

        groupListViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>(){
            @Override
            public void onChanged(Map<String, String> strings) {
                groupMap = strings;
            }
        });

        //Asociar personas individuales
        SparseBooleanArray checked = list_personas.getCheckedItemPositions();
        int len = checked.size();
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String user = userMap.get(users.get(i));
                selected.add(user);
            }
        }

        //Asociar grupo
        checked = list_grupos.getCheckedItemPositions();
        len = checked.size();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                String item = groups.get(i);
                String id_group = groupMap.get(item);

                fb.collection("groups")
                        .document(id_group)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ArrayList<String> userlist = (ArrayList<String>) document.getData().get("Usuarios");

                                        Log.d(TAG, userlist.toString());
                                        for (String id_user: userlist){
                                            selected.add(id_user);
                                        }
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

                                Toast.makeText(
                                        getContext(),
                                        "Se asociado a los participantes seleccionados al foro con éxito",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
            }
        }
    }

    private void filterNames(String text) {
        if(text.isEmpty()){
            //Set groups
            list_grupos.setAdapter(new ArrayAdapter<String>(
                    root.getContext(),
                    android.R.layout.simple_list_item_multiple_choice,
                    groups
                )
            );
            list_grupos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            //Set personas
            list_personas.setAdapter(new ArrayAdapter<String>(
                            root.getContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            users
                    )
            );
            list_personas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else {
            List<String> filterGroupsList = new ArrayList<>();
            List<String> filterPersonList = new ArrayList<>();

            //Set groups
            for (String name : groups) {
                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    filterGroupsList.add(name);
                }
            }
            list_grupos.setAdapter(new ArrayAdapter<String>(
                            root.getContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            filterGroupsList
                    )
            );
            list_grupos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            //Set personas
            for (String name : users) {
                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    filterPersonList.add(name);
                }
            }
            list_personas.setAdapter(new ArrayAdapter<String>(
                            root.getContext(),
                            android.R.layout.simple_list_item_multiple_choice,
                            filterPersonList
                    )
            );
            list_personas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }
}