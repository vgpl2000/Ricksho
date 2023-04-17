package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class driver_login extends AppCompatActivity {
    EditText v_number,fname,lname,email;

    String svnum,sfname,slname,smail,spassword;
    TextInputEditText editTextPassword;
    TextInputLayout textInputLayout;
    Timer timer;
    private FirebaseAuth mAuth;
    public FrameLayout loadingView;
    public LottieAnimationView loadingAnimation;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }

        v_number=findViewById(R.id.v_number);
       fname=findViewById(R.id.f_name);
       lname=findViewById(R.id.l_name);
       email=findViewById(R.id.email);
        btn_login=findViewById(R.id.btn_login);

        mAuth=FirebaseAuth.getInstance();

        // Set the transformation method to show/hide the password
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        // Get a reference to the password toggle and set a listener
        textInputLayout = findViewById(R.id.password);
        loadingView = findViewById(R.id.loading_view);
        loadingAnimation = findViewById(R.id.loading_animation);

        //made invisible to enter vehicle number
        fname.setVisibility(View.GONE);
        lname.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    editTextPassword.setTransformationMethod(null);
                } else {
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                svnum=v_number.getText().toString().toUpperCase().trim();

                if(svnum.isEmpty()){
                    v_number.setError("Enter a Vehicle Number!");
                }
                else if(!isValidVehicleNumber(svnum)){
                    v_number.setError("Enter a valid Vehicle Number!");
                }else{
                    //if vehicle number is valid, then check already registered or not
                    driverExist_R(svnum, new user_login.UserExistCallback() {
                        @Override
                        public void onUserExist(boolean exist) {
                            if (exist) {
                            //If user already exists in Realtime database
                                hideLoading();
                            show_Password();
                            show_email();
                            smail = email.getText().toString();
                            spassword = editTextPassword.getText().toString();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("driver");
                            showLoading();
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //get password from realtime database
                                    String password = snapshot.child(svnum).child("password").getValue().toString();
                                    //Once got the password from realtime database
                                    hideLoading();

                                    if(smail.isEmpty()){
                                        email.setError("Email cannot be empty!");
                                    }
                                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(smail).matches()){
                                        email.setError("Enter proper Email Id!");
                                    }else {
                                        //compare the password with the entered password
                                        if (spassword.equals(password)) {
                                            //now let to sign in the driver
                                            mAuth.signInWithEmailAndPassword(smail, spassword)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            show_toast("Driver " + smail + " signed in!");
                                                            hideLoading();
                                                            Intent intent = new Intent(driver_login.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });

                                        } else if (spassword != null) {
                                            show_toast("Wrong Password!");
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    show_toast("Failed to access password!");
                                }
                            });

                            }else {
                                //Driver does not exist
                                show_names();
                                show_email();
                                show_Password();
                                sfname = fname.getText().toString();
                                slname = lname.getText().toString();
                                smail = email.getText().toString();
                                spassword = editTextPassword.getText().toString();
                                if (sfname.isEmpty()) {
                                    fname.setError("First name is necessary");
                                } else if (slname.isEmpty()) {
                                    lname.setError("Last name is necessary");
                                } else if (smail.isEmpty()) {
                                    email.setError("Email is necessary");
                                } else if (spassword.isEmpty()) {
                                    editTextPassword.setError("Password is necessary");
                                } else {

                                showLoading();
                                mAuth.createUserWithEmailAndPassword(smail, spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //driver is created in Auth with mail and password
                                            //add to realtime database
                                            addToReal_Driver(mAuth.getCurrentUser().getUid(), smail, sfname, slname, spassword, svnum);
                                            //send verification mail
                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //adding timer to check every 2 seconds that email is verified or not
                                                    timer = new Timer();
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    timer.schedule(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    if (useremailVerify(user)) {
                                                                        //once user is verified, cancel the timer
                                                                        timer.cancel();

                                                                        //FINAL for New Driver

                                                                        show_toast("Email Verified!");
                                                                        Intent intent = new Intent(driver_login.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                        hideLoading();

                                                                    } else {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                show_toast("Verification link sent to your mail id...");
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });

                                                        }
                                                    }, 1, 3000);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            }
                        }
                    });

                }


            }
        });
    }
    public boolean isValidVehicleNumber(String vehicleNumber) {
        String pattern = "^[A-Z]{2}[0-9]{1,2}[A-Z]{0,2}[0-9]{0,4}$";
        return vehicleNumber.matches(pattern);
    }

    //Function to add DRIVER'S data to realtime database
    public static boolean addToReal_Driver(String uid,String email,String fname,String lname, String password,String vnum){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference reference=firebaseDatabase.getReference("driver").child(vnum);
        reference.child("uid").setValue(uid);
        reference.child("vnum").setValue(vnum);
        reference.child("email").setValue(email);
        reference.child("fname").setValue(fname);
        reference.child("lname").setValue(lname);
        reference.child("password").setValue(password);
        return true;
    }


    public interface UserExistCallback {
        void onUserExist(boolean exist);
    }

    public static void driverExist_R(String vnum, user_login.UserExistCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("driver");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(vnum)) {
                    callback.onUserExist(true);
                } else {
                    callback.onUserExist(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    //Function to return true if user exists in Google Auth
    public static boolean driverExist_A(FirebaseUser user){
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            return true;
        }else{
            return false;
        }
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


    //Function to know user verified email or not
    public static boolean useremailVerify(FirebaseUser user){
        if(user.isEmailVerified()){
            return true;
        }else{
            return false;
        }
    }


    void show_email(){
        email.setVisibility(View.VISIBLE);
    }
    public void hide_email(){
        email.setVisibility(View.GONE);
    }
    public void show_names(){
        fname.setVisibility(View.VISIBLE);
        lname.setVisibility(View.VISIBLE);
    }
    public void hide_names(){
        fname.setVisibility(View.GONE);
        lname.setVisibility(View.GONE);
    }
    public void show_Password(){
        textInputLayout.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
    }
    public void hide_Password(){
        textInputLayout.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
    }



    public void show_toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}