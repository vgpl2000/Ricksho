package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class driver_home extends AppCompatActivity {
    TextView text, switch_text;
    Switch aSwitch;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    ValueEventListener mValueEventListener;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        // Get a reference to the Realtime Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("driver");

        // Get a reference to the Location Manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        text = findViewById(R.id.textView);
        switch_text = findViewById(R.id.switch_Text);
        aSwitch = findViewById(R.id.mySwitch);


        String targetUID = mAuth.getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("driver");
        Query query = driverRef.orderByChild("uid").equalTo(targetUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    String vnum = driverSnapshot.child("vnum").getValue(String.class);

                    String name = driverSnapshot.child("fname").getValue(String.class);
                    text.setVisibility(View.VISIBLE);
                    text.setText("Hi " + name + "!");


                    aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                //Switch is ON
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
                                        Toast.makeText(driver_home.this, "Sending Location...", Toast.LENGTH_SHORT).show();

                                        mDatabaseReference.child(vnum).child("status").setValue("Online");
                                        mDatabaseReference.child(vnum).child("location").setValue(userLocation);

                                    }
                                };
                                // Request location updates every 10 seconds
                                if (ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(driver_home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);






                            }else{
                                //Switch is OFF
                                switch_text.setText("Offline");
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the location updates and ValueEventListener when the activity is destroyed
        mLocationManager.removeUpdates(mLocationListener);
        mDatabaseReference.removeEventListener(mValueEventListener);
    }
}