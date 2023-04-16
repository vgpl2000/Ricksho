package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class user_login extends AppCompatActivity {
    Button btn_login;
    EditText email,f_name,l_name;
    String txtemail,txtfname,txtlname,txtpass;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference reference=firebaseDatabase.getReference("user");
    private Timer timer;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //Initialing Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //To change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellow));
        }

        // Set the transformation method to show/hide the password
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        // Get a reference to the password toggle and set a listener
        TextInputLayout textInputLayout = findViewById(R.id.password);
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

        FrameLayout loadingView = findViewById(R.id.loading_view);
        LottieAnimationView loadingAnimation = findViewById(R.id.loading_animation);

        btn_login=findViewById(R.id.btn_login);
        email=findViewById(R.id.email);
        f_name=findViewById(R.id.f_name);
        l_name=findViewById(R.id.l_name);
        f_name.setVisibility(View.GONE);
        l_name.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);

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
                    loadingView.setVisibility(View.VISIBLE);
                    loadingAnimation.playAnimation();
                    //mail is available in proper format and now check user is created already or not
                    //signing in

                            if(mAuth.getCurrentUser()!=null)
                            {
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

                            }else{
                                //user doesn't already exists, then create new
                                //if there doesn't exist any user already with email, creating new user account
                                // Hide the loading view
                                loadingView.setVisibility(View.GONE);
                                loadingAnimation.cancelAnimation();

                                //create user with mail and password
                                textInputLayout.setVisibility(View.VISIBLE);
                                editTextPassword.setVisibility(View.VISIBLE);
                                txtpass=editTextPassword.getText().toString();
                                if(txtpass.isEmpty()){
                                    editTextPassword.setError("Password cannot be Empty!");
                                }
                                else {
                                    loadingView.setVisibility(View.VISIBLE);
                                    loadingAnimation.playAnimation();

                                    mAuth.createUserWithEmailAndPassword(txtemail, txtpass)
                                            .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        Toast.makeText(user_login.this, "User created!", Toast.LENGTH_SHORT).show();


                                                        //verify email
                                                        //send verification email after verifying then proceed
                                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isComplete()) {

                                                                    //mail is sent, when verified display next
                                                                    //adding timer to check every 2 seconds that email is verified or not
                                                                    timer = new Timer();
                                                                    timer.schedule(new TimerTask() {
                                                                        @Override
                                                                        public void run() {
                                                                            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    if (user.isEmailVerified()) {
                                                                                        //once user is verified, cancel the timer
                                                                                        timer.cancel();
                                                                                        // Hide the loading view
                                                                                        loadingView.setVisibility(View.GONE);
                                                                                        loadingAnimation.cancelAnimation();
                                                                                        //mail verified then display next
                                                                                        f_name.setVisibility(View.VISIBLE);
                                                                                        l_name.setVisibility(View.VISIBLE);
                                                                                        txtfname = f_name.getText().toString();
                                                                                        txtlname = l_name.getText().toString();


                                                                                        if (txtfname.isEmpty()) {
                                                                                            f_name.setError("First Name cannot be Empty!");
                                                                                        } else if (txtlname.isEmpty()) {
                                                                                            l_name.setError("Last Name cannot be Empty!");
                                                                                        } else {
                                                                                            //if all data are given then create user account
                                                                                            // Show the loading view
                                                                                            loadingView.setVisibility(View.VISIBLE);
                                                                                            loadingAnimation.playAnimation();

                                                                                            //Adding to realtime database
                                                                                            String uid = user.getUid();
                                                                                            DatabaseReference ref1 = reference.child(uid);
                                                                                            ref1.child("fname").setValue(txtfname);
                                                                                            ref1.child("lname").setValue(txtlname);
                                                                                            ref1.child("email").setValue(txtemail);
                                                                                            ref1.child("password").setValue(txtpass);
                                                                                        }

                                                                                    } else {
                                                                                        runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(user_login.this, "Verification link sent to your email id!", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }
                                                                            });

                                                                        }
                                                                    }, 1, 3000);
                                                                } else {
                                                                    Toast.makeText(user_login.this, "Failed to send mail to " + txtemail, Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    } else {
                                                        loadingView.setVisibility(View.GONE);
                                                        loadingAnimation.cancelAnimation();

                                                        Toast.makeText(user_login.this, "Failed to register! Try Again", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }

                }
            }
        };
    });

    }
}