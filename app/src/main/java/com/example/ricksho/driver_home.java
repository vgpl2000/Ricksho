package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class driver_home extends AppCompatActivity {
    TextView text, switch_text,btn_long;
    Switch aSwitch;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    private UserAdapter mAdapter;
    private List<UserList> mDriverList;
    DatabaseReference mDatabaseReference;
    ValueEventListener mValueEventListener;
    public FrameLayout loadingView;
    public LottieAnimationView loadingAnimation;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        mDriverList = new ArrayList<>();

        // Get a reference to the Realtime Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("driver");

        // Get a reference to the Location Manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        mAuth = FirebaseAuth.getInstance();
        text = findViewById(R.id.textView);
        switch_text = findViewById(R.id.switch_Text);
        aSwitch = findViewById(R.id.mySwitch);
        recyclerView=findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new UserAdapter(mDriverList,this);
        btn_long=findViewById(R.id.btnlong);
        mAdapter = new UserAdapter(new ArrayList<>(), driver_home.this);
        recyclerView.setAdapter(mAdapter);
        loadingView = findViewById(R.id.loading_view);
        loadingAnimation = findViewById(R.id.loading_animation);
        showLoading();


        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }

        if(!aSwitch.isChecked()){
            recyclerView.setVisibility(View.INVISIBLE);
        }


        String targetUID = mAuth.getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("driver");
        Query query = driverRef.orderByChild("uid").equalTo(targetUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    String vnum = driverSnapshot.child("vnum").getValue(String.class);



                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String status=snapshot.child(vnum).child("status").getValue().toString();
                                if(status.equals("Online")){
                                    aSwitch.setChecked(true);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    hideLoading();
                    String name = driverSnapshot.child("fname").getValue(String.class);
                    text.setVisibility(View.VISIBLE);
                    text.setText("Hi " + name + "!");


                    aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                //Switch is ON
                                recyclerView.setVisibility(View.VISIBLE);
                                switch_text.setText("Online");

                                if (ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // Request location permission at runtime if not granted
                                    ActivityCompat.requestPermissions(driver_home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                    return;
                                }

                                mLocationListener = new LocationListener() {
                                    @Override
                                    public void onLocationChanged(@NonNull Location location) {
                                        // Get the user's latitude and longitude coordinates
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();

                                        // Create a Location object with the user's coordinates
                                        Location userLocation = new Location("");
                                        userLocation.setLatitude(latitude);
                                        userLocation.setLongitude(longitude);

                                        // Save the user's location to the Realtime Database
                                        //Toast.makeText(driver_home.this, "Sending Location...", Toast.LENGTH_SHORT).show();

                                        mDatabaseReference.child(vnum).child("status").setValue("Online");
                                        mDatabaseReference.child(vnum).child("location").setValue(userLocation);

                                    }
                                };
                                // Request location updates every 10 seconds
                                if (ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);


                            }else{
                                //Switch is OFF
                                switch_text.setText("Offline");
                                recyclerView.setVisibility(View.INVISIBLE);
                                mLocationManager.removeUpdates(mLocationListener);
                                mDatabaseReference.child(vnum).child("status").setValue("Offline");


                                DatabaseReference reference=mDatabaseReference.child(vnum).child("location");
                                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(driver_home.this, "Driver Offline!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_long.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showLoading();
                Intent intent = new Intent(driver_home.this, driver_home.class);
                startActivity(intent);
                hideLoading();
                return false;
            }
        });

        //For Recycler View
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference().child("driver");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        //inside vnum now
                        String uid1=dataSnapshot.child("uid").getValue().toString();
                        if(uid1.equals(mAuth.getCurrentUser().getUid())){
                            //driver verified
                            //get vnum to enter through vnum
                            String vnum=dataSnapshot.child("vnum").getValue().toString();

                            for(DataSnapshot dataSnapshot1:snapshot.child(vnum).child("orders").getChildren()){
                                String uname=dataSnapshot1.child("name").getValue().toString();
                                Double lat= Double.valueOf(dataSnapshot1.child("location").child("latitude").getValue().toString());
                                Double longi= Double.valueOf(dataSnapshot1.child("location").child("longitude").getValue().toString());
                                //now get address from lat and longi

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String addressLine = address.getAddressLine(0);


                                        UserList listItem = new UserList(uname,addressLine,lat,longi);
                                        mDriverList.add(listItem);
                                        mAdapter.setDriverList(mDriverList);

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

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

    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();
    }

    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        loadingAnimation.cancelAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the location updates and ValueEventListener when the activity is destroyed
        try{
            mLocationManager.removeUpdates(mLocationListener);
            mDatabaseReference.removeEventListener(mValueEventListener);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        // Navigate back to the home screen when the back button is pressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}