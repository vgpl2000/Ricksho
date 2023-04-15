package com.example.ricksho;

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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class driver_login extends AppCompatActivity {
    EditText v_number=findViewById(R.id.v_number);
    EditText fname=findViewById(R.id.f_name);
    EditText lname=findViewById(R.id.l_name);
    EditText email=findViewById(R.id.email);
    String svnum,sfname,slname,smail,spassword;
    Button btn_login=findViewById(R.id.btn_login);

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

        // Set the transformation method to show/hide the password
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        // Get a reference to the password toggle and set a listener
        TextInputLayout textInputLayout = findViewById(R.id.password);

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

                /*
                sfname=fname.getText().toString();
                slname=lname.getText().toString();
                smail=email.getText().toString();
                spassword=editTextPassword.getText().toString();

                 */

                if(svnum.isEmpty()){
                    v_number.setError("Enter a Vehicle Number!");
                }
                else if(!isValidVehicleNumber(svnum)){
                    v_number.setError("Enter a valid Vehicle Number!");
                }else{
                    //if vehicle number is valid, then check already registered or not
                    //get uid and check for the child, if child is available then compare v_numbers
                }


            }
        });
    }
    public boolean isValidVehicleNumber(String vehicleNumber) {
        String pattern = "^[A-Z]{2}[0-9]{1,2}[A-Z]{0,2}[0-9]{4}$";
        return vehicleNumber.matches(pattern);
    }

}