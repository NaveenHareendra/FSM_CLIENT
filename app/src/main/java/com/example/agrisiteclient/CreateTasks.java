package com.example.agrisiteclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import io.github.muddz.styleabletoast.StyleableToast;

public class CreateTasks extends AppCompatActivity {
    String fullName, VSDomainFromDB, userIDFromDB;
    AutoCompleteTextView autoCompleteTextView;
    TextInputEditText taskTitle, txtdescriptionBox;
    TextView TextViewSetLocation;
    private static final int REQUEST_LOCATION = 1;
    private String FOLongitude, FOLatitude, taskstatus,address;
    Button btnStart, btnAddTask, btnRemove, btnEnd;
    DatePickerDialog datePickerDialog;
    DatabaseReference obj;
    private SwitchMaterial getLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tasks);

        //TextViewSetLocation = findViewById(R.id.TextViewSetLocation);

        String[] Status = new String[]{"Opened", "In Progress", "On Hold", "Resolved","Unresolved"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.task_status_dropdown_items, Status);

        // Remove the type declaration (AutoCompleteTextView) here to use the class-level variable
        autoCompleteTextView = findViewById(R.id.TaskStatus);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String taskstatus = autoCompleteTextView.getText().toString();
                //Toast.makeText(CreateTasks.this, autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        obj = FirebaseDatabase.getInstance().getReference().child("Tasks");

        // Get references to the layout views
        btnAddTask = findViewById(R.id.btnAddTask);
        btnRemove = findViewById(R.id.btnRemove);
        btnStart = findViewById(R.id.BtnStartDatePicker);
        btnEnd = findViewById(R.id.BtnEndDatePicker);
        taskTitle = findViewById(R.id.taskTitle);
        txtdescriptionBox = findViewById(R.id.txtdescriptionBox);
        //getLocation = findViewById(R.id.GetLocation);

        // Set the text of the button to the current date
        btnStart.setText(getTodayDate());
        btnEnd.setText(getTodayDate());

        showUserData();

        /*getLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                //Check whether the location permission
                if(compoundButton.isChecked()){
                    CheckedLocationPermission();
                }
            }
        });
*/
        //Insert Data When Button Complete is Clicked
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showUserData();

                //Call the InsertData method
                InsertTaskData();

                //FCMSend.pushNotifications(CreateTasks.this, "cKUS5GFwQiynHOP54WNFtl:APA91bEz707eWZHx3kPf2VaSzG_Mwz9o6Onrhh_uMs5u0efvd2ZagtldO5wauRBJk1aDWfOrDPpDOE9tK-ComZdMyEfDRD3FzxRTVTvo37lj_wiSbuPPSSmD5TAZISDczkUZG_9cJm92", "Good Night!", "Good Night Bro");
            }
        });

        //Remove All the Data When Button Clear is Clicked
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTitle.setText("");
                txtdescriptionBox.setText("");
                btnStart.setText("");
                btnEnd.setText("");
                //TextViewSetLocation.setText("");
                autoCompleteTextView.setText("");
            }
        });
    }

/*    private void CheckedLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            //Create a Method to On your Mobile GPS
            OnGPS();

        }else{
            getCurrentLocation();
        }
    }*/

    /*private void getCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }else{
            Location GpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(GpsLocation!=null) {
                double latitude = GpsLocation.getLatitude();
                double longitude = GpsLocation.getLongitude();

                //We need to Convert the double into String
                FOLatitude = String.valueOf(latitude);
                FOLongitude = String.valueOf(longitude);

                //Set the values in TextView
                String Location = FOLatitude + FOLongitude;
                TextViewSetLocation.setText(Location);

                getAddressFromLatLong(CreateTasks.this, latitude, longitude);

            }
        }
    }

    public void getAddressFromLatLong(Context context, double LATITUDE, double LONGITUDE){
        //set address
        try{
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE,1);
            if(addresses!=null && addresses.size()>0){
                address = addresses.get(0).getAddressLine(0);
            }

            TextViewSetLocation.setText(address);
            TextViewSetLocation.setSelected(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    public void InsertTaskData() {

        // Retrieve the input values

        String uniqueKey = null;
        String title = taskTitle.getText().toString();
        String description = txtdescriptionBox.getText().toString();
        String startdate = btnStart.getText().toString();
        String enddate = btnEnd.getText().toString();
        taskstatus = autoCompleteTextView.getText().toString();
        fullName = getIntent().getStringExtra("full_name_of_user");
        VSDomainFromDB = getIntent().getStringExtra("selectedVSDomain");
        userIDFromDB = getIntent().getStringExtra("userID");
;
        // Create the task object
        Tasks tasks = new Tasks(uniqueKey, title, description, startdate, enddate, taskstatus,fullName, VSDomainFromDB,userIDFromDB);

        // Generate a unique key for the task
        uniqueKey = FirebaseDatabase.getInstance().getReference().child("Tasks").push().getKey();

        // Set the generated unique key in your task object
        tasks.setKey(uniqueKey);

        FirebaseDatabase.getInstance().getReference().child("Tasks").child(uniqueKey).setValue(tasks);
        //obj.push().setValue(tasks);
        StyleableToast.makeText(this, "Data Inserted!", Toast.LENGTH_LONG, R.style.InsertToast).show();
    }

    private int countTasksByStatus(String taskstatus) {

        return 0; // Replace with the actual count
    }

    /*public void sendNotification(String toString, String body){
        String ReceivingDeviceToken = "cLDSHOgETM6AnzMX9d0dyM:APA91bHqLF8uB1YGD4MItyAoNz93kbrzqCmxwaLEsBNeXPLGM5COzP1e4uYt0C4ammpo-WsE0k4R6AksL78oJylS75jdvTbRGxNF5xyV9MpPnUo920S2ZAMv_9NonYveS-AxG9vFHbWv";

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonNotif = new JSONObject();
        JSONObject wholeObj = new JSONObject();

        try {
            jsonNotif.put("title", ReceivingDeviceToken);
            jsonNotif.put("body",body);

            wholeObj.put("to",ReceivingDeviceToken);
            wholeObj.put("notification", jsonNotif);

        } catch (JSONException e) {
            Log.d("mylog",e.toString());
        }

        //Send the notification through Firebase API

        RequestBody rBody = RequestBody.create(mediaType, wholeObj.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(rBody)
                .addHeader("Authorization", "AAAAdCI6LYo:APA91bEKrs4IoxwHIZeCffu0_iiucEczPeL0LYNwGVQf4PF-yJIDlf3MAB-TbYI_j57Ho8_g2iDHuFAqp72A_XtUJptCzxwdMI1DzdPXCq_i5LS9GCCnFDQhVUJgutY0BL_iWllGzCc3")
                .addHeader("Content-Type", "application/json").build();

        try {
            Response response =client.newCall(request).execute();
        } catch (IOException e) {
            Log.d("mylog", e.toString());
        }
    }*/

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public void openDatePicker(View view) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);

                if (view.getId() == R.id.BtnStartDatePicker) {
                    btnStart.setText(date);
                } else if (view.getId() == R.id.BtnEndDatePicker) {
                    btnEnd.setText(date);
                }
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    public void showUserData() {
            Intent intent = getIntent();

            fullName = intent.getStringExtra("full_name_of_user");
            VSDomainFromDB = intent.getStringExtra("selectedVSDomain");
            userIDFromDB = intent.getStringExtra("userID");

            //Toast.makeText(CreateTasks.this, "Your Selected Division is: " + VSDomainFromDB, Toast.LENGTH_SHORT).show();
    }
}
