package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ThuChiAdapter;
import com.example.myapplication.model.ThuChi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class KhoanthuFragment extends Fragment {

    private RecyclerView recyclerView;
    private final ArrayList<ThuChi> thuChiArrayList = new ArrayList<>();
    private ThuChiAdapter adapter;
    private String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khoanthu,container,false);
        recyclerView  = view.findViewById(R.id.recyclerview_khoanthu);
        adapter = new ThuChiAdapter();
        Log.e("TAGHome","onCreateView");
        GetData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    private void GetData(){
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef  = database.getReference(idUser).child("khoanthu");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thuChiArrayList.clear();
                for (DataSnapshot years : dataSnapshot.getChildren()){
                    for (DataSnapshot months : years.getChildren()){
                        for(DataSnapshot thuchiss : months.getChildren()){
                            ThuChi thuChi = thuchiss.getValue(ThuChi.class);
                            thuChi.setId(thuchiss.getKey());
                            if (thuChi != null){
                                thuChiArrayList.add(thuChi);
                            }
                        }
                    }
                }
                adapter.setThuChiArrayList(thuChiArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("MYTAG", "onCancelled: "+ databaseError.toString());

            }
        });
    }
}
