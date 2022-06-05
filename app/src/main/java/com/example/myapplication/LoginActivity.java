package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText textEmail, textPass;
    private Button buttonLogin;
    private TextView intentToRegis;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        boolean login = sharedPreferences.getBoolean("logined", false);
        // false la chua dang nhap
        // true la da dang nhap
        if(login == true){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.text_email_login);
        textPass = findViewById(R.id.text_password_login);
        intentToRegis = findViewById(R.id.intentToRegister);
        buttonLogin = findViewById(R.id.button_login);
        intentToRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logined", true);
                String vlTextUser = textEmail.getText().toString();
                String vlTextPass = textPass.getText().toString();
                editor.putString("pass", vlTextPass);
                editor.apply();

                if(isNullValue(new String[]{vlTextUser, vlTextPass})){
                    Toast.makeText(LoginActivity.this, "Null value", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(vlTextUser, vlTextPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isNullValue(String[] str){
        for(String value : str){
            if(value.equals("")){
                return true;
            }
        }
        return false;
    }
}