package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btn_hire,btn_login;
    TextView btn_driver;
    private FirebaseAuth mAuth;
    public FrameLayout loadingView;
    public LottieAnimationView loadingAnimation;
    DatabaseReference mDatabaseReference;
    DatabaseReference mDatabaseReference1;
    ValueEventListener mValueEventListener;
    LocationManager mLocationManager;
    LocationListener mLocationListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }

        // Get a reference to the Realtime Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("user");
        mDatabaseReference1 = FirebaseDatabase.getInstance().getReference("driver");

        // Get a reference to the Location Manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        loadingView = findViewById(R.id.loading_view);
        loadingAnimation = findViewById(R.id.loading_animation);
        showLoading();


        //Initialing Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //There exists a user
        if(mAuth.getCurrentUser()!=null){
            //It must be checked the user is driver or user in realtime database
            String uid=mAuth.getCurrentUser().getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref1 = firebaseDatabase.getReference("user");
            ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(uid)){
                        //It means the signed in user is user
                        hideLoading();
                        String name=snapshot.child(uid).child("fname").getValue(String.class);
                        btn_driver.setVisibility(View.VISIBLE);
                        btn_driver.setText("Hi "+name+"!");
                        btn_driver.setClickable(false);
                        btn_login.setVisibility(View.GONE);

                    }else{

                        DatabaseReference ref2=firebaseDatabase.getReference("driver");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot snapshot1:snapshot.getChildren()){
                                    if(snapshot1.child("uid").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                        hideLoading();
                                        //Signed in user is Driver
                                        Intent intent = new Intent(MainActivity.this, driver_home.class);
                                        startActivity(intent);
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
        hideLoading();

        btn_hire=findViewById(R.id.btn_hire);
        btn_login=findViewById(R.id.reg_user);
        btn_driver=findViewById(R.id.reg_driver);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, user_login.class);
                startActivity(intent);
            }
        });
        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, driver_login.class);
                startActivity(intent);
            }
        });

        btn_hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClickEffect(btn_hire);
                //Verify that email verified or not
                if (mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(MainActivity.this, user_login.class);
                    startActivity(intent);
                }else{
                    mDatabaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                for(DataSnapshot snapshot1:snapshot.getChildren()){
                                    if(snapshot1.hasChild("orders")&&snapshot1.child("orders").hasChild(mAuth.getCurrentUser().getUid())){
                                        //User ordered from this auto driver
                                        if(snapshot1.child("orders").child(mAuth.getCurrentUser().getUid()).child("order_status").getValue().toString().equals("ordered")){
                                            Toast.makeText(MainActivity.this, "Already ordered!", Toast.LENGTH_SHORT).show();
                                        } else if(snapshot1.child("orders").child(mAuth.getCurrentUser().getUid()).child("order_status").getValue().toString().equals("accepted")){
                                            Toast.makeText(MainActivity.this, "Driver is on the way....", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //Display Activity of Driver List
                                            Intent intent = new Intent(MainActivity.this, user_driver_list.class);
                                            startActivity(intent);
                                        }

                                    }
                                    else{
                                        //Display Activity of Driver List
                                        Intent intent = new Intent(MainActivity.this, user_driver_list.class);
                                        startActivity(intent);
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    showLoading();
                    String uid= mAuth.getUid();


                    //Hiring processes
                    //Saving the Location of the user every 10 secs
                    mLocationListener=new LocationListener() {
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
                            //Toast.makeText(MainActivity.this, "Sending Location...", Toast.LENGTH_SHORT).show();

                            mDatabaseReference.child(uid).child("location").setValue(userLocation);
                            hideLoading();
                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {
                            // GPS provider is enabled
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            // GPS provider is disabled
                        }
                    };

                    // Request location updates every 10 seconds
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Request location permission at runtime if not granted
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }

                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);
                    } else {
                        // GPS provider is disabled
                        Toast.makeText(MainActivity.this, "Please enable GPS to use this feature", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
    //Function to display loading animation

    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();
    }

    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        loadingAnimation.cancelAnimation();
    }

    void addClickEffect(View view)
    {
        Drawable drawableNormal = view.getBackground();

        Drawable drawablePressed = view.getBackground().getConstantState().newDrawable();
        drawablePressed.mutate();
        drawablePressed.setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);

        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable.addState(new int[] {android.R.attr.state_pressed}, drawablePressed);
        listDrawable.addState(new int[] {}, drawableNormal);
        view.setBackground(listDrawable);
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