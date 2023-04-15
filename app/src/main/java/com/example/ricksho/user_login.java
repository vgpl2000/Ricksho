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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class user_login extends AppCompatActivity {
    Button btn_login;
    EditText email,f_name,l_name;
    String txtemail,txtfname,txtlname,txtpass;
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

        btn_login=findViewById(R.id.btn_login);
        email=findViewById(R.id.email);
        f_name=findViewById(R.id.f_name);
        l_name=findViewById(R.id.l_name);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtemail=email.getText().toString().trim();
                txtfname=f_name.getText().toString();
                txtlname=l_name.getText().toString();
                txtpass=editTextPassword.getText().toString();
                if(txtemail.isEmpty()){
                    email.setError("Please enter a Email!");
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtemail).matches()){
                    email.setError("Please enter a valid Email!");
                }
                else if(txtfname.isEmpty()){
                    f_name.setError("First Name cannot be Empty!");
                }
                else if(txtlname.isEmpty()){
                    l_name.setError("Last Name cannot be Empty!");
                }
                else if(txtpass.isEmpty()){
                    editTextPassword.setError("Password cannot be Empty!");
                }
                else{
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    if(currentUser!=null){
                        mAuth.signInWithEmailAndPassword(txtemail,txtpass)
                                .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(user_login.this, "User signed In", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(user_login.this, "Authentication failed in sign in",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        mAuth.createUserWithEmailAndPassword(txtemail,txtpass)
                                .addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user=mAuth.getCurrentUser();
                                    Toast.makeText(user_login.this, "User signed In", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(user_login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}