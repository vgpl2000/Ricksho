package com.example.ricksho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class driver_home extends AppCompatActivity {
    TextView text;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        mAuth=FirebaseAuth.getInstance();
        text=findViewById(R.id.textView);
        String targetUID = mAuth.getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("driver");
        Query query = driverRef.orderByChild("uid").equalTo(targetUID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot driverSnapshot:snapshot.getChildren()){
                    String name=driverSnapshot.child("fname").getValue(String.class);
                    text.setVisibility(View.VISIBLE);
                    text.setText("Hi "+name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}