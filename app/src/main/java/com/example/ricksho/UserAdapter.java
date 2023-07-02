package com.example.ricksho;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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



        //when order is accepted already....
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
                                                String status=snapshot2.child("order_status").getValue().toString();
                                                if(status.equals("accepted")){
                                                    holder.btn_cancel.setVisibility(View.GONE);
                                                    holder.btn_accept.setVisibility(View.GONE);
                                                    holder.btn_deli.setVisibility(View.VISIBLE);
                                                }

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


        //Until here code loads button inside single row

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Ricksho");
                builder.setMessage("Are you sure you want to ACCEPT this order?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Yes, proceed with order

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
                                                                holder.btn_accept.setVisibility(View.GONE);
                                                                holder.btn_deli.setVisibility(View.VISIBLE);
                                                                //Just to reload activity
                                                                Intent intent = new Intent(context, driver_home.class);
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();






            }
        });

        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Ricksho");
                builder.setMessage("Are you sure you want to CANCEL this order?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                holder.mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            //get uid. if uid matches, get vnum
                                            if (holder.mAuth.getCurrentUser().getUid().equals(snapshot1.child("uid").getValue().toString())) {
                                                vnum = snapshot1.child("vnum").getValue().toString();
                                                //Toast.makeText(context, "got vnum"+vnum, Toast.LENGTH_SHORT).show();
                                            }
                                            for (DataSnapshot snapshot2 : snapshot1.child("orders").getChildren()) {
                                                if (snapshot2.child("name").getValue().equals(holder.u_name.getText().toString())) {
                                                    String name = snapshot2.child("name").getValue().toString();
                                                    String email = snapshot2.child("email").getValue().toString();
                                                    //email matches or not
                                                    holder.mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot snapshot3 : snapshot.getChildren()) {
                                                                if (snapshot3.child("fname").getValue().toString().equals(name)) {
                                                                    if (snapshot3.child("email").getValue().toString().equals(email)) {
                                                                        //now can accept the order state
                                                                        //get user id
                                                                        String uid = snapshot3.child("uid").getValue().toString();
                                                                        //delete child of orders: uid directly
                                                                        holder.mRef.child(vnum).child("orders").child(uid).removeValue();
                                                                        Toast.makeText(context, "Order Cancelled!", Toast.LENGTH_SHORT).show();
                                                                        //Just to reload activity
                                                                        Intent intent = new Intent(context, driver_home.class);
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        holder.btn_deli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Ricksho");
                builder.setMessage("Are you sure you want to COMPLETE this order?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                holder.mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            //get uid. if uid matches, get vnum
                                            if (holder.mAuth.getCurrentUser().getUid().equals(snapshot1.child("uid").getValue().toString())) {
                                                vnum = snapshot1.child("vnum").getValue().toString();
                                                //Toast.makeText(context, "got vnum"+vnum, Toast.LENGTH_SHORT).show();
                                            }
                                            for (DataSnapshot snapshot2 : snapshot1.child("orders").getChildren()) {
                                                if (snapshot2.child("name").getValue().equals(holder.u_name.getText().toString())) {
                                                    String name = snapshot2.child("name").getValue().toString();
                                                    String email = snapshot2.child("email").getValue().toString();
                                                    //email matches or not
                                                    holder.mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot snapshot3 : snapshot.getChildren()) {
                                                                if (snapshot3.child("fname").getValue().toString().equals(name)) {
                                                                    if (snapshot3.child("email").getValue().toString().equals(email)) {
                                                                        //now can accept the order state
                                                                        //get user id
                                                                        String uid = snapshot3.child("uid").getValue().toString();
                                                                        //delete child of orders: uid directly
                                                                        holder.mRef.child(vnum).child("orders").child(uid).removeValue();
                                                                        Toast.makeText(context, "Thanks for the service!", Toast.LENGTH_SHORT).show();
                                                                        //Just to reload activity
                                                                        Intent intent = new Intent(context, driver_home.class);
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        //To set direction in google map installed in the phone
        holder.btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mRef.child(vnum).child("status").equals("Offline")){
                    Toast.makeText(context, "You must be online to Set Direction...", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Get the latitude and longitude of source and destination from database

                    holder.mRef.child(vnum).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // Get the source and destination latitude and longitude
                            double sourceLatitude;
                            double sourceLongitude;

                            double destinationLatitude;
                            double destinationLongitude;

                            sourceLatitude= (double) snapshot.child("location").child("latitude").getValue();
                            sourceLongitude= (double) snapshot.child("location").child("longitude").getValue();

                            //Destination i.e users location
                            destinationLatitude= listItem.getU_latitude();
                            destinationLongitude= listItem.getU_longitude();

                            //Pass them to code which opens google map
                            // Create a Uri object for the Google Maps intent
                            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + sourceLatitude + "," + sourceLongitude + "&destination=" + destinationLatitude + "," + destinationLongitude);

                            // Create an intent with the Uri
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                            // Set the package to ensure only the Google Maps app responds to the intent
                            mapIntent.setPackage("com.google.android.apps.maps");

                            // Check if there's a suitable app available to handle the intent
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                // Start the intent to open Google Maps
                                context.startActivity(mapIntent);
                            } else {
                                // Handle the case where Google Maps app is not installed
                                Toast.makeText(context.getApplicationContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }












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
        public ImageButton btn_deli;
        public UserAdapter mAdapter;
        public Button btn_set;
        FirebaseAuth mAuth;
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference("driver");
        DatabaseReference mRef2=FirebaseDatabase.getInstance().getReference("user");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            u_name=itemView.findViewById(R.id.u_name);
            address=itemView.findViewById(R.id.address);
            btn_accept=itemView.findViewById(R.id.btn_accept);
            btn_cancel=itemView.findViewById(R.id.btn_cancel);
            btn_deli=itemView.findViewById(R.id.btn_deli);
            btn_set=itemView.findViewById(R.id.btnset);
            mAuth=FirebaseAuth.getInstance();
        }
    }
}


