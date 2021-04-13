package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeedbackFacilitadorAdapter extends ArrayAdapter {
    private final Context context;
    private final List<String> id;
    private final Map<String,Date> item_date;
    private final Map<String,String> item_file;
    private final Map<String,String> item_type;
    private final Map<String,String> item_user;

    public FeedbackFacilitadorAdapter(Context context, List<String> objects, Map<String, Date> item_date,
                                      Map<String,String> item_file, Map<String,String> item_type,
                                      Map<String,String> item_user) {
        super(context, R.layout.feedback_adapter_item_facilitador, objects);
        this.context = context;
        this.id = objects;
        this.item_date = item_date;
        this.item_file = item_file;
        this.item_type = item_type;
        this.item_user = item_user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.feedback_adapter_item_facilitador, parent, false);

        TextView fecha = rowView.findViewById(R.id.show_date_facilitador_feedback);
        TextView nombre = (TextView) rowView.findViewById(R.id.show_facilitador_nombre_persona);
        TextView texto = (TextView) rowView.findViewById(R.id.show_facilitador_feedback);
        ImageButton btn = rowView.findViewById(R.id.button_watch_feedback_facilitador);

        String id_item = id.get(position);

        //Convertir fecha a String
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(item_date.get(id_item));
        fecha.setText(strDate);

        nombre.setText(item_user.get(id_item));
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