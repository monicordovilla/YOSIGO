package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yosigo.R;

import java.util.List;

public class ActivityListAdapter extends ArrayAdapter {

    private final Context context;
    private final List<String> valuesList;

    public ActivityListAdapter(Context context, List valuesList) {
        super(context, R.layout.list_activities_item, valuesList);
        this.context = context;
        this.valuesList = valuesList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_activities_item, parent, false);

        String name = valuesList.get(position);

        TextView textViewAlias = (TextView) rowView.findViewById(R.id.textViewAlias);
        textViewAlias.setText(name);

        return rowView;
    }
}
