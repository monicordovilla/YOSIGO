package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.List;

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

        getParticipantes();
        return root;
    }

    private void getParticipantes(){
        List<String> participantes = new ArrayList<>();
        List<String> user_id = new ArrayList<>();
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

    private void getUsers(){
        Log.d("CHAT LIST", mParam1);
        List<String> users = new ArrayList<>();
        fb.collection("activities")
                .document(mParam1)
                .collection("Chat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "Documents: " + task.getResult().getDocuments());
                        if (task.isSuccessful()) {
                            //Obtener usuario
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                fb.collection("users")
                                        .document(document.getData().get("Emisor").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task_user) {
                                                if (task_user.isSuccessful()) {
                                                    DocumentSnapshot doc_user = task_user.getResult();
                                                    String full_name = doc_user.getData().get("Nombre") + " " +
                                                            doc_user.getData().get("Apellidos") + " (" +
                                                            doc_user.getData().get("Apodo") + ")";
                                                    users.add(full_name);
                                                }
                                                list.setAdapter(new ArrayAdapter<String>(
                                                                root.getContext(),
                                                                android.R.layout.simple_list_item_1,
                                                                users
                                                        )
                                                );
                                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override//
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("param1", mParam1);
                                                        bundle.putString("param2", users.get(position));
                                                        Navigation.findNavController(view).navigate(R.id.action_chatUsersListFragment_to_chatFacilitadorFragment, bundle);
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}