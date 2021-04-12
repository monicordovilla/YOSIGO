package com.example.yosigo.Persona.ActivitiesPersona;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeedbackAdapter extends ArrayAdapter {
    private final Context context;
    private final List<String> id;
    private final Map<String,Date> item_date;
    private final Map<String,String> item_file;
    private final Map<String,String> item_type;

    public FeedbackAdapter(Context context, List<String> objects, Map<String, Date> item_date,
                           Map<String,String> item_file, Map<String,String> item_type) {
        super(context, R.layout.feedback_adapter_item, objects);
        this.context = context;
        this.id = objects;
        this.item_date = item_date;
        this.item_file = item_file;
        this.item_type = item_type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.feedback_adapter_item, parent, false);

        TextView fecha = rowView.findViewById(R.id.show_date_persona_feedback);;
        TextView texto = (TextView) rowView.findViewById(R.id.show_persona_feedback);
        ImageButton btn = rowView.findViewById(R.id.button_watch_feedback_persona);

        String id_item = id.get(position);

        //Convertir fecha a String
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(item_date.get(id_item));
        fecha.setText(strDate);


        texto.setText(item_file.get(id_item));

        if(item_type.get(id_item).equals("Texto")){
            btn.setVisibility(View.GONE);
        } else if(item_type.get(id_item).equals("Archivo")){
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
        }

        return rowView;
    }
}