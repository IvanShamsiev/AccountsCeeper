package com.example.ivan.accountsceeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

class DataBase {

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private String[] iconNames = {"Стандартная иконка", "ВКонтакте", "Яндекс"};
    private int[] icons = {android.R.drawable.sym_def_app_icon, R.drawable.vk, R.drawable.yandex};


    DataBase(Context ctx) {
        context = ctx;
        dbHelper = new DBHelper();
        database = dbHelper.getWritableDatabase();
    }

    boolean checkAccountName(String accountName) {
        Cursor c = database.rawQuery("Select * from accounts where name = ?", new String[]{accountName});
        boolean isClear = c.getCount() == 0;
        c.close();
        return isClear;
    }

    void addAccount(ArrayList<HashMap<String, String>> data) {
        String accountName = data.get(0).get("accountName");

        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put("name", accountName);
        contentValues.put("account", data.get(0).get("other") == null ? "" : data.get(0).get("other"));
        contentValues.put("type", data.get(0).get("type") == null ? "" : data.get(0).get("type"));

        String img = data.get(0).get("icon");
        for (int i = 0; i < iconNames.length; i++)
            if (img.equals(iconNames[i])) {
                img = String.valueOf(icons[i]);
                break;
            }
        contentValues.put("img", img);

        database.insert("accounts", null, contentValues);

        ListIterator<HashMap<String, String>> iterator = data.listIterator();
        HashMap<String, String> map;
        iterator.next();
        while (iterator.hasNext()) {
            map = iterator.next();
            contentValues = new ContentValues();
            contentValues.put("name", accountName);
            contentValues.put("field", map.get("name"));
            contentValues.put("value", map.get("value"));
            contentValues.put("hidden", String.valueOf(!map.get("inputType").equals("visible")));
            database.insert("logpasses", null, contentValues);
        }
    }

    void deleteAccount(String name) {
        database.delete("accounts", "name = ?", new String[] {name});
        database.delete("logpasses", "name = ?", new String[] {name});
    }


    Cursor getAccountsData() {
        return database.rawQuery("Select * from accounts", null);
    }

    Cursor getAccountData(String name) {
        return database.rawQuery("Select * from accounts where name = ?", new String[] {name});
    }

    Cursor getLogPassData(String name) {
        return database.rawQuery("Select field, value, hidden from logpasses where name = ?", new String[] {name});
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper() {
            super(context, "myDataBase", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table accounts (" +
                    "_id integer primary key autoincrement, " +
                    "name text, " +
                    "img integer, " +
                    "account text, " +
                    "type text)");

            db.execSQL("create table logpasses (" +
                    "_id integer primary key autoincrement, " +
                    "name text, " +
                    "field text, " +
                    "value text, " +
                    "hidden text)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
