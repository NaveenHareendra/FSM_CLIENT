package com.example.agrisiteclient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* This is the Adapter to the recyclerview */

public class TaskRecycleViewAdapter extends RecyclerView.Adapter<TaskRecycleViewAdapter.MyViewHolder> {

    private final List<Tasks> items; // Items Array List
    private final Context context; // Context

    // Constructor
    public TaskRecycleViewAdapter(List<Tasks> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_recycler_view_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecycleViewAdapter.MyViewHolder holder, int position) {

        // Getting single task related details from list
        Tasks tasks = items.get(position);

        // Setting user details to TextViews
        holder.title.setText(tasks.getTitle());
        holder.description.setText(tasks.getDescription());
        holder.startdate.setText(tasks.getStartdate());
        holder.enddate.setText(tasks.getEnddate());
        //holder.taskStatus.setText(tasks.getTaskStatus());

        // When button edit is clicked, it will display the task_update_popup.xml
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.title.getContext())
                        .setContentHolder(new ViewHolder(R.layout.task_update_popup))
                        .setExpanded(true, 1200)
                        .create();

                // dialogPlus.show();
                View view = dialogPlus.getHolderView();

                TextInputEditText title_update = view.findViewById(R.id.taskTitle);
                TextInputEditText description_update = view.findViewById(R.id.txtdescriptionBox);

                Button start_date_update = view.findViewById(R.id.BtnStartDatePicker);
                Button end_date_update = view.findViewById(R.id.BtnEndDatePicker);
                AutoCompleteTextView task_status_update = view.findViewById(R.id.TaskStatus);
                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                title_update.setText(tasks.getTitle());
                description_update.setText(tasks.getDescription());
                task_status_update.setText(tasks.getTaskStatus());
                start_date_update.setText(tasks.getStartdate());
                end_date_update.setText(tasks.getEnddate());

                List<String> taskStatuses = new ArrayList<>();
                taskStatuses.add("Opened");
                taskStatuses.add("In Progress");
                taskStatuses.add("Resolved");
                taskStatuses.add("Unresolved");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, taskStatuses);
                task_status_update.setAdapter(adapter);

                start_date_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the current date
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        // Create a date picker dialog and set the selected date as the current date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                holder.title.getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        // Convert month number to month abbreviation (e.g., JAN, FEB, MAR, etc.)
                                        String[] monthAbbreviations = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
                                        String formattedDate = monthAbbreviations[month] + " " + dayOfMonth + " " + year;

                                        // Set the formatted date on the start_date_update button
                                        start_date_update.setText(formattedDate);
                                    }
                                },
                                year, month, dayOfMonth);

                        // Show the date picker dialog
                        datePickerDialog.show();
                    }
                });

                end_date_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the current date
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        // Create a date picker dialog and set the selected date as the current date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                holder.title.getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        // Convert month number to month abbreviation (e.g., JAN, FEB, MAR, etc.)
                                        String[] monthAbbreviations = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
                                        String formattedDate = monthAbbreviations[month] + " " + dayOfMonth + " " + year;

                                        // Set the formatted date on the start_date_update button
                                        end_date_update.setText(formattedDate);
                                    }
                                },
                                year, month, dayOfMonth);

                        // Show the date picker dialog
                        datePickerDialog.show();
                    }
                });

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newStartDate = start_date_update.getText().toString();
                        String newEndDate = end_date_update.getText().toString();

                        // Retrieves the position of the clicked RecyclerView item
                        int adapterPosition = holder.getAdapterPosition();

                        // Checks if the clicked position is valid
                        if (adapterPosition != RecyclerView.NO_POSITION) {

                            // Retrieves the task object at the clicked position
                            Tasks taskToUpdate = items.get(adapterPosition);

                            // Checks if the retrieved task object is not null
                            if (taskToUpdate != null) {

                                // Creates a map to hold updated task information
                                Map<String, Object> map = new HashMap<>();

                                // Updates the map with data from UI input fields
                                map.put("title", title_update.getText().toString());
                                map.put("description", description_update.getText().toString());
                                map.put("taskStatus", task_status_update.getText().toString());

                                if (!newStartDate.isEmpty() && !newEndDate.isEmpty()) {
                                    // Update the start date and end date in the map
                                    map.put("startdate", newStartDate);
                                    map.put("enddate", newEndDate);
                                } else {
                                    Toast.makeText(holder.title.getContext(), "Please enter start date and end date.", Toast.LENGTH_SHORT).show();
                                    return; // Don't proceed if start date or end date is empty
                                }

                                if (taskToUpdate.getKey() != null) {
                                    map.put("startdate", newStartDate);
                                    map.put("enddate", newEndDate);

                                    // Retrieves the unique key of the task to update
                                    String taskKey = taskToUpdate.getKey();

                                    FirebaseDatabase.getInstance().getReference().child("Tasks")
                                            .child(taskKey).updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(holder.title.getContext(), "Data Updated Successfully!", Toast.LENGTH_SHORT).show();
                                                    dialogPlus.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(holder.title.getContext(), "Error While Updating!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Log.e("Update", "Task key is null");
                                }
                            } else {
                                Log.e("Update", "Task to update is null");
                            }
                        } else {
                            Log.e("Update", "Invalid adapter position");
                        }
                    }
                });

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.title.getContext());
                builder.setTitle("Are you sure you want delete task?");
                builder.setMessage("Deleted data can't undo again.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseDatabase.getInstance().getReference().child("Tasks")
                                .child(tasks.getKey()).removeValue();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.title.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // MyViewHolder Class to hold View Reference for every item in the RecyclerView
    static class MyViewHolder extends RecyclerView.ViewHolder {

        // Declaring TextViews
        private final TextView title, description, startdate, enddate, taskStatus;
        Button btnEdit, btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Getting TextViews from task_recycler_view_adapter_layout XML file

            title = itemView.findViewById(R.id.Title_of_Task);
            description = itemView.findViewById(R.id.Description_of_Task);
            startdate = itemView.findViewById(R.id.Start_of_Task);
            enddate = itemView.findViewById(R.id.End_of_Task);
            taskStatus = itemView.findViewById(R.id.Task_Status);

            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }
}
