package com.example.ricksho;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_drivers,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItem=listItems.get(position);
        holder.d_name.setText(listItem.getName());
        holder.d_distance.setText(listItem.getDistance());

        holder.cdview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get fname and search for it in the database, upon match,get his uid from child and add orders
                String ex_fname=holder.d_name.getText().toString();
                //Getting user's name and location from database.
                String uid_user=holder.mAuth.getUid();


                holder.mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Map<String, Object> data = (Map<String, Object>) snapshot.child(uid_user).child("location").getValue();
                            String fname=snapshot.child(uid_user).child("fname").getValue().toString();




                            holder.mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot dsnapshot: snapshot.getChildren()){
                                            String d_fname=dsnapshot.child("fname").getValue().toString();
                                            if(d_fname.equals(ex_fname)){
                                                //found driver selected by user. now get vnum from it's child
                                                String vnum=dsnapshot.child("vnum").getValue().toString();
                                                holder.mRef.child(vnum).child("orders").child(uid_user).child("name").setValue(fname);
                                                holder.mRef.child(vnum).child("orders").child(uid_user).child("location").setValue(data);
                                                //make already ordered so that one user cannot order again
                                                holder.mRef.child(vnum).child("orders").child(uid_user).child("order_status").setValue("ordered");
                                                String email=holder.mAuth.getCurrentUser().getEmail().toString();
                                                holder.mRef.child(vnum).child("orders").child(uid_user).child("email").setValue(email);
                                                Toast.makeText(view.getContext(), "Informing "+ex_fname, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context, MainActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView d_name;
        public TextView d_distance;
        private CardView cdview;
        DatabaseReference mRef,mRef2;
        FirebaseAuth mAuth;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            d_name=itemView.findViewById(R.id.d_name);
            d_distance=itemView.findViewById(R.id.d_distance);
            cdview=itemView.findViewById(R.id.cdview);
            mRef=FirebaseDatabase.getInstance().getReference("driver");
            mRef2=FirebaseDatabase.getInstance().getReference("user");
            mAuth = FirebaseAuth.getInstance();

        }
    }
    public void setDriverList(List<ListItem> driverList) {
        this.listItems = driverList;
        notifyDataSetChanged();
    }

}
