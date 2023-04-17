package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class user_login extends AppCompatActivity {
    Button btn_login;
    EditText email,fname,lname;
    String txtemail,txtfname,txtlname,txtpass;
    private Timer timer;
    TextInputEditText editTextPassword;
    TextInputLayout textInputLayout;

    private FirebaseAuth mAuth;
    public FrameLayout loadingView;
    public LottieAnimationView loadingAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //Functions to display views
        email=findViewById(R.id.email);
        fname=findViewById(R.id.f_name);
        lname=findViewById(R.id.l_name);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayout = findViewById(R.id.password);

        //Initialing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loadingView = findViewById(R.id.loading_view);
        loadingAnimation = findViewById(R.id.loading_animation);

        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }


        FrameLayout loadingView = findViewById(R.id.loading_view);
        LottieAnimationView loadingAnimation = findViewById(R.id.loading_animation);

        btn_login=findViewById(R.id.btn_login);

        show_email();
        hide_names();
        hide_Password();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtemail=email.getText().toString().trim();
                if(txtemail.isEmpty()){
                    email.setError("Please enter a Email!");
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtemail).matches()){
                    email.setError("Please enter a valid Email!");
                }
                else{
                    // Show the loading view
                    showLoading();
                    //mail is available in proper format and now check user is created already or not
                    //signing in
                    FirebaseUser user;
                    user= mAuth.getCurrentUser();
                    if(userExist_A(user)){

                        userExist_R(txtemail, new UserExistCallback() {
                            @Override
                            public void onUserExist(boolean exist) {
                                if (exist&&mAuth.getCurrentUser()!=null) {
                                    // User exists
                                    //when current user already created in firebase realtime database then sign in...
                                    //check user with that email id exists or not
                                    mAuth.fetchSignInMethodsForEmail(txtemail)
                                            .addOnCompleteListener(user_login.this, new OnCompleteListener<SignInMethodQueryResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                                    if(task.isSuccessful()){
                                                        //if there exists a user with that mail id
                                                        // Hide the loading view
                                                        loadingView.setVisibility(View.GONE);
                                                        loadingAnimation.cancelAnimation();

                                                        textInputLayout.setVisibility(View.VISIBLE);
                                                        editTextPassword.setVisibility(View.VISIBLE);
                                                        txtpass= editTextPassword.getText().toString();
                                                        if(txtpass.isEmpty()){
                                                            editTextPassword.setError("Enter Password!");
                                                        }else{
                                                            //now sign in the user
                                                            // Show the loading view
                                                            loadingView.setVisibility(View.VISIBLE);
                                                            loadingAnimation.playAnimation();

                                                            mAuth.signInWithEmailAndPassword(txtemail,txtpass)
                                                                    .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if(task.isSuccessful()){
                                                                                // Hide the loading view
                                                                                loadingView.setVisibility(View.GONE);
                                                                                loadingAnimation.cancelAnimation();

                                                                                //FINAL for registered user

                                                                                Toast.makeText(user_login.this, "User signed In", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else {
                                                                                // Hide the loading view
                                                                                loadingView.setVisibility(View.GONE);
                                                                                loadingAnimation.cancelAnimation();

                                                                                Toast.makeText(user_login.this, "Authentication failed in sign in",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }else{
                                                        //if not give error message
                                                        // Hide the loading view
                                                        loadingView.setVisibility(View.GONE);
                                                        loadingAnimation.cancelAnimation();

                                                        Toast.makeText(user_login.this, "Sign In Failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            }
                        });

                    }else{
                        // User does not exist
                        //CREATING ACCOUNT
                        //user doesn't already exists, then create new
                        //if there doesn't exist any user already with email, creating new user account
                        // Hide the loading view
                        hideLoading();
                        //create user with mail and password
                        show_Password();
                        txtpass=editTextPassword.getText().toString();
                        if(txtpass.isEmpty()){
                            editTextPassword.setError("Password cannot be Empty!");
                        }
                        else {
                            showLoading();

                            mAuth.createUserWithEmailAndPassword(txtemail, txtpass)
                                    .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Toast.makeText(user_login.this, "User created!", Toast.LENGTH_SHORT).show();
                                                hideLoading();
                                                show_names();
                                                txtfname = fname.getText().toString();
                                                txtlname = lname.getText().toString();
                                                txtpass=editTextPassword.getText().toString();
                                                if (txtfname.isEmpty()) {
                                                    fname.setError("First Name cannot be Empty!");
                                                } else if (txtlname.isEmpty()) {
                                                    lname.setError("Last Name cannot be Empty!");
                                                } else {
                                                    //if all data are given then create user account in realtime
                                                    //Adding to realtime database
                                                    String uid = user.getUid();
                                                    addToReal_User(uid,txtemail,txtfname,txtlname,txtpass);
                                                }
                                                //send verification email after verifying then proceed
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isComplete()) {
                                                            Toast.makeText(user_login.this, "Verification link is sent to your email address...", Toast.LENGTH_SHORT).show();


                                                            //adding timer to check every 2 seconds that email is verified or not
                                                            timer = new Timer();
                                                            timer.schedule(new TimerTask() {
                                                                @Override
                                                                public void run() {
                                                                    user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            if (useremailVerify(user)) {
                                                                                //once user is verified, cancel the timer
                                                                                timer.cancel();
                                                                                // Hide the loading view
                                                                                hideLoading();
                                                                                //mail verified then display next

                                                                                //FINAL for New User

                                                                                show_toast("Email Verified!");
                                                                                hideLoading();
                                                                                show_names();
                                                                                txtfname = fname.getText().toString();
                                                                                txtlname = lname.getText().toString();
                                                                                txtpass=editTextPassword.getText().toString();
                                                                                if (txtfname.isEmpty()) {
                                                                                    fname.setError("First Name cannot be Empty!");
                                                                                } else if (txtlname.isEmpty()) {
                                                                                    lname.setError("Last Name cannot be Empty!");
                                                                                } else {
                                                                                    //if all data are given then create user account in realtime
                                                                                    //Adding to realtime database
                                                                                    String uid = user.getUid();
                                                                                    addToReal_User(uid,txtemail,txtfname,txtlname,txtpass);
                                                                                }

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
                                                        } else {
                                                            show_toast("Error in sending mail to"+txtemail);
                                                        }

                                                    }
                                                });
                                            } else {
                                                hideLoading();
                                                show_toast("Failed to Register the User!");
                                            }
                                        }
                                    });

                        }

                    }


            }
        };
    });

    }
    //Function to add data to realtime database
    public static boolean addToReal_User(String uid,String email,String fname,String lname, String password){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        String[] parts = email.split("@");
        String username = parts[0];
        DatabaseReference reference=firebaseDatabase.getReference("user").child(username);
        reference.child("uid").setValue(uid);
        reference.child("email").setValue(email);
        reference.child("fname").setValue(fname);
        reference.child("lname").setValue(lname);
        reference.child("password").setValue(password);
        return true;
    }
    //Function to return true or false if user exists in Realtime database
    public interface UserExistCallback {
        void onUserExist(boolean exist);
    }

    public static void userExist_R(String email, UserExistCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] parts = email.split("@");
                String username = parts[0];

                if (snapshot.hasChild(username)) {
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
    public static boolean userExist_A(FirebaseUser user){
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