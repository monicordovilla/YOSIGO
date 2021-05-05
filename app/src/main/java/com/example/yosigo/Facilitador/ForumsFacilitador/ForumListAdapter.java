package com.example.yosigo.Facilitador.ForumsFacilitador;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.example.yosigo.R;

import java.util.List;
import java.util.Map;

public class ForumListAdapter extends ArrayAdapter {

    private final Context context;
    private final Map<String,String> valuesList;
    List<String> nameList;

    public ForumListAdapter(Context context, Map<String,String> valuesList, List<String>nameList) {
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

        //Text
        TextView textView = (TextView) rowView.findViewById(R.id.textViewAlias);
        textView.setText(name);
        textView.setId(position);

        //Buttons
        Button btn_watch = (Button) rowView.findViewById(R.id.watch_activity);
        Button btn_asociate = (Button) rowView.findViewById(R.id.asociate_activity);
        Button btn_delete = (Button) rowView.findViewById(R.id.delete_activity);

        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", "Forum");
                bundle.putString("param2", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_chatFacilitadorFragment, bundle);
            }
        });

        btn_asociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("param1", valuesList.get(name));
                Navigation.findNavController(view).navigate(R.id.action_nav_forums_to_asociateForumFragment, bundle);
            }
        });

        return rowView;
    }
}
