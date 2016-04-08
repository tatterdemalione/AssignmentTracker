package com.example.tatterdemalione.assignmenttracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    Button viewAll, addAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Associate variables with UI xml */
        viewAll = (Button) findViewById(R.id.viewAll);
        addAssignment = (Button) findViewById(R.id.add);


        /* Add OnClickListeners */
        viewAll.setOnClickListener(viewAllListener);
        addAssignment.setOnClickListener(addAssignmentListener);
    }

    View.OnClickListener viewAllListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent i = new Intent(MainActivity.this, ViewAll.class);
            startActivity(i);
        }
    };
    View.OnClickListener addAssignmentListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent i = new Intent(MainActivity.this, ViewAssignment.class);
            String newAssignment = "true";
            i.putExtra("newAssignment", newAssignment);
            startActivity(i);
        }
    };
}
