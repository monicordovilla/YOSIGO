package com.example.yosigo.Persona.ActivitiesPersona;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.Facilitador.ActivitiesFacilitador.PersonasViewModel;
import com.example.yosigo.MainActivity;
import com.example.yosigo.Persona.CalendarPersona.CalendarFragment;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityViewFragment} factory method to
 * create an instance of this fragment.
 */
public class ActivityViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "VER ACTIVIDAD";

    private String mParam1;
    private String CHANNEL_ID = "ag5fg54";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();

    //Dias semana
    private int flag;

    //Elementos de la vista
    private View root;
    private TextView text_name;
    private ImageView img_picto, img_meta, img_cat;
    private ListView list_actividades;
    private ImageButton btn_feedback, btn_email;

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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_view_fragment, container, false);

        text_name = (TextView) root.findViewById(R.id.name_activity_persona);
        img_picto = (ImageView) root.findViewById(R.id.picto_activity_persona);
        img_meta = (ImageView) root.findViewById(R.id.show_picto_meta_persona);
        list_actividades = root.findViewById(R.id.list_array_activities_persona);
        btn_feedback = root.findViewById(R.id.btn_feedback_persona);
        btn_email = root.findViewById(R.id.btn_go_chat);

        btn_email.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", "Chat");
                bundle.putString("param2", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_activityViewFragment2_to_chatPersonaFragment, bundle);
            }
        });

        setButton();
        getDatosTarea();
        createNotificationChannel();
        notifychanges(this.getContext());

        return root;
    }

    private void buttonFeedback(){
        btn_feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_activityViewFragment2_to_createFeedbackFragment, bundle);
            }
        });
    }

    private void buttonAssessment(){
        Drawable drawableAssessment = getResources().getDrawable(R.drawable.como_estas);
        btn_feedback.setImageDrawable(drawableAssessment);
        btn_feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", mParam1);
                Navigation.findNavController(view).navigate(R.id.action_activityViewFragment2_to_createAssessmentFragment, bundle);
            }
        });
    }

    private void setButton(){
        fb.collection("users")
                .document(MainActivity.sesion)
                .collection("activities")
                .document(mParam1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Obtenermos el ??ltimo d??a
                                DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                Date fecha_fin = document.getDate("Fecha Fin", behavior);
                                //Dias de la semana de la actividad
                                flag = Integer.parseInt(document.getData().get("Dias semana").toString());

                                /*
                                * Fecha final
                                * */
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(fecha_fin);
                                //Dia de la semana
                                int dia = cal.get(Calendar.DAY_OF_WEEK);

                                /*
                                * Vemos el ??ltimo d??a real que se realiza la actividad
                                *
                                * Hay comprobamos el ??ltimo d??a de la semana que se realiza y obtenemos
                                * su d??a respecto a la fecha final seleccionada
                                * */
                                int diafin = getDia(dia);
                                int diferencia = (Math.floorMod((diafin-2), 7) + 1);
                                LocalDate realDate = fecha_fin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                realDate = realDate.minusDays(diferencia);

                                /*
                                * Fecha actual
                                * */
                                LocalDate today = LocalDate.now();

                                /*
                                * Si estamos en el ??ltimo d??a real o posterior
                                * */
                                if ( today.compareTo(realDate) >= 0 ){
                                    LocalDate finalRealDate = realDate;
                                    fb.collection("activities")
                                            .document(mParam1).collection("Feedback")
                                            .whereEqualTo("Persona", MainActivity.sesion)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        //Si no hay retroalimentacion
                                                        if(task.getResult().isEmpty()){
                                                            buttonFeedback();
                                                            return;
                                                        }
                                                        boolean haveFeeback = false;
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                            DocumentSnapshot.ServerTimestampBehavior b = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
                                                            Date date = document.getDate("Fecha", b);
                                                            LocalDate tempDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                                            if (tempDate.compareTo(finalRealDate) >= 0){
                                                                haveFeeback = true;
                                                            }
                                                            int wait=0;
                                                        }

                                                        if(haveFeeback) buttonAssessment();
                                                        else buttonFeedback();
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Error getting documents: ", e);
                                                    buttonFeedback();
                                                }
                                            });
                                /*
                                * Si aun no estamos en el ultimo dia
                                * */
                                } else {
                                    buttonFeedback();
                                }
                            }
                        } else {
                            buttonFeedback();
                        }
                    }
                });

    }

    private int getDia(int dia){
        boolean es??ltimo = false;
        //Ver el ??ltimo d??a de la semana de la actividad
        while (!es??ltimo) {
            switch (dia) {
                case Calendar.SUNDAY:
                    es??ltimo = ((CalendarFragment.DOMINGO & flag) == CalendarFragment.DOMINGO);
                    break;

                case Calendar.MONDAY:
                    es??ltimo = ((CalendarFragment.LUNES & flag) == CalendarFragment.LUNES);
                    break;

                case Calendar.TUESDAY:
                    es??ltimo = ((CalendarFragment.MARTES & flag) == CalendarFragment.MARTES);
                    break;

                case Calendar.WEDNESDAY:
                    es??ltimo = ((CalendarFragment.MIERCOLES & flag) == CalendarFragment.MIERCOLES);
                    break;

                case Calendar.THURSDAY:
                    es??ltimo = ((CalendarFragment.JUEVES & flag) == CalendarFragment.JUEVES);
                    break;

                case Calendar.FRIDAY:
                    es??ltimo = ((CalendarFragment.VIERNES & flag) == CalendarFragment.VIERNES);
                    break;

                case Calendar.SATURDAY:
                    es??ltimo = ((CalendarFragment.SABADO & flag) == CalendarFragment.SABADO);
                    break;
            }

            if (!es??ltimo) {
                dia =Math.floorMod((dia-2), 7) + 1; // Decrementa de 7 a 1, volviendo al 7 al terminar
            }
        }

        return dia;
    }

    private void getDatosTarea(){
        fb.collection("activities").document(mParam1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        text_name.setText(document.getData().get("Nombre").toString());

                        // Create a reference to a file from a Google Cloud Storage URI
                        if(document.getData().get("Pictograma") != null) {
                            storageRef.child((String) document.getData().get("Pictograma")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(root)
                                            .load(uri)
                                            .into(img_picto);
                                }
                            });
                        }

                        if(document.getData().get("Meta") != null) {
                            getGoal((String) document.getData().get("Meta"));
                        }

                        if(document.getData().get("Tarea") != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    root.getContext(),
                                    android.R.layout.simple_list_item_1,
                                    (List<String>) document.getData().get("Tarea")
                            );
                            list_actividades.setAdapter(adapter);
                            list_actividades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void getGoal(String id){
        fb.collection("goals").document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        storageRef
                                .child((String) document.getData().get("Pictograma"))
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(root)
                                                .load(uri)
                                                .into(img_meta);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notifychanges(Context context){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cambiar);


        fb.collection("activities")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if( dc.getDocument().getId().equals(mParam1) ) {
                                switch (dc.getType()) {
                                    case MODIFIED:
                                        Log.d(TAG, "se ha modificado la actividad");
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.ic_launcher_background)
                                                .setContentTitle(text_name.getText().toString())
                                                .setContentText("Modificada la actividad")
                                                .setLargeIcon(icon)
                                                .setStyle(new NotificationCompat.BigPictureStyle()
                                                        .bigPicture(icon)
                                                        .bigLargeIcon(null))
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                                        // notificationId is a unique int for each notification that you must define
                                        notificationManager.notify(1, builder.build());
                                        break;
                                }
                            }
                        }

                    }
                });
    }
}