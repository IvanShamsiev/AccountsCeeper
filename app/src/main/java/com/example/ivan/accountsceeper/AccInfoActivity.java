package com.example.ivan.accountsceeper;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccInfoActivity extends AppCompatActivity {

    String name;
    private DataBase dataBase;
    private ListView listInfo;
    private static final int editId = 1;
    private static final int deleteId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = getIntent().getStringExtra("name");
        setTitle(name);

        dataBase = new DataBase(this);

        Cursor cursor = dataBase.getLogPassData(name);

        listInfo = findViewById(R.id.listInfo);
        listInfo.setAdapter(new InfoAdapter(this, cursor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, editId, 1, "Изменить аккаунт");
        menu.add(0, deleteId, 2, "Удалить аккаунт");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case editId:
                Intent intent = new Intent(AccInfoActivity.this, EditActivity.class);
                intent.putExtra("account", "edit");
                intent.putExtra("name", name);
                startActivityForResult(intent, 2);
                break;
            case deleteId:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);

                adb.setTitle("Удаление аккаунта");
                adb.setMessage("Вы действительно хотите удалить аккаунт " + name + "?");
                adb.setIcon(android.R.drawable.ic_menu_delete);
                adb.setPositiveButton("Да", listener);
                adb.setNegativeButton("Нет", listener);

                adb.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == 1) {
            if (data != null) name = data.getStringExtra("accountName");
            setTitle(name);
            listInfo.setAdapter(new InfoAdapter(this, dataBase.getLogPassData(name)));
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    dataBase.deleteAccount(name);
                    finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    public void viewClick(View view) {
        View parentView = (View) view.getParent();
        TextView textValue =  parentView.findViewById(R.id.textValue);
        if (textValue.getInputType() == InputType.TYPE_CLASS_TEXT)
            textValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
        else
            textValue.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    public void copyClick(View view) {
        String text = ((TextView) ((View) view.getParent()).findViewById(R.id.textValue)).getText().toString();

        ClipboardManager clipboard = (ClipboardManager) AccInfoActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, R.string.toastText, Toast.LENGTH_LONG).show();

    }

    private class InfoAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        ArrayList<Map<String, String>> data = new ArrayList<>();

        private InfoAdapter(Context ctx, Cursor cursor) {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("key", cursor.getString(cursor.getColumnIndex("field")));
                    map.put("value", cursor.getString(cursor.getColumnIndex("value")));
                    map.put("hideField", cursor.getString(cursor.getColumnIndex("hidden")));
                    data.add(map);
                } while (cursor.moveToNext());
            }
            inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.size();
        }
        public Object getItem(int position) {
            return data.get(position);
        }
        public long getItemId(int position) {
            return position;
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBase.close();
    }
}
