package com.example.ivan.accountsceeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DataBase dataBase;
    ListView listView;
    SimpleCursorAdapter adapter;
    View selectedView;

    public static final int editId = 1;
    public static final int deleteId = 2;
    public static final int optionsId = 3;
    public static final int exitId = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataBase = new DataBase(this);

        String[] from = {"name", "img", "account", "type"};
        int[] to = {R.id.textName, R.id.imageView, R.id.textAccount, R.id.textType};

        adapter = new SimpleCursorAdapter(this, R.layout.list_item,
                dataBase.getAccountsData(), from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabOnClickListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        adapter.changeCursor(dataBase.getAccountsData());
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, AccountInfo.class);
            intent.putExtra("name", ((TextView) view.findViewById(R.id.textName)).getText().toString());
            intent.putExtra("img", R.id.imageView);
            startActivity(intent);
        }
    };

    View.OnClickListener fabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, CreateAccount.class);
            intent.putExtra("account", "create");
            startActivityForResult(intent, 0);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.add(0, optionsId, 1, "Настройки");
        menu.add(0, exitId, 2, "Выход");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case optionsId:

                break;
            case exitId:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, editId, 1, "Изменить аккаунт");
        menu.add(0, deleteId, 2, "Удалить аккаунт");
        selectedView = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi;
        switch (item.getItemId()) {
            case editId:
                acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                intent.putExtra("account", "edit");
                intent.putExtra("name", ((TextView) acmi.targetView.findViewById(R.id.textName)).getText().toString());
                startActivityForResult(intent, 0);
                break;
            case deleteId:
                acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteAccount(((TextView) acmi.targetView.findViewById(R.id.textName)).getText().toString());
                adapter.changeCursor(dataBase.getAccountsData());
                break;
        }
        return super.onContextItemSelected(item);
    }
}
