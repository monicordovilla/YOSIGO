package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yosigo.Facilitador.ActivitiesFacilitador.dummy.DummyContent.DummyItem;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AssessmentViewItemAdapter extends ArrayAdapter {
    private final Context context;
    private final List<String> id;
    private final Map<String, Date> item_date;
    private final Map<String,String> item_file;
    private final Map<String,String> item_type;
    private final Map<String,String> item_user;
    private final Map<String,Integer> item_difficult;
    private final Map<String,Integer> item_utility;
    private MediaPlayer mediaPlayer = null;

    public AssessmentViewItemAdapter(Context context, List<String> objects, Map<String, Date> item_date,
                                      Map<String,Integer> item_difficult, Map<String,Integer> item_utility,
                                     Map<String,String> item_file, Map<String,String> item_type,
                                      Map<String,String> item_user) {
        super(context, R.layout.fragment_view_assessment, objects);
        this.context = context;
        this.id = objects;
        this.item_date = item_date;
        this.item_file = item_file;
        this.item_type = item_type;
        this.item_user = item_user;
        this.item_difficult = item_difficult;
        this.item_utility = item_utility;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.fragment_view_assessment, parent, false);

        TextView fecha = rowView.findViewById(R.id.date_assessment);
        TextView nombre = (TextView) rowView.findViewById(R.id.name_assessment);
        TextView dificultad = (TextView) rowView.findViewById(R.id.text_assessment_difficult);
        TextView utilidad = (TextView) rowView.findViewById(R.id.text_assessment_utility);
        TextView titulo_sugerencia = (TextView) rowView.findViewById(R.id.text_suggestion);
        TextView sugerencia = (TextView) rowView.findViewById(R.id.text_assessment_suggestion);
        ImageButton btn = rowView.findViewById(R.id.button_assessment_suggestion);

        String id_item = id.get(position);

        //Convertir fecha a String
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(item_date.get(id_item));
        fecha.setText(strDate);

        nombre.setText(item_user.get(id_item));
        sugerencia.setText(item_file.get(id_item));
        String string_dificultad, string_utilidad;
        Log.d("ASSESSMENT ADAPTER", String.valueOf(item_difficult.get(id_item)));
        if(item_difficult.get(id_item) == 3){
            string_dificultad = "Baja";
        } else if(item_difficult.get(id_item) == 2){
            string_dificultad = "Media";
        } else {
            string_dificultad = "Alta";
        }
        dificultad.setText(string_dificultad);
        Log.d("ASSESSMENT ADAPTER", String.valueOf(item_utility.get(id_item)));
        if(item_utility.get(id_item) == 3){
            string_utilidad = "Alta";
        } else if(item_utility.get(id_item) == 2){
            string_utilidad = "Media";
        } else {
            string_utilidad = "Baja";
        }
        utilidad.setText(string_utilidad);

        if(item_type.get(id_item).equals("audio")){
            btn.setImageResource(R.drawable.ic_sound);
            sugerencia.setVisibility(View.GONE);

            btn.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    FirebaseStorage.getInstance().getReference().
                            child(item_file.get(id_item))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if( mediaPlayer != null ){
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(context, uri);
                                mediaPlayer.prepare();
                                mediaPlayer.start();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } else if(item_type.get(id_item).equals("Texto")){
            btn.setVisibility(View.GONE);
        } else if(item_type.get(id_item).equals("archivo") || item_type.get(id_item).equals("imagen")){
            sugerencia.setVisibility(View.GONE);
            btn.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    FirebaseStorage.getInstance().getReference().
                            child(item_file.get(id_item))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(launchBrowser);
                        }
                    });
                }
            });
        } else {
            btn.setVisibility(View.GONE);
            titulo_sugerencia.setVisibility(View.GONE);
            sugerencia.setVisibility(View.GONE);
        }

        return rowView;
    }
}