package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements Dialog.DialogListener {
    FloatingActionButton floatingActionButton;
    public static final int KHOANTHU = 1;
    public static final int KHOANCHI = 2;
    public static final int THONGKE = 3;
    static int indexFragment = 1;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(firebaseAuth.getCurrentUser().getEmail().toString() != null){
            getSupportActionBar().setTitle(firebaseAuth.getCurrentUser().getEmail().toString());
        }
        floatingActionButton = (FloatingActionButton) findViewById(R.id.item_them);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexFragment != THONGKE){
                    Intent intent = new Intent(getApplicationContext(), ThemActivity.class);
                    intent.putExtra("indexFragment", indexFragment);
                    startActivity(intent);
                } else {
                    firebaseAuth.signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("logined", false);
                    editor.putString("pass", "null");
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }});

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace
                (R.id.fragment_container,new KhoanthuFragment()).commit();


    }

    public static int getIndexFragment() {
        return indexFragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_khoanthu:
                    indexFragment = KHOANTHU;
                    floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24);
                    selectedFragment = new KhoanthuFragment();
                    break;

                case R.id.nav_khoanchi:
                    indexFragment = KHOANCHI;
                    floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24);
                    selectedFragment = new KhoanchiFragment();
                    break;

                case R.id.nav_thongke:
                    indexFragment = THONGKE;
                    floatingActionButton.setImageResource(R.drawable.ic_logout);
                    selectedFragment = new ThongkeFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.fragment_container,selectedFragment).commit();

            return (true);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_acc:
                Dialog dialog = new Dialog();
                dialog.show(getSupportFragmentManager(), "Confirm delete");

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dialogConfirmEvent() {    /**/
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String pass = sharedPreferences.getString("pass","null");
        credential = EmailAuthProvider.getCredential(firebaseAuth.getCurrentUser().getEmail(), pass);
        firebaseAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Deleted account", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("logined", false);
                            editor.putString("pass", "null");
                            editor.apply();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Xoa", task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }
}