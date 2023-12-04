package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TaskPreview extends AppCompatActivity {

    /*This activity is responsible for fetching tasks data from the Firebase Realtime Database and displaying it in a RecyclerView.*/

    //Getting firebase database reference to communicate with firebase database
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private final List<Tasks> tasksList = new ArrayList<>();
    private TaskRecycleViewAdapter adapter;
    String fullName, VSDomainFromDB, userIDFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_preview);

        //Getting Recyclerview from the xml file
        final RecyclerView taskRecyclerView = findViewById(R.id.task_preview_recycle_view);

        //Setting Recyclerview  size fixed for every item in the recycler view
        taskRecyclerView.setHasFixedSize(true);

        //Set Layout Manger for the Recyclerview. Ex: LinearLayoutManager (Vertical) Mode
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(TaskPreview.this));

        showUserData();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("FirebaseData", "onDataChange called");

                //Clear Old Task Details from the list to add new Task Details
                tasksList.clear();

                //Getting all children users from
                for (DataSnapshot Tasks : snapshot.child("Tasks").getChildren()) {
                    Log.d("FirebaseData", "Processing task: " + Tasks.getKey());

                    //To prevent crashes, check if tasks has all the details in the DB
                    if (Tasks.hasChild("title") && Tasks.hasChild("description") && Tasks.hasChild("startdate") && Tasks.hasChild("enddate") && Tasks.hasChild("taskStatus") && Tasks.hasChild("fullName") && Tasks.hasChild("vsdomainFromDB") && Tasks.hasChild("userIDFromDB")  /*&& Tasks.hasChild("FOLatitude") && Tasks.hasChild("FOLongitude")*/) {

                        String FOUserID = Tasks.child("userIDFromDB").getValue(String.class);
                        String FOFullName = Tasks.child("fullName").getValue(String.class);
                        String Status_of_task = Tasks.child("taskStatus").getValue(String.class);

                        Log.d("FirebaseData TASK DATA", "Title: " + Tasks.child("title").getValue());
                        Log.d("FirebaseData TASK DATA", "Description: " + Tasks.child("description").getValue());
                        Log.d("FirebaseData TASK DATA", "Start Date: " + Tasks.child("startdate").getValue());
                        Log.d("FirebaseData TASK DATA", "End Date: " + Tasks.child("enddate").getValue());
                        Log.d("FirebaseData TASK DATA", "VSDomain: " + Tasks.child("vsdomainFromDB").getValue());
                        Log.d("FirebaseData TASK DATA", "Full Name: " + Tasks.child("fullName").getValue());
                        Log.d("FirebaseData TASK DATA", "Task Status: " + Tasks.child("taskStatus").getValue());

                        if (FOUserID != null && FOUserID.equals(userIDFromDB) && Status_of_task.equals("Accepted")) {
                            Log.d("FirebaseData 1", "Creating tasks object for task: " + Tasks.getKey());
                            Log.d("FirebaseData 2", "FOFullName: " + FOFullName + ", fullName: " + fullName);
                            Log.d("FirebaseData 2", "FOUserID: " + FOUserID + ", userIDFromDB: " + userIDFromDB);


                            //In, here give column IDs as per the way mentioned in the firebase DB
                            final String getKey = Tasks.child("key").getValue(String.class);
                            final String getTitle = Tasks.child("title").getValue(String.class);
                            final String getDescription = Tasks.child("description").getValue(String.class);
                            final String getStart = Tasks.child("startdate").getValue(String.class);
                            final String getEnd = Tasks.child("enddate").getValue(String.class);
                            final String getTaskStatus = Tasks.child("taskStatus").getValue(String.class);
                            final String getFullName = Tasks.child("FullName").getValue(String.class);
                            final String getVSDomainFromDB = Tasks.child("vsdomainFromDB").getValue(String.class);
                            final String getUserIDFromDB = Tasks.child("userID").getValue(String.class);

                            //Creating task items with task details
                            Tasks tasks = new Tasks(getKey, getTitle, getDescription, getStart, getEnd, getTaskStatus, getFullName, getVSDomainFromDB, getUserIDFromDB);

                            //Adding task items with task details
                            tasksList.add(tasks);
                            Log.d("FirebaseData", "Adding task: " + tasks);
                        } /*else{
                            Log.d("FirebaseData", "FOFullName does not match fullName for task: " + Tasks.getKey());
                            Log.d("FirebaseData", "FOFullName: " + FOFullName + ", fullName: " + fullName);
                        }*/
                    }
                }
                //After adding all the task related data to the list, set the adapter
                taskRecyclerView.setAdapter(new TaskRecycleViewAdapter(tasksList, TaskPreview.this));


                /*adapter = new TaskRecycleViewAdapter(tasksList, TaskPreview.this);
                taskRecyclerView.setAdapter(adapter);*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "onCancelled called", error.toException());

            }
        });

    }

    public void showUserData() {
        Intent intent = getIntent();

        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        userIDFromDB = intent.getStringExtra("userID");


        //Toast.makeText(TaskPreview.this, "Your Selected FullName in TP Activity: " + fullName, Toast.LENGTH_SHORT).show();
        //Toast.makeText(TaskPreview.this, "Your Selected Division is TP Activity: " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
    }
}


