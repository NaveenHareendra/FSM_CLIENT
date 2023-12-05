package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class NavigationDrawer extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView AdminNameText;
    String fullName, VSDomainFromDB, userIDFromDB, DivisionFromDB;
    TextView TextViewAllTasksCount, TextViewOpenedTaskCount, TextViewAcceptedTasksCount,TextViewRejectedTasksCount,TextViewInCompletedTasksCount,TextViewCompletedTasksCount, TextViewInprogressTasksCount, TextViewOnHoldTasksCount;
    int totalcount = 0, openedCount = 0, inProgressCount = 0, onHoldCount = 0, completedCount = 0, incompletedCount = 0, rejectedcount = 0, acceptedCount = 0;
    DatabaseReference obj;
    PieChart chart;

    //Adds the toggle functionality to the header section of the navigation drawer.
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        obj = FirebaseDatabase.getInstance().getReference().child("Users");

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        AdminNameText = findViewById(R.id.AdminNameText);

        TextViewAllTasksCount = findViewById(R.id.TextViewAllTasksCount);
        TextViewOpenedTaskCount = findViewById(R.id.TextViewOpenedTasksCount);
        TextViewInCompletedTasksCount = findViewById(R.id.TextViewInCompletedTasksCount);
        TextViewCompletedTasksCount = findViewById(R.id.TextViewCompletedTasksCount);
        TextViewInprogressTasksCount = findViewById(R.id.TextViewInprogressTasksCount);
        TextViewOnHoldTasksCount = findViewById(R.id.TextViewOnHoldTasksCount);
        TextViewAcceptedTasksCount = findViewById(R.id.TextViewAcceptTasksCount);
        TextViewRejectedTasksCount = findViewById(R.id.TextViewRejectTasksCount);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        chart = findViewById(R.id.pie_chart);

        showUserData();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    Log.i("MENU_DRAWER_TAG", "Dashboard item is clicked");
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else if (itemId == R.id.nav_createtasks) {
                    Log.i("MENU_DRAWER_TAG", "Create Task item is clicked");

                    Intent i = new Intent(NavigationDrawer.this, CreateTasks.class);

                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                    //Toast.makeText(NavigationDrawer.this, "Selected Division is (after starting CreateTasks): " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NavigationDrawer.this, "Selected Name is (after starting CreateTasks): " + fullName, Toast.LENGTH_SHORT).show();

                    startActivity(i);

                } else if (itemId == R.id.nav_acceptedtaskboard) {
                    Log.i("MENU_DRAWER_TAG", "Accepted Task Board item is clicked");

                    Intent i = new Intent(NavigationDrawer.this, TaskPreview.class);
                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                    //Toast.makeText(NavigationDrawer.this, "Selected Division is (after starting CreateTasks): " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NavigationDrawer.this, "Selected Name is (after starting CreateTasks): " + fullName, Toast.LENGTH_SHORT).show();

                    startActivity(i);

                } else if (itemId == R.id.nav_reports) {
                    Log.i("MENU_DRAWER_TAG", "Download Report item is clicked");

                    Intent i = new Intent(NavigationDrawer.this, DownloadReports.class);

                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                    //Toast.makeText(NavigationDrawer.this, "Selected Division is (after starting CreateTasks): " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NavigationDrawer.this, "Selected Name is (after starting CreateTasks): " + fullName, Toast.LENGTH_SHORT).show();

                    startActivity(i);

                } else if (itemId == R.id.nav_premap) {
                    Log.i("MENU_DRAWER_TAG", "Location History item is clicked");
                    Intent i = new Intent(NavigationDrawer.this, FOPreviousLocation.class);
                    startActivity(i);
                    drawerLayout.closeDrawer(GravityCompat.START);

                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                } else if (itemId == R.id.nav_map) {
                Log.i("MENU_DRAWER_TAG", "Profile item is clicked");
                Intent i = new Intent(NavigationDrawer.this, FieldOfficerLocation.class);

                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.START);

                }else if (itemId == R.id.nav_profile) {
                    Log.i("MENU_DRAWER_TAG", "Profile item is clicked");
                    Intent i = new Intent(NavigationDrawer.this, MyProfile.class);

                    i.putExtra("selectedDivision", DivisionFromDB);
                    i.putExtra("selectedVSDomain", VSDomainFromDB);
                    i.putExtra("full_name_of_user", fullName);
                    i.putExtra("userID", userIDFromDB);

                    startActivity(i);
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
                return true;
            }
        });

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");
        Query query = tasksRef.orderByChild("fullName").equalTo(fullName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Iterate Through Tasks and Update Counters

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    if (taskSnapshot.child("taskStatus").exists()) {
                        String taskStatus = taskSnapshot.child("taskStatus").getValue(String.class);
                        if (taskStatus != null) {
                            switch (taskStatus) {
                                case "Opened":
                                    openedCount++;
                                    break;
                                case "Resolved":
                                    completedCount++;
                                    break;
                                case "In Progress":
                                    inProgressCount++;
                                    break;
                                case "Accepted":
                                    acceptedCount++;
                                    break;
                                case "On Hold":
                                    onHoldCount++;
                                    break;
                                case "Rejected":
                                    rejectedcount++;
                                    break;
                                case "Unresolved":
                                    incompletedCount++;
                                    break;
                            }
                            totalcount = openedCount+rejectedcount+acceptedCount+inProgressCount+onHoldCount+completedCount+incompletedCount;
                        }
                    }
                }

                // Step 4: Display the Counts
                TextViewAllTasksCount.setText(String.valueOf(totalcount));
                TextViewCompletedTasksCount.setText(String.valueOf(completedCount));
                TextViewInprogressTasksCount.setText(String.valueOf(inProgressCount));
                TextViewOnHoldTasksCount.setText(String.valueOf(onHoldCount));
                TextViewRejectedTasksCount.setText(String.valueOf(rejectedcount));
                TextViewInCompletedTasksCount.setText(String.valueOf(incompletedCount));
                TextViewAcceptedTasksCount.setText(String.valueOf(acceptedCount));
                TextViewOpenedTaskCount.setText(String.valueOf(openedCount));

                addToPieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
    public void showUserData() {
        Intent intent = getIntent();
        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        DivisionFromDB = intent.getStringExtra("selectedDivision");
        userIDFromDB = intent.getStringExtra("userID");

        // Print to verify values
        Log.d("NavigationDrawer", "Full Name: " + fullName);
        Log.d("NavigationDrawer", "DivisionFromDB: " + DivisionFromDB);
        Log.d("NavigationDrawer", "VSDomainFromDB: " + VSDomainFromDB);

        AdminNameText.setText(fullName);
    }

    private void addToPieChart() {

        chart.addPieSlice(new PieModel("Opened Count", openedCount, Color.parseColor("#DFFF00")));
        chart.addPieSlice(new PieModel("Resolved Count", completedCount, Color.parseColor("#9DC44D")));
        chart.addPieSlice(new PieModel("In Progress Count ", inProgressCount, Color.parseColor("#FFBF00")));
        chart.addPieSlice(new PieModel("On Hold Count", onHoldCount, Color.parseColor("#B5AC95")));

        chart.addPieSlice(new PieModel("Accepted Count", acceptedCount, Color.parseColor("#FF7F50")));
        chart.addPieSlice(new PieModel("Rejected Count", rejectedcount, Color.parseColor("#DE3163")));
        chart.addPieSlice(new PieModel("Unresolved Count ", incompletedCount, Color.parseColor("#40E0D0")));

        chart.startAnimation();
        // click.setClickable(false);
    }
}