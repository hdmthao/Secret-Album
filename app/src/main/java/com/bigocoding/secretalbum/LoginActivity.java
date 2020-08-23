package com.bigocoding.secretalbum;

import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Log.d("MyString", "123");
        OnclickButtonListener();
    }

    public void OnclickButtonListener() {

        ImageView voice = (ImageView) findViewById(R.id.imgview_voice);
        ImageView face = (ImageView) findViewById(R.id.imgview_face);
        Button btnSignUp = (Button) findViewById(R.id.button_signup);

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
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");
                Log.d(TAG, "CALL");
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