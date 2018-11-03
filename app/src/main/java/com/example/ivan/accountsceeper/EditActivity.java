package com.example.ivan.accountsceeper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    private FieldsController fieldsController;
    private String[] iconNames = {"Стандартная иконка", "ВКонтакте", "Яндекс"};
    private int[] icons = {android.R.drawable.sym_def_app_icon, R.drawable.vk, R.drawable.yandex};

    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dataBase = DataBase.getDataBase(this);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, iconNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinnerIcon);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Выбор иконки");

        if (getIntent().getStringExtra("account").equals("create")) create(list, spinner);
        if (getIntent().getStringExtra("account").equals("edit")) edit(list, spinner);

        LinearLayout layoutForFields = findViewById(R.id.layoutForFields);

        fieldsController = new FieldsController(this, layoutForFields, list);

    }

    void create(ArrayList<HashMap<String, String>> list, Spinner spinner) {
        setTitle("Новый аккаунт");

        HashMap<String, String> map = new HashMap<>();
        map.put("name", "Логин");
        map.put("value", "");
        map.put("inputType", "visible");
        list.add(map);
        map = new HashMap<>();
        map.put("name", "Пароль");
        map.put("value", "");
        map.put("inputType", "hidden");
        list.add(map);

        spinner.setSelection(0);

        findViewById(R.id.layoutEdit).setVisibility(View.GONE);
    }

    void edit(ArrayList<HashMap<String, String>> list, Spinner spinner) {
        setTitle("Редактирование аккаунта");

        String name = getIntent().getStringExtra("name");
        String other = "";
        String type = "";
        int img = 0;
        Cursor cursor = dataBase.getAccountData(name);
        if (cursor.moveToFirst()) {
            other = cursor.getString(cursor.getColumnIndex("account"));
            type = cursor.getString(cursor.getColumnIndex("type"));
            img = cursor.getInt(cursor.getColumnIndex("img"));
        }
        ((EditText) findViewById(R.id.editAccountName)).setText(name);
        ((EditText) findViewById(R.id.editOther)).setText(other);
        ((EditText) findViewById(R.id.editType)).setText(type);
        for (int i = 0; i < iconNames.length; i++) if (img == icons[i]) spinner.setSelection(i);

        cursor = dataBase.getLogPassData(name);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", cursor.getString(cursor.getColumnIndex("field")));
                map.put("value", cursor.getString(cursor.getColumnIndex("value")));
                map.put("inputType", cursor.getString(cursor.getColumnIndex("hidden")).equals("true") ? "hidden" : "visible");
                list.add(map);
            } while (cursor.moveToNext());
        }

        findViewById(R.id.buttonConfim).setVisibility(View.GONE);
    }



    public void imgClose(View view) {
        LinearLayout layout = (LinearLayout) view.getParent();
        View nameView = new View(this);
        View enabledView = new View(this);
        switch (view.getId()) {
            case R.id.imgCloseOther:
                nameView = layout.findViewById(R.id.textViewOther);
                enabledView = layout.findViewById(R.id.editOther);
                break;
            case R.id.imgCloseType:
                nameView = layout.findViewById(R.id.textViewType);
                enabledView = layout.findViewById(R.id.editType);
                break;
            case R.id.imgCloseIcon:
                nameView = layout.findViewById(R.id.textViewIcon);
                enabledView = layout.findViewById(R.id.spinnerIcon);
                break;
        }
        if (layout.isEnabled()) {
            nameView.setAlpha(0.3f);
            enabledView.setAlpha(0.3f);
            enabledView.setEnabled(false);

            ImageView imgClose = (ImageView) view;
            imgClose.setImageResource(android.R.drawable.ic_menu_add);

            layout.setEnabled(false);
        } else {
            nameView.setAlpha(1.0f);
            enabledView.setAlpha(1.0f);
            enabledView.setEnabled(true);

            ImageView imgClose = (ImageView) view;
            imgClose.setImageResource(android.R.drawable.btn_dialog);

            layout.setEnabled(true);
        }
    }

    public void addField(View view) {
        fieldsController.addField(new HashMap<String, String>());
    }

    public void AddConfim(View view) {
        ArrayList<HashMap<String, String>> fieldsData = new ArrayList<>();
        String accountName = ((EditText) findViewById(R.id.editAccountName)).getText().toString();

        if (!dataBase.checkAccountName(accountName)) {
            Toast.makeText(this, "Аккаунт с таким именем уже существует", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, String> map;
        map = new HashMap<>();
        map.put("accountName", accountName);
        if (findViewById(R.id.layoutOther).isEnabled())
            map.put("other", ((EditText) findViewById(R.id.editOther)).getText().toString());
        if (findViewById(R.id.layoutType).isEnabled())
            map.put("type", ((EditText) findViewById(R.id.editType)).getText().toString());
        if (findViewById(R.id.layoutIcon).isEnabled())
            map.put("icon", iconNames[((Spinner) findViewById(R.id.spinnerIcon)).getSelectedItemPosition()]);
        fieldsData.add(map);
        fieldsData.addAll(fieldsController.getInfo());

        dataBase.addAccount(fieldsData);

        Intent intent = new Intent();
        intent.putExtra("accountName", accountName);

        setResult(1, intent);
        finish();

    }

    public void EditSave(View view) {
        dataBase.deleteAccount(getIntent().getStringExtra("name"));
        AddConfim(view);
    }

    public void EditCancel(View view) {
        setResult(0);
        finish();
    }

}
