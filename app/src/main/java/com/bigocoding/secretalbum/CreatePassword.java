package com.bigocoding.secretalbum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
    }
    public void passwordsignup_button(View view) {
        EditText password = (EditText) findViewById(R.id.signup_password);
        EditText confirm_password = (EditText) findViewById(R.id.signup_confirm_password) ;
        if (password.getText().toString().equals(confirm_password.getText().toString())){
            Toast.makeText(this, "Sucessful", Toast.LENGTH_SHORT).show() ;
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Password and Confirm password are not the same", Toast.LENGTH_SHORT).show() ;

    }
}