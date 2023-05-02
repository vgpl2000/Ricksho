package com.example.ricksho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserList> listItems;
    private final Context context;
    private String vnum;

    public UserAdapter(List<UserList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_orders,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        UserList listItem=listItems.get(position);
        holder.u_name.setText(listItem.getUName());
        holder.address.setText(listItem.getAddress());

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    holder.mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                //get uid. if uid matches, get vnum
                                if(holder.mAuth.getCurrentUser().getUid().equals(snapshot1.child("uid").getValue().toString())){
                                    vnum=snapshot1.child("vnum").getValue().toString();
                                    //Toast.makeText(context, "got vnum"+vnum, Toast.LENGTH_SHORT).show();
                                }
                                for(DataSnapshot snapshot2:snapshot1.child("orders").getChildren()){
                                    if(snapshot2.child("name").getValue().equals(holder.u_name.getText().toString())){
                                        String name=snapshot2.child("name").getValue().toString();
                                        String email=snapshot2.child("email").getValue().toString();
                                        //email matches or not
                                        holder.mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot snapshot3:snapshot.getChildren()){
                                                        if(snapshot3.child("fname").getValue().toString().equals(name)){
                                                            if(snapshot3.child("email").getValue().toString().equals(email)){
                                                                //now can accept the order state
                                                                //get user id
                                                                String uid=snapshot3.child("uid").getValue().toString();
                                                                holder.mRef.child(vnum).child("orders").child(uid).child("order_status").setValue("accepted");
                                                                Toast.makeText(context.getApplicationContext(), "Order Accepted!", Toast.LENGTH_SHORT).show();
                                                                holder.btn_cancel.setVisibility(View.GONE);
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

    public void setDriverList(List<UserList> mDriverList) {
        this.listItems = mDriverList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView u_name;
        public TextView address;
        public ImageButton btn_accept;
        public ImageButton btn_cancel;
        FirebaseAuth mAuth;
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference("driver");
        DatabaseReference mRef2=FirebaseDatabase.getInstance().getReference("user");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            u_name=itemView.findViewById(R.id.u_name);
            address=itemView.findViewById(R.id.address);
            btn_accept=itemView.findViewById(R.id.btn_accept);
            btn_cancel=itemView.findViewById(R.id.btn_cancel);
            mAuth=FirebaseAuth.getInstance();
        }
    }
}


