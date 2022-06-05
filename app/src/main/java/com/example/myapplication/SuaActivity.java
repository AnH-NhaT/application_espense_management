package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SuaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText edtName, edtNote;
    private Button btnCapNhat, btnHuy, btnTroVe, buttonTime;
    private TextView texTime;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private ThuChi thuChi;
    private String indexFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua);

        texTime = findViewById(R.id.text_time);

        edtName = findViewById(R.id.edtName_edit);  /**/
        edtNote = findViewById(R.id.edtNote_edit);

        btnCapNhat = findViewById(R.id.btnCapNhat);
        btnHuy = findViewById(R.id.btnHuy);
        btnTroVe = findViewById(R.id.btnTroVe);
        buttonTime = findViewById(R.id.edit_time);
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        Intent intent = getIntent();   /**/
        thuChi = (ThuChi) intent.getSerializableExtra("ThuChi");
        indexFragment = intent.getStringExtra("indexFragment");
        setDefautForValues();   /**/

        addEvents(thuChi, indexFragment);

    }

    private void setDefautForValues() {
        edtName.setText(thuChi.getName());
        edtNote.setText(thuChi.getNote().replace(",",""));
        texTime.setText(thuChi.getCreateTime());
    }

    private void updateThuChi() {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        String name = edtName.getText().toString();
        String note = format.format(Long.parseLong(edtNote.getText().toString()));
        String time = texTime.getText().toString();
        String createTime = thuChi.getCreateTime();
        if (time.equals(createTime)) {
            update(createTime, name, note);
        } else {
            removeThenUpdate(createTime, time);
        }

    }
    private void removeThenUpdate(String createtime, String time){
        String id = thuChi.getId();
        String name = edtName.getText().toString();
        String crtime = thuChi.getCreateTime();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        String note = format.format(Long.parseLong(edtNote.getText().toString()));
        // remove
        String[] splitTime = createtime.split("-");
        String dayOfMonth = splitTime[0];
        String month = splitTime[1];
        String year = splitTime[2];
        databaseReference = database.getReference(idUser).child(indexFragment);
        databaseReference.child(year).child(month).child(thuChi.getId()).removeValue();
        // add new
        splitTime = time.split("-");
        dayOfMonth = splitTime[0];
        month = splitTime[1];
        year = splitTime[2];
        ThuChi thuChinew = new ThuChi(name, note, time);
        databaseReference.child(year).child(month).child(id).setValue(thuChinew).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SuaActivity.this, "Update completed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SuaActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private void update(String createTime, String name, String note) {
        String[] splitTime = createTime.split("-");
        String dayOfMonth = splitTime[0];
        String month = splitTime[1];
        String year = splitTime[2];
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("note", note);
        databaseReference = database.getReference(idUser).child(indexFragment);
        databaseReference.child(year).child(month).child(thuChi.getId())
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override

            public void onSuccess(Void aVoid) {
                Toast.makeText(SuaActivity.this, "Edited", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addEvents(ThuChi thuChi, String indexFragment) {
        btnTroVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefautForValues();
            }
        });

        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateThuChi();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentTime = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.getTime());
        texTime.setText(currentTime);
    }
}