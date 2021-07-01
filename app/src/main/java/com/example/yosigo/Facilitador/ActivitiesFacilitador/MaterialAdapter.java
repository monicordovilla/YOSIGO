package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.yosigo.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class MaterialAdapter extends ArrayAdapter {

    private static final String TAG = "ITEM ADAPTER";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final Context context;
    private String id;
    private List<String> valuesList;

    public MaterialAdapter(Context context, String id, List<String>valuesList) {
        super(context, R.layout.material_adapter, valuesList);

        this.context = context;
        this.id = id;
        this.valuesList = valuesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.material_adapter, parent, false);

        String name = valuesList.get(position);
        Log.d(TAG, "Nombre: " + name);

        //Text
        TextView textView = (TextView) rowView.findViewById(R.id.nameMaterials);
        textView.setText(name);

        //Buttons
        Button btn_watch = (Button) rowView.findViewById(R.id.watch_material);
        Button btn_delete = (Button) rowView.findViewById(R.id.delete_material);

        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(launchBrowser);
                    }
                });
            }
        });

        if(id == null) {
            btn_delete.setVisibility(View.GONE);
        } else {
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storageRef.child(name).delete();
                    FirebaseFirestore.getInstance().collection("activities").document(id)
                            .update("Tarea", FieldValue.arrayRemove(name));
                }
            });
        }

        return rowView;
    }
}