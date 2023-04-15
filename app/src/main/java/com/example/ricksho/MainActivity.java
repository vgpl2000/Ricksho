package com.example.ricksho;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button btn_hire,btn_login;
    TextView btn_driver;
    private FirebaseAuth mAuth;


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

        //Initialing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                    //Email verified
                    Toast.makeText(MainActivity.this, "User email verified", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Verify your email id first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}