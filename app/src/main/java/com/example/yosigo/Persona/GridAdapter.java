package com.example.yosigo.Persona;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.yosigo.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private List<itemGrid> item_grid;


    public GridAdapter(Context context, List<itemGrid> item_grid) {
        this.context = context;
        this.item_grid = item_grid;
    }

    @Override
    public int getCount() {
        return item_grid.size();
    }

    @Override
    public Object getItem(int i) {
        return item_grid.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
        }

        ImageView pictograma = (ImageView) view.findViewById(R.id.grid_picto_descriptivo);
        TextView titulo = (TextView) view.findViewById(R.id.text_title_content);


        titulo.setText(item_grid.get(position).getTitulo());
        if(pictograma != null) {
            Glide.with(context)
                    .load(item_grid.get(position).getPictograma())
                    .into(pictograma);
        }

        return view;
    }
}
