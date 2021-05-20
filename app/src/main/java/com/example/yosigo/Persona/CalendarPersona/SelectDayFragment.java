package com.example.yosigo.Persona.CalendarPersona;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yosigo.MainActivity;
import com.example.yosigo.Persona.ActivitiesPersona.Activity;
import com.example.yosigo.Persona.ActivitiesPersona.ListActivitiesPersonaViewModel;
import com.example.yosigo.Persona.GridAdapter;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
 * Use the {@link SelectDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectDayFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "DAY ACTIVITIES";

    private String mParam1;
    private ListActivitiesPersonaViewModel mViewModel;
    private GridView list;
    private List<Activity> actividades = new ArrayList<>();
    View root;

    public SelectDayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SelectDayFragment.
     */
    public static SelectDayFragment newInstance(String param1) {
        SelectDayFragment fragment = new SelectDayFragment();
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
        mViewModel = new ViewModelProvider(this).get(ListActivitiesPersonaViewModel.class);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_select_day, container, false);

        //Listado
        list = (GridView) root.findViewById(R.id.grid_calendar);
        ImageView imageDay = root.findViewById(R.id.select_day);

        switch (Integer.parseInt(mParam1)){
            case CalendarFragment.LUNES:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.lunes));
                break;

            case CalendarFragment.MARTES:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.martes));
                break;

            case CalendarFragment.MIERCOLES:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.miercoles));
                break;

            case CalendarFragment.JUEVES:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.jueves));
                break;

            case CalendarFragment.VIERNES:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.viernes));
                break;

            case CalendarFragment.SABADO:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.sabado));
                break;

            case CalendarFragment.DOMINGO:
                imageDay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.domingo));
                break;
        }

        mViewModel.getActivities().observe(getViewLifecycleOwner(), new Observer<List<Activity>>() {
            @Override
            public void onChanged(List<Activity> activities) {
                getActivities(activities);
            }
        });

        return root;
    }

    private void getActivities(List<Activity> activities){
        actividades.clear();
        List<String> ids = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> images = new HashMap<>();

        for(Activity activity : activities) {
            Date fecha_inicio = activity.getFecha_inicio();
            Date fecha_fin = activity.getFecha_fin();

            if (new Date().after(fecha_inicio) && new Date().before(fecha_fin)) {
                int dia = Integer.parseInt(mParam1);
                if((activity.getDias() & dia) == dia){
                    actividades.add(activity);
                    ids.add(activity.getId());
                    names.put(activity.getId(), activity.getNombre());
                    images.put(activity.getId(), activity.getImagen());
                }
            }
        }

        GridAdapter adapter = new GridAdapter(
                root.getContext(),
                ids,
                names,
                images
        );
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", ids.get(position));
                Navigation.findNavController(view).navigate(R.id.action_selectDayFragment_to_activityViewFragment2, bundle);
            }
        });
    }
}