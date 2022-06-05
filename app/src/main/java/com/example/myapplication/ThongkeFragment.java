package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.ThuChi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ThongkeFragment extends Fragment {
    private long sumKhoangThu = 0;
    private long sumKhoangChi = 0;
    private long canDoi = 0;
    private TextView tvKhoangThu;
    private TextView tvKhoangChi;
    private TextView tvCanDoi;
    private TextView tvNgay;
    private TextView tongket;
    private RecyclerView recyclerView;
    private Spinner spinner_months;
    private Spinner spinner_years;
    private NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    // date of realtime
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    String[] dateSplit = date.split("-");
    String day_of_month = dateSplit[0];
    String month = dateSplit[1];
    String year = dateSplit[2];
    String[] monthFilter = {"", month};
    String[] yearFilter = {"", year};
    private String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayList<String> filter_array_years = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thongke, container, false);
        tvKhoangChi = view.findViewById(R.id.khoangchi);
        tvKhoangThu = view.findViewById(R.id.khoangthu);
        tvCanDoi = view.findViewById(R.id.candoi);
        tvNgay = view.findViewById(R.id.ngay);
        tongket = view.findViewById(R.id.tongket);
        tongket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTongKet();
            }
        });
        addYearFilter(2009);
        spinner_months = view.findViewById(R.id.spinner_months);
        spinner_years = view.findViewById(R.id.spinner_years);
        ArrayAdapter<CharSequence> arrayAdapterFilderMonths = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_array_days, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> arrayAdapterFilderYears = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, filter_array_years);

        arrayAdapterFilderMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterFilderYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_months.setAdapter(arrayAdapterFilderMonths);
        spinner_years.setAdapter(arrayAdapterFilderYears);

        spinner_months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthFilter = parent.getItemAtPosition(position).toString().split(" ");
                setUpValue(monthFilter[1], yearFilter[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearFilter = parent.getItemAtPosition(position).toString().split(" ");
                setUpValue(monthFilter[1], yearFilter[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    private void addYearFilter(int i) {
        for (int yearIndex = i; yearIndex <= Integer.valueOf(year); yearIndex++) {
            filter_array_years.add("Year " + yearIndex);
        }
    }

    private void setNullForAllView() {
        tvKhoangChi.setText("0");
        tvKhoangThu.setText("0");
        tvCanDoi.setText("0");
    }

    private void setUpTongKet() {
        sumKhoangChi = 0;
        sumKhoangThu = 0;
        canDoi = 0;
        databaseReference = database.getReference(idUser).child("khoanthu");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot year : snapshot.getChildren()) {
                    for (DataSnapshot month : year.getChildren()) {
                        for (DataSnapshot item : month.getChildren()) {
                            ThuChi thuChi = item.getValue(ThuChi.class);
                            if (thuChi != null) {
                                String money = thuChi.getNote().replace(",", "");
                                sumKhoangThu += Long.valueOf(money);
                            }
                        }
                    }
                }
                canDoi = sumKhoangThu - sumKhoangChi;
                tvKhoangThu.setText(format.format(sumKhoangThu) +" $");
                tvCanDoi.setText(format.format(canDoi) +" $");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference = database.getReference(idUser).child("khoanchi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot year : snapshot.getChildren()) {
                    for (DataSnapshot month : year.getChildren()) {
                        for (DataSnapshot item : month.getChildren()) {
                            ThuChi thuChi = item.getValue(ThuChi.class);
                            if (thuChi != null) {
                                String money = thuChi.getNote().replace(",", "");
                                sumKhoangChi += Long.valueOf(money);
                            }
                        }
                    }
                }
                canDoi = sumKhoangThu - sumKhoangChi;
                tvKhoangChi.setText(format.format(sumKhoangChi) +" $");

                tvCanDoi.setText(format.format(canDoi) + " $");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpValue(String monthFilter, String yearFilter) {

        tvNgay.setText(month);
        sumKhoangChi = 0;
        sumKhoangThu = 0;
        canDoi = 0;
        databaseReference = database.getReference(idUser).child("khoanthu").child(yearFilter).child(monthFilter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ThuChi thuChi = data.getValue(ThuChi.class);
                    if (thuChi != null) {
                        String money = thuChi.getNote().replace(",", "");
                        sumKhoangThu += Long.valueOf(money);
                    }
                }
                canDoi = sumKhoangThu - sumKhoangChi;
                tvKhoangThu.setText(format.format(sumKhoangThu)+" $");
                tvCanDoi.setText(format.format(canDoi) + " $");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("MYTAG", "onCancelled: " + databaseError.toString());

            }
        });

        databaseReference = database.getReference(idUser).child("khoanchi").child(yearFilter).child(monthFilter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ThuChi thuChi = data.getValue(ThuChi.class);
                    if (thuChi != null) {
                        String money = thuChi.getNote().replace(",", "");
                        sumKhoangChi += Long.valueOf(money);
                    }
                }
                canDoi = sumKhoangThu - sumKhoangChi;
                tvKhoangChi.setText(format.format(sumKhoangChi) +" $");
                tvCanDoi.setText(format.format(canDoi)+" $");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("MYTAG", "onCancelled: " + databaseError.toString());

            }
        });

    }
}
