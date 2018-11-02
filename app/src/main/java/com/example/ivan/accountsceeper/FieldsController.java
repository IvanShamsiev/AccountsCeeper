package com.example.ivan.accountsceeper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

class FieldsController {

    private Context context;
    private LayoutInflater inflater;
    private LinearLayout layoutForFields;
    private ArrayList<LinearLayout> fields = new ArrayList<>();

    FieldsController (Context ctx, LinearLayout layout, ArrayList<HashMap<String, String>> list) {
        context = ctx;
        layoutForFields = layout;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (HashMap<String, String> map: list) {
            addField(map);
        }
    }


    void addField(HashMap<String, String> map) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(params);

        inflater.inflate(R.layout.edit_item, layout);

        EditText editName = layout.findViewById(R.id.editName);
        EditText editValue = layout.findViewById(R.id.editValue);
        ImageView imgFieldClose = layout.findViewById(R.id.imgFieldClose);
        ImageView imgInputType = layout.findViewById(R.id.imgInputType);

        String name = map.get("name");
        String value = map.get("value");
        String inputType = map.get("inputType");

        editName.setText(name);
        editValue.setText(value);
        imgInputType.setOnClickListener(inputTypeListener);
        imgFieldClose.setOnClickListener(closeFieldListener);

        boolean isHidden = inputType == null ? false : !inputType.equals("visible");
        if (isHidden) inputTypeListener.onClick(imgInputType);


        fields.add(layout);
        layoutForFields.addView(layout);

        LinearLayout.LayoutParams paramsForLine = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        View line = new View(context);
        line.setLayoutParams(paramsForLine);
        line.setBackgroundColor(Color.BLACK);

        layoutForFields.addView(line);

    }
    private View.OnClickListener closeFieldListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteField(v);
        }
    };
    private View.OnClickListener inputTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag().equals("visible")) {
                v.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
                v.setTag("hidden");
            } else {
                v.setBackgroundColor(Color.TRANSPARENT);
                v.setTag("visible");
            }
        }
    };

    private void deleteField(View view) {
        LinearLayout layout = (LinearLayout) view.getParent().getParent();
        layoutForFields.removeViewAt(fields.indexOf(layout)*2 + 1);
        layoutForFields.removeView(layout);
        fields.remove(layout);
    }

    public ArrayList<HashMap<String, String>> getInfo() {

        ArrayList<HashMap<String, String>> fieldsData = new ArrayList<>(fields.size());
        HashMap<String, String> map;
        for (LinearLayout field: fields) {
            map = new HashMap<>();
            map.put("name", ((EditText) field.findViewById(R.id.editName)).getText().toString());
            map.put("value", ((EditText) field.findViewById(R.id.editValue)).getText().toString());
            map.put("inputType", field.findViewById(R.id.imgInputType).getTag().toString());
            fieldsData.add(map);
        }

        return fieldsData;
    }
}
