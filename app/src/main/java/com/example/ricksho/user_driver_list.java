package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class user_driver_list extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth;
    DatabaseReference myRef = database.getReference();
    RecyclerView recyclerView;
    ImageButton btn_close;
    TextView txt_gone;
    private MyAdapter mAdapter;

    private double mUserLatitude;
    public FrameLayout loadingView;
    public LottieAnimationView loadingAnimation;
    private double mUserLongitude;
    private List<ListItem> mDriverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_driver_list);
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_close=findViewById(R.id.btn_cancel);
        txt_gone=findViewById(R.id.txt_gone);
        mAdapter=new MyAdapter(mDriverList,this);
        mAuth = FirebaseAuth.getInstance();
        loadingView = findViewById(R.id.loading_view);
        loadingAnimation = findViewById(R.id.loading_animation);


        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(user_driver_list.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //Retrieving users latitude and longitude
        mAuth=FirebaseAuth.getInstance();
        myRef.child("user").child(mAuth.getUid()).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    mUserLatitude = latitude;
                    mUserLongitude = longitude;
                    // Once you have the user's latitude and longitude, you can retrieve the drivers
                    // initialize the mAdapter variable with an empty list
                    mAdapter = new MyAdapter(new ArrayList<>(), user_driver_list.this);
                    recyclerView.setAdapter(mAdapter);
                    retrieveDrivers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void retrieveDrivers() {
        myRef.child("driver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mDriverList = new ArrayList<>();

                    // Loop through the drivers and calculate the distance between the user and each driver
                    for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                        if(driverSnapshot.child("location").exists()){
                            double driverLatitude = driverSnapshot.child("location").child("latitude").getValue(Double.class);
                            double driverLongitude = driverSnapshot.child("location").child("longitude").getValue(Double.class);
                            double distance = calculateDistance(mUserLatitude, mUserLongitude, driverLatitude, driverLongitude);
                            String driverName = driverSnapshot.child("fname").getValue(String.class);

                            // Create a new ListItem object and add it to the driver list
                            ListItem listItem = new ListItem(driverName, String.format("%.2f km", distance));
                            mDriverList.add(listItem);
                        }

                    }
                    mAdapter.setDriverList(mDriverList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        //Toast.makeText(this, "Calculating Distance", Toast.LENGTH_SHORT).show();
        double R = 6371; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();
    }

    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        loadingAnimation.cancelAnimation();
    }
}
