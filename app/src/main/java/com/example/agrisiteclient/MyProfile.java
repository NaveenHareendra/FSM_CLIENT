package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity {

    String fullName, VSDomainFromDB, userIDFromDB;
    TextView TextViewAdminFullName, TextViewAdminProvince, TextViewAdminDivision, TextViewAdminVSDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //TextView initialization
        TextViewAdminFullName = findViewById(R.id.TextViewAdminFullName);
        TextViewAdminProvince = findViewById(R.id.TextViewAdminProvince);
        TextViewAdminDivision = findViewById(R.id.TextViewAdminDivision);
        TextViewAdminVSDomain = findViewById(R.id.TextViewAdminVSDomain);

        // Retrieve userIDFromDB from intent
        Intent intent = getIntent();
        fullName = intent.getStringExtra("full_name_of_user");

        // Initialize the Firebase Database reference
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Retrieve data from Firebase based on userIDFromDB
        usersRef.orderByChild("full_name_of_user").equalTo(fullName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if (userSnapshot.hasChild("selectedProvince") && userSnapshot.hasChild("selectedVSDomain") && userSnapshot.hasChild("selectedDivision")) {
                            String province_of_admin = userSnapshot.child("selectedProvince").getValue(String.class);
                            String division_of_admin = userSnapshot.child("selectedDivision").getValue(String.class);
                            String VSdomain_of_admin = userSnapshot.child("selectedVSDomain").getValue(String.class);

                            // Set the retrieved data to the respective TextViews
                            TextViewAdminProvince.setText(province_of_admin);
                            TextViewAdminDivision.setText(division_of_admin);
                            TextViewAdminVSDomain.setText(VSdomain_of_admin);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        showUserData();  // Show user data from intent
    }

    public void showUserData() {
        Intent intent = getIntent();

        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        userIDFromDB = intent.getStringExtra("userID");

        TextViewAdminFullName.setText(fullName);

    }
}
