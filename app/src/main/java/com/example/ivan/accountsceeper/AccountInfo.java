package com.example.ivan.accountsceeper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        setTitle(name);

        DataBase dataBase = new DataBase(this);

        Cursor cursor = dataBase.getLogPassData(name);

        ArrayList<Map<String, String>> list = new ArrayList<>();
        HashMap<String, String> map;

        if (cursor.moveToFirst()) {
            do {
                map = new HashMap<>();
                map.put("key", cursor.getString(cursor.getColumnIndex("field")));
                map.put("value", cursor.getString(cursor.getColumnIndex("value")));
                map.put("hideField", cursor.getString(cursor.getColumnIndex("hidden")));
                list.add(map);
            } while (cursor.moveToNext());
        }

        myAdapter adapter = new myAdapter(this, list);

        ListView listInfo = findViewById(R.id.listInfo);
        listInfo.setAdapter(adapter);
        listInfo.setOnItemClickListener(onItemClickListener);
    }
    
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = ((TextView) view.findViewById(R.id.textValue)).getText().toString();

            ClipboardManager clipboard = (ClipboardManager) AccountInfo.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", text);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(AccountInfo.this, "Значение скопировано в буфер обмена", Toast.LENGTH_LONG).show();
        }
    };

    public void imgClick(View view) {
        View parentView = (View) view.getParent();
        TextView textValue =  parentView.findViewById(R.id.textValue);
        if (textValue.getInputType() == InputType.TYPE_CLASS_TEXT)
            textValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
        else
            textValue.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    public void copyClick(View view) {

        String text = ((TextView) ((View) view.getParent()).findViewById(R.id.textValue)).getText().toString();

        ClipboardManager clipboard = (ClipboardManager) AccountInfo.this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(AccountInfo.this, "Значение скопировано в буфер обмена", Toast.LENGTH_LONG).show();

    }
}
