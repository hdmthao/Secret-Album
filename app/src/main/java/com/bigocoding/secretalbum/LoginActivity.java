package com.bigocoding.secretalbum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
        }



        setContentView(R.layout.login_layout);
        Log.d("MyString", "123");
        OnclickButtonListener();
    }

    public void OnclickButtonListener() {

        ImageView voice = (ImageView) findViewById(R.id.imgview_voice);
        ImageView face = (ImageView) findViewById(R.id.imgview_face);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Voice Click", Toast.LENGTH_SHORT).show();

            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Face Click", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void signin_button(View view) {
        EditText login_password = (EditText) findViewById(R.id.et_password);
        Log.d("MyString", login_password.getText().toString());
        if (login_password.getText().toString().equals("111111")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else
            Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT).show();

    }



}