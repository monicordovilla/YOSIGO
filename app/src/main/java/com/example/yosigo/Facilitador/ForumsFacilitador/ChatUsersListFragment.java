package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

import com.example.yosigo.Facilitador.ActivitiesFacilitador.ActivityListAdapter;
import com.example.yosigo.MainActivity;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatUsersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatUsersListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "USER LIST";

    private String mParam1;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private View root;
    private ListView list;
    private EditText searchBar;

    List<String> participantes = new ArrayList<>();
    List<String> user_id = new ArrayList<>();

    public ChatUsersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatUsersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatUsersListFragment newInstance(String param1, String param2) {
        ChatUsersListFragment fragment = new ChatUsersListFragment();
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
        root = inflater.inflate(R.layout.fragment_chat_users_list, container, false);
        list = root.findViewById(R.id.list_chat_users);

        //Set searchbar
        searchBar = root.findViewById(R.id.search_person_name);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                filterActivities(newText);
            }
        });

        getParticipantes();
        return root;
    }

    private void getParticipantes(){
        participantes.clear();
        user_id.clear();

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("Rol", "Persona")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String full_name = document.getData().get("Nombre") + " " +
                                        document.getData().get("Apellidos") + " (" +
                                        document.getData().get("Apodo") + ")";

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(document.getId())
                                        .collection("activities")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> act) {
                                                if (act.isSuccessful()) {
                                                    for (QueryDocumentSnapshot doc : act.getResult()) {
                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        if(mParam1.equals(doc.getId())) {
                                                            /*Marca de firebase
                                                             * url problema: https://stackoverflow.com/questions/47771044/firestore-timestamp-getting-null
                                                             * */
                                                            DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                            Date fecha_inicio = doc.getDate("Fecha Inicio", behavior) ;
                                                            Date fecha_fin = doc.getDate("Fecha Fin", behavior) ;

                                                            if (new Date().after(fecha_inicio) && new Date().before(fecha_fin)) {
                                                                participantes.add(full_name);
                                                                user_id.add(document.getId());
                                                                Log.d(TAG, "Participante: " + full_name);
                                                            }
                                                        }
                                                    }

                                                    ArrayAdapter<String> adapter = new ArrayAdapter(
                                                            root.getContext(),
                                                            android.R.layout.simple_list_item_1,
                                                            participantes
                                                    );
                                                    list.setAdapter(adapter);
                                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override//
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("param1", mParam1);
                                                            bundle.putString("param2", user_id.get(position));
                                                            Navigation.findNavController(view).navigate(R.id.action_chatUsersListFragment_to_chatFacilitadorFragment, bundle);
                                                        }
                                                    });
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", act.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void filterActivities(String text) {
        if(text.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter(
                    root.getContext(),
                    android.R.layout.simple_list_item_1,
                    participantes
            );
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override//
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", mParam1);
                    bundle.putString("param2", user_id.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_chatUsersListFragment_to_chatFacilitadorFragment, bundle);
                }
            });
        } else {
            List<String> filterList = new ArrayList<>();
            List<String> filterId = new ArrayList<>();

            for (int i=0; i < user_id.size(); i++) {
                String name = participantes.get(i);
                if( name.toLowerCase().contains(text.toLowerCase()) ) {
                    filterList.add(name);
                    filterId.add(user_id.get(i));
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter(
                    root.getContext(),
                    android.R.layout.simple_list_item_1,
                    filterList
            );
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override//
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("param1", mParam1);
                    bundle.putString("param2", filterId.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_chatUsersListFragment_to_chatFacilitadorFragment, bundle);
                }
            });
        }
    }
}