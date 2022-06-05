package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SuaActivity;
import com.example.myapplication.ThemActivity;
import com.example.myapplication.model.ThuChi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ThuChiAdapter extends RecyclerView.Adapter<ThuChiAdapter.ViewHolder> {

    private ArrayList<ThuChi> thuChiArrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String firebaseAuth = FirebaseAuth.getInstance().getUid();
    private DatabaseReference databaseReference;
    public void setThuChiArrayList(ArrayList<ThuChi> thuChiArrayList) {
        this.thuChiArrayList = thuChiArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recyclerview, parent, false); /*k cho tuong tac vs root*/
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Context mContext = holder.txtName.getContext();
        final ThuChi thuChi = thuChiArrayList.get(position);
        Log.e("ThuChiAdapter", thuChi.toString());
        holder.txtName.setText("  Names:  "+ thuChi.getName());
        holder.txtNote.setText("  Money:  "+ thuChi.getNote()+" $");
        holder.txtFilterDay.setText(thuChi.getCreateTime());
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext,view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.item_them){

                            Intent intent = new Intent(mContext, ThemActivity.class);
                            mContext.startActivity(intent);

                        }

                        else if (menuItem.getItemId() == R.id.item_sua){
                            Intent intent = new Intent(mContext, SuaActivity.class);
                            intent.putExtra("ThuChi", thuChi);
                            if(MainActivity.getIndexFragment() == MainActivity.KHOANTHU){
                                intent.putExtra("indexFragment", "khoanthu");

                            } else if(MainActivity.getIndexFragment() == MainActivity.KHOANCHI){
                                intent.putExtra("indexFragment", "khoanchi");

                            }
                            mContext.startActivity(intent);
                        }

                        else if (menuItem.getItemId() == R.id.item_xoa){
//
                            String createTime = thuChi.getCreateTime();
                            String[] splitTime = createTime.split("-");
                            String dayOfMonth = splitTime[0];
                            String month = splitTime[1];
                            String year = splitTime[2];
                            if(MainActivity.getIndexFragment() == MainActivity.KHOANTHU){
                                Log.e("Adapter", "khoanthu" + thuChi.getId());
                                databaseReference = database.getReference(firebaseAuth).child("khoanthu");
                                databaseReference.child(year).child(month).child(thuChi.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if(MainActivity.getIndexFragment() == MainActivity.KHOANCHI){
                                Log.e("Adapter", "khoanchi" + thuChi.getId());
                                databaseReference = database.getReference(firebaseAuth).child("khoanchi");
                                databaseReference.child(year).child(month).child(thuChi.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        return false;
                    }
                });

                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                try {
                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    Object popUpMenuHelper = field.get(popupMenu);
                    Class<?> cls = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
                    Method method = cls.getDeclaredMethod("setForceShowIcon",new Class[]{boolean.class});
                    method.setAccessible(true);
                    method.invoke(popUpMenuHelper,new Object[]{true});
                }catch (Exception e){
                    Log.d("MYTAG", "onClick: "  + e.toString());
                }
                popupMenu.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return thuChiArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtNote;
        private TextView txtFilterDay;
        private ImageView btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFilterDay = itemView.findViewById(R.id.filter_day);
            txtName = itemView.findViewById(R.id.txtName);
            txtNote = itemView.findViewById(R.id.txtNote);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }



}
