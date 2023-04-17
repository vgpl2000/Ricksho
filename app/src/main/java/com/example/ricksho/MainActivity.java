package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
                        hideLoading();
                        //Signed in user is Driver
                        Intent intent = new Intent(MainActivity.this, driver_home.class);
                        startActivity(intent);

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
                //Verify that email verified or not
                if (mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(MainActivity.this, user_login.class);
                    startActivity(intent);
                }else{


                    //Hiring processes




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

}