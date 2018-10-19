package com.example.ivan.accountsceeper;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

class InfoAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    ArrayList<Map<String, String>> data;

    public InfoAdapter(Context ctx, ArrayList<Map<String, String>> data) {
        this.data = data;
        context = ctx;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) view = inflater.inflate(R.layout.info_item, parent, false);

        TextView textKey = view.findViewById(R.id.textKey);
        textKey.setText(data.get(position).get("key"));

        TextView textValue = view.findViewById(R.id.textValue);
        textValue.setText(data.get(position).get("value"));

        boolean isHide = Boolean.parseBoolean(data.get(position).get("hideField"));

        if (isHide)
            textValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
        else
            view.findViewById(R.id.imageView).setVisibility(View.INVISIBLE);

        return view;
    }
}