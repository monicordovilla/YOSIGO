package com.example.yosigo.Facilitador.ActivitiesFacilitador;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.yosigo.R;

import java.util.List;
import java.util.Map;

public class UserCheckBoxAdapter extends ArrayAdapter {

    private final Context context;
    private final List<String> nameList;

    public UserCheckBoxAdapter(Context context, List<String>nameList) {
        super(context, R.layout.list_activities_item, nameList);

        this.context = context;
        this.nameList = nameList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.check_user_item, parent, false);

        String name = nameList.get(position);

        //Text
        TextView textView = (TextView) rowView.findViewById(R.id.checkbox_text_user_name);
        textView.setText(name);
        textView.setId(position);

        //ChechBox
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox_check_user_name);

        return rowView;
    }
}
