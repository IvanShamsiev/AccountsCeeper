package com.example.ivan.accountsceeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PassActivity extends AppCompatActivity {

    int attemptsCount = 3;

    EditText editPass;

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        setTitle(R.string.title_pass);

        sPref = getSharedPreferences("prefs", MODE_PRIVATE);
        int passLength;
        String passLengthString = sPref.getString("length", "null");
        if (!passLengthString.equals("null")) passLength = Integer.parseInt(passLengthString);
        else {
            Toast.makeText(this, "Ошибка: не задан пароль" + "\n" + "Новое значение пароля: 12345", Toast.LENGTH_SHORT).show();
            setPass(getSharedPreferences("prefs", MODE_PRIVATE), "12345");
            passLength = 5;
        }

        final int finalPassLength = passLength;

        editPass = findViewById(R.id.editPass);
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= finalPassLength) checkPass(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void checkPass(String pass) {
        if (attemptsCount > 0) {
            attemptsCount--;
            String defaultValue = "Не удалось получить сохранённую строку";
            String savedText = sPref.getString("pass", defaultValue);
            if (!savedText.equals(defaultValue) && savedText.equals(pass)) {
                setResult(0);
                finish();
            } else {
                Toast.makeText(this, "Неверный пароль! Осталось попыток: " + attemptsCount, Toast.LENGTH_SHORT).show();
                editPass.setText("");
            }
        }
    }

    public static void setPass(SharedPreferences sPref, String newPass) {
        SharedPreferences.Editor ePref = sPref.edit();
        ePref.putString("pass", newPass);
        ePref.putString("length", String.valueOf(newPass.length()));
        ePref.apply();
    }

}
