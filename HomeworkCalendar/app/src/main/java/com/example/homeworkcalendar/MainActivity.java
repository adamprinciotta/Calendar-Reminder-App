package com.example.homeworkcalendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private NotificationManagerCompat notificationManager;
    int mMin, mHour, mDate, mMonth, mYear = -1;
    int rMin, rHour, rDate, rMonth, rYear = -1;
    String assignment, reminder;
    Button storeAssignment, viewAssignments;
    DatabaseHelper mDatabaseHelper;
    Spinner spinner, ReminderSpinner;
    ArrayList<String> arrayListAssignment = new ArrayList<>();
    ArrayList<String> arrayListReminder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayListAssignment.add("Homework");
        arrayListAssignment.add("Reading");
        arrayListAssignment.add("Project");
        arrayListAssignment.add("Test");
        arrayListAssignment.add("Other");

        arrayListReminder.add("1 Hour Before");
        arrayListReminder.add("4 Hours Before");
        arrayListReminder.add("12 Hours Before");
        arrayListReminder.add("1 Day Before");
        arrayListReminder.add("1 Week Before");
        arrayListReminder.add("Other");

        createNotificationChannel();

        mDatabaseHelper = new DatabaseHelper(this);

        ImageView timer = findViewById(R.id.timer);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TimerActivity.class));
            }
        });


        storeAssignment = (Button) findViewById(R.id.StoreAssignment);
        storeAssignment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                EditText DescriptionText = (EditText) findViewById(R.id.Description);
                String Description = DescriptionText.getText().toString();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, mMin);
                cal.set(Calendar.HOUR_OF_DAY, mHour);
                cal.set(Calendar.MONTH, mMonth);
                cal.set(Calendar.DAY_OF_MONTH, mDate);
                cal.set(Calendar.YEAR, mYear);
                long assignmentDue = cal.getTimeInMillis();

                if (reminder.equals("1 Hour Before")) {
                    assignmentDue -= 3600000;
                } else if (reminder.equals("4 Hours Before")) {
                    assignmentDue -= 14400000;
                } else if (reminder.equals("12 Hours Before")) {
                    assignmentDue -= 43200000;
                } else if (reminder.equals("1 Day Before")) {
                    assignmentDue -= 86400000;
                } else if (reminder.equals("1 Week Before")) {
                    assignmentDue -= 604800000;
                } else if (ReminderSpinner.getSelectedItemPosition() >= 5) {
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, rMin);
                    cal.set(Calendar.HOUR_OF_DAY, rHour);
                    cal.set(Calendar.MONTH, rMonth);
                    cal.set(Calendar.DAY_OF_MONTH, rDate);
                    cal.set(Calendar.YEAR, rYear);
                    assignmentDue = cal.getTimeInMillis();
                }

                Calendar PastCheck = Calendar.getInstance();
                if (assignmentDue < PastCheck.getTimeInMillis()) {
                    Toast.makeText(MainActivity.this, "Don't time travel", Toast.LENGTH_SHORT).show();
                } else {
                    String DataToAdd = assignment + "," + Description + "," + mMonth + "/" + mDate;
                    AddData(DataToAdd);

                    Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
                    intent.putExtra("description", Description);
                    intent.putExtra("assignment", assignment);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Log.i("REMINDER", assignment + " " + Description);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            assignmentDue,
                            pendingIntent);

                    Toast.makeText(MainActivity.this, "Assignment Stored", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageView viewAssignments = findViewById(R.id.viewAssignments);
        viewAssignments.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AssignmentList.class));
            }
        });


        spinner = (Spinner) findViewById(R.id.AssignmentSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListAssignment);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ReminderSpinner = (Spinner) findViewById(R.id.ReminderSpinner);
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListReminder);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ReminderSpinner.setAdapter(reminderAdapter);
        ReminderSpinner.setOnItemSelectedListener(this);

        View TimeDue = findViewById(R.id.TimeDue);
        TimeDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMin = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMin = minute;
                    }
                }, mHour, mMin, false);
                timePickerDialog.show();
            }
        });

        View DateDue = findViewById(R.id.DateDue);
        DateDue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDate = c.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDate = dayOfMonth;
                    }
                }, mYear, mMonth, mDate);
                datePickerDialog.show();

            }
        });

    }

    public void AddData(String AssignmentCol) {
        boolean insertData = mDatabaseHelper.addData(AssignmentCol);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.AssignmentSpinner) {
            assignment = parent.getItemAtPosition(position).toString();
            if (assignment.equals("Other")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Assignment Title");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assignment = input.getText().toString();
                        arrayListAssignment.add(assignment);
                        spinner.setSelection(arrayListAssignment.size()-1);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(arrayListAssignment.size() == 5){
                            spinner.setSelection(0);
                        }
                        else{
                            spinner.setSelection(arrayListAssignment.size()-1);
                        }
                    }
                });
                builder.show();
            }
        } else if (parent.getId() == R.id.ReminderSpinner) {
            reminder = parent.getItemAtPosition(position).toString();
            if (reminder.equals("Other")) {
                Calendar c = Calendar.getInstance();
                rYear = c.get(Calendar.YEAR);
                rMonth = c.get(Calendar.MONTH);
                rDate = c.get(Calendar.DATE);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        rYear = year;
                        rMonth = month;
                        rDate = dayOfMonth;
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                rHour = hourOfDay;
                                rMin = minute;
                                arrayListReminder.add((rMonth + 1) + "/" + rDate + "/" + rYear + "   " + rHour + ":" + rMin);
                                ReminderSpinner.setSelection(arrayListReminder.size()-1);
                            }
                        }, rHour, rMin, false);
                        timePickerDialog.show();
                    }
                }, rYear, rMonth, rDate);
                datePickerDialog.show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "TestChannel";
            String descriptionChannel = "Channel for testing";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = null;

            channel = new NotificationChannel("notificationTest", name, importance);

            channel.setDescription(descriptionChannel);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

