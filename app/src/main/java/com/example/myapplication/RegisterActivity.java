package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText textEmail, textPass, textCfPass;
    private Button buttonRegister;
    private TextView intentToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.text_email_register);
        textPass = findViewById(R.id.text_password_register);
        textCfPass = findViewById(R.id.text_confirm_password_register);
        buttonRegister = findViewById(R.id.button_register);
        intentToLogin = findViewById(R.id.intentToLogin);
        intentToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vlTextUser = textEmail.getText().toString();
                String vlTextPass = textPass.getText().toString();
                String vlTextCfPass = textCfPass.getText().toString();

                if(isNullValue(new String[]{vlTextUser, vlTextPass, vlTextCfPass})){
                    Toast.makeText(RegisterActivity.this, "Null value", Toast.LENGTH_LONG).show();
                } else if(!vlTextPass.equals(vlTextCfPass)){
                    Toast.makeText(RegisterActivity.this, "Password confirm must same", Toast.LENGTH_LONG).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(vlTextUser, vlTextPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Successful register", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

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