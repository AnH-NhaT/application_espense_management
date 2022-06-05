package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.model.ThuChi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ThemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText edtName, edtNote;
    private Button btnThem, btnHuy, btnTroVe, btnCreateTime;
    private TextView contentCreateTime;
    int indexFragment;
    private String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("khoanthu");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);
        Intent intent = getIntent();
        indexFragment = intent.getIntExtra("indexFragment", -1);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnCreateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtName.setText("");
                edtNote.setText("");

            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            String[] dateSplit;
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
                String note = format.format(Long.parseLong(edtNote.getText().toString()));
                String date = contentCreateTime.getText().toString().trim();
                dateSplit = date.split("-");
                if (date.equals("")){
                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    dateSplit = date.split("-");
                }
                String day_of_month = dateSplit[0];
                String month = dateSplit[1];
                String year = dateSplit[2];
                ThuChi thuChi = new ThuChi(name, note, date);

                if (indexFragment == MainActivity.KHOANTHU){
                    databaseReference = database.getReference(idUser).child("khoanthu");
                } else if(indexFragment == MainActivity.KHOANCHI){
                    databaseReference = database.getReference(idUser).child("khoanchi");
                }
                String id = databaseReference.push().getKey();
                databaseReference.child(year).child(month).child(id).setValue(thuChi).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Add to success", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }


                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Add to fail" + e.toString(), Toast.LENGTH_LONG).show();
                                            }

                                        }
                );

            }
        });
    }


    private void addControls() {

        edtName = findViewById(R.id.edtName_add);
        edtNote = findViewById(R.id.edtNote_add);
        btnThem = findViewById(R.id.btnThem);
        btnHuy = findViewById(R.id.btnHuy);
        btnTroVe = findViewById(R.id.btnTroVe);
        btnCreateTime = findViewById(R.id.creat_time);
        contentCreateTime = findViewById(R.id.content_create_time);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());
        contentCreateTime.setText(currentTime);
    }
}