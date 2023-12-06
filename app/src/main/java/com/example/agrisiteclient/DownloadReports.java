package com.example.agrisiteclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DownloadReports extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Query query;
    private String fullName, VSDomainFromDB, userIDFromDB;

    public static Date from;
    public static Date to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_reports);

        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");
        query = databaseReference.orderByChild("userID").equalTo(userIDFromDB);

        showUserData();

        ((TextView) findViewById(R.id.admin_report_title)).setText(fullName + " - " + "Report");

        ImageButton date = findViewById(R.id.aar_date);
        date.setVisibility(View.VISIBLE);

        date.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this);

            dialog.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                String date1 = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                from = calendar.getTime();

                ((EditText) findViewById(R.id.aar_text)).setText(date1);
            });
            dialog.show();
        });

        ImageButton date2 = findViewById(R.id.aar2_date);
        date2.setVisibility(View.VISIBLE);

        date2.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this);
            dialog.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                String date1 = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                to = calendar.getTime();

                ((EditText) findViewById(R.id.aar2_text)).setText(date1);
            });
            dialog.show();
        });

        findViewById(R.id.aar_generate).setOnClickListener(v -> {
            if (from == null) {
                Toast.makeText(this, "Invalid Starting Date!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (to == null) {
                Toast.makeText(this, "Invalid Ending Date!", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchTasksFromFirebase();
        });

        findViewById(R.id.aar_share).setOnClickListener(v -> {
            Bitmap screenshot = takeScreenshot(findViewById(R.id.aar_report_container));
            saveBitmapToGallery(screenshot);
            shareBitmap(screenshot);
        });
    }

    private void fetchTasksFromFirebase() {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalTasks = 0;
                int completedTasks = 0;

                Log.d("DownloadReports", "Entering onDataChange");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("DownloadReports", "Inside loop");

                    Tasks task = snapshot.getValue(Tasks.class);

                    // Check if the task belongs to the selected date range and field officer
                    if (isTaskWithinDateRange(task)) {
                        totalTasks++;
                        //Toast.makeText(DownloadReports.this, "MY FO NAME IS: " +fullName, Toast.LENGTH_SHORT).show();

                        if ("Resolved".equals(task.getTaskStatus())) {
                            completedTasks++;
                            //Toast.makeText(DownloadReports.this, "THIS TASK IS: " +completedTasks, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                float completionRatio = totalTasks == 0 ? 0 : ((float) completedTasks / totalTasks);
                float incompletionRatio = ((float)(totalTasks - completedTasks)/totalTasks);

                //Toast.makeText(DownloadReports.this, "COMPLETION RATIO: " +completionRatio, Toast.LENGTH_SHORT).show();
                //Toast.makeText(DownloadReports.this, "INCOMPLETION RATIO: " +incompletionRatio, Toast.LENGTH_SHORT).show();

                Log.d("COMPLETE RATIO","AS RATIO"+completionRatio);
                Log.d("INCOMPLETE RATIO","AS RATIO"+incompletionRatio);



                updateUI(totalTasks, completedTasks, completionRatio,incompletionRatio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DownloadReports.this, "Failed to fetch tasks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isTaskWithinDateRange(Tasks task) {
        String taskStartDateString = task.getStartdate();
        String taskEndDateString = task.getEnddate();

        try {
            // Parse the date strings to Date objects
            Date taskStartDate = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH).parse(taskStartDateString);
            Date taskEndDate = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH).parse(taskEndDateString);

            // Check if the task belongs to the selected date range and field officer
            return (taskStartDate != null && taskStartDate.after(from) && taskStartDate.before(to))
                    || (taskEndDate != null && taskEndDate.after(from) && taskEndDate.before(to));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;  // Handle parsing errors
        }
    }


    private void updateUI(int totalTasks, int completedTasks, float completionRatio, float incompletionRatio) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");  // Round to 2 decimal places

        // Round completionRatio and incompletionRatio to 2 decimal places
        String roundedCompletionRatio = decimalFormat.format(completionRatio);
        String roundedIncompletionRatio = decimalFormat.format(incompletionRatio);


        ReportAdapter reportAdapter = new ReportAdapter(this, totalTasks, completedTasks, Float.parseFloat(roundedCompletionRatio), Float.parseFloat(roundedIncompletionRatio), fullName, dateFormat.format(from),dateFormat.format(to));
        TableLayout container = findViewById(R.id.aar_report_container);

        container.removeAllViews();
        container.addView(reportAdapter.getView());

        LinearLayout infoContainer = new LinearLayout(this);
        infoContainer.setOrientation(LinearLayout.VERTICAL);

        container.addView(infoContainer);

    }

    private Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String filename = "report_bitmap.png";
        String folderPath = Environment.getExternalStorageDirectory() + "/YourAppReports";

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folderPath, filename);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(this, "Bitmap saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareBitmap(Bitmap bitmap) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");

        Uri uri = getImageUri(this, bitmap);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Report", null);
        return Uri.parse(path);
    }

    private void showUserData() {
        Intent intent = getIntent();
        fullName = intent.getStringExtra("full_name_of_user");
        VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
        userIDFromDB = intent.getStringExtra("userID");
    }


}
