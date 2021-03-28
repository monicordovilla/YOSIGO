package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yosigo.R;

import java.util.List;
import java.util.Map;

public class ActivityListAdapter extends ArrayAdapter {

    private final Context context;
    private final Map<String,String> valuesList;
    List<String> nameList;


    public ActivityListAdapter(Context context, Map<String,String> valuesList, List<String>nameList) {
        super(context, R.layout.list_activities_item, nameList);

        this.context = context;
        this.valuesList = valuesList;
        this.nameList = nameList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_activities_item, parent, false);

        String name = nameList.get(position);

        TextView textViewAlias = (TextView) rowView.findViewById(R.id.textViewAlias);
        textViewAlias.setText(name);

        return rowView;
    }
}
