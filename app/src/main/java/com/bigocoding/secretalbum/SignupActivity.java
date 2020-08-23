package com.bigocoding.secretalbum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    boolean isFace = false, isVoice = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        OnclickButtonListener();

    }

    private void OnclickButtonListener() {
        ImageView voice = (ImageView) findViewById(R.id.signup_voice);
        ImageView face = (ImageView) findViewById(R.id.signup_face);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this, "Voice Click", Toast.LENGTH_SHORT).show();
                isVoice = true ;
            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this, "Face Click", Toast.LENGTH_SHORT).show();
                isFace = true  ;
            }
        });
    }

    public void onClick_signupassword(View view) {
        Intent createpassword = new Intent (SignupActivity.this, CreatePassword.class);
        startActivity(createpassword) ;
    }
}