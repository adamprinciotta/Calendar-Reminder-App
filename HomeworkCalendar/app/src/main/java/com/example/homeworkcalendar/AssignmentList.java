package com.example.homeworkcalendar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AssignmentList extends AppCompatActivity {
    private static final String TAG = "AssignmentList";
    DatabaseHelper mDatabaseHelper;
    private ListView mListViewAssignment;
    private ListView mListViewDescription;
    private ListView mListViewDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListViewAssignment = (ListView) findViewById(R.id.assignment_list);
        mListViewDescription = (ListView) findViewById(R.id.description_list);
        mListViewDate = (ListView) findViewById(R.id.date_list);
        mDatabaseHelper = new DatabaseHelper(this);

        populateAssignmentList();
    }

    private void populateAssignmentList() {
        String dataString;
        Cursor data = mDatabaseHelper.getData();
        //Cursor DELETE = mDatabaseHelper.getID("Test");
        if (data != null) {
            ArrayList<String> listAssign = new ArrayList<>();
            ArrayList<String> listDesc = new ArrayList<>();
            ArrayList<String> listDate = new ArrayList<>();
            while (data.moveToNext()) {
                dataString = data.getString(1);
                String[] splitStrings = dataString.split(",");

                listAssign.add(splitStrings[0]);
                listDesc.add(splitStrings[1]);
                listDate.add(splitStrings[2]);
            }

            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listAssign);
            ListAdapter adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDesc);
            ListAdapter adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDate);

            mListViewAssignment.setAdapter(adapter);
            mListViewDescription.setAdapter(adapter2);
            mListViewDate.setAdapter(adapter3);
        }
    }
}
