package com.example.yosigo.Persona;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridAdapter extends ArrayAdapter {
    private final Context context;
    private final List<String> id;
    private final Map<String,String> item_name;
    private final Map<String,String> item_picto;

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public GridAdapter(Context context, List<String> id,
                       Map<String,String> item_name, Map<String,String> item_picto) {
        super(context, R.layout.grid_item, id);
        this.context = context;
        this.id = id;
        this.item_name = item_name;
        this.item_picto = item_picto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.grid_item, parent, false);

        ImageView pictograma = (ImageView) rowView.findViewById(R.id.grid_picto_descriptivo);
        TextView titulo = (TextView) rowView.findViewById(R.id.text_title_content);

        String id_item = id.get(position);

        storageRef.child(item_picto.get(id_item)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(pictograma);

            }
        });
        titulo.setText(item_name.get(id_item));

        return rowView;
    }
}
