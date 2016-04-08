package com.example.tatterdemalione.assignmenttracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Tatterdemalione on 2016-03-29.
 */
public class ViewAssignment extends Activity
{
    EditText editName, editUrl, editCourse, editDue;
    Button submit, delete, update, pickDate, go;
    DBAdapter db;
    int assignmentId;

    static final int DATE_DIALOG_ID = 0;
    private int day, month, year;
    private int cDay, cMonth, cYear;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewassignment_layout);

        openDB();

        editName = (EditText) findViewById(R.id.nameEdit);
        editUrl = (EditText) findViewById(R.id.urlEdit);
        editCourse = (EditText) findViewById(R.id.courseEdit);
        editDue = (EditText) findViewById(R.id.dueEdit);
        submit = (Button) findViewById(R.id.button);
        delete = (Button) findViewById(R.id.delete);
        update = (Button) findViewById(R.id.update);
        pickDate = (Button) findViewById(R.id.pickDate);
        go = (Button) findViewById(R.id.go);

        submit.setVisibility(View.INVISIBLE);
        editDue.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        String newAssignment = extras.getString("newAssignment");
        if(newAssignment.equals("true"))
        {
            submit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.INVISIBLE);
            update.setVisibility(View.INVISIBLE);
            go.setVisibility(View.INVISIBLE);
            submit.setOnClickListener(submitListener);
        }
        else
        {
            String assignmentName, courseName, url, dueDate;
            // get extras
            assignmentId = Integer.parseInt(extras.getString("assignmentId"));
            assignmentName = extras.getString("assignmentName");
            courseName = extras.getString("courseName");
            url = extras.getString("assignmentUrl");
            dueDate = extras.getString("dueDate");

            // fill the text fields
            editName.setText(assignmentName);
            editCourse.setText(courseName);
            editUrl.setText(url);
            editDue.setText(dueDate);

            // set listeners
            delete.setOnClickListener(deleteListener);
            update.setOnClickListener(updateListener);
            go.setOnClickListener(goListener);
        }
        pickDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(DATE_DIALOG_ID);
            }
        });


    }
    View.OnClickListener submitListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            String name = editName.getText().toString();
            String course = editCourse.getText().toString();
            String url = editUrl.getText().toString();
            String dueDate = editDue.getText().toString();

            PushDb push = new PushDb(name, course, url, dueDate);
            push.execute();
        }
    };

    View.OnClickListener updateListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            String assignmentName = editName.getText().toString();
            String courseName = editCourse.getText().toString();
            String url = editUrl.getText().toString();
            String dueDate = editDue.getText().toString();

            UpdateDb update = new UpdateDb(assignmentId, assignmentName, courseName, url, dueDate);
            update.execute();
        }
    };
    View.OnClickListener deleteListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            DeleteDb delete = new DeleteDb(assignmentId);
            delete.execute();
        }
    };
    View.OnClickListener goListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {

           String uri = editUrl.getText().toString();
           if( !uri.startsWith("http://") && !uri.startsWith("https://") )
           {
               uri = "http://" + uri;
           }

           Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse(uri) );
           startActivity(browserIntent);
        }
    };

    private void updateDate(int month, int day, int year)
    {
        this.year = year;
        this.month = month;
        this.day = day;

        this.editDue.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(day).append(" ")
                        .append(month + 1).append(" ")
                        .append(year));
    }
    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    updateDate(monthOfYear, dayOfMonth, year);

                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {

        // get the current date
        final Calendar c = Calendar.getInstance();
        cYear = c.get(Calendar.YEAR);
        cMonth = c.get(Calendar.MONTH);
        cDay = c.get(Calendar.DAY_OF_MONTH);

        switch (id)
        {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        dateSetListener,
                        cYear, cMonth, cDay);
        }
        return null;
    }



    private void openDB()
    {
        db = new DBAdapter(this);
        db.open();
    }
    private void closeDB()
    {
        db.close();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        closeDB();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * PushDb class is used to add a row to the database in the background             *
     *                                                                                 *
     *                                                                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class PushDb extends AsyncTask<String, String, String>
    {
        String name, course, url, dueDate;
        private PushDb(String name, String course, String url, String dueDate)
        {
            this.name = name;
            this.course = course;
            this.url = url;
            this.dueDate = dueDate;
        }
        /**
         * Before starting background thread print a message saying what is happening
         */
        @Override
        protected void onPreExecute()
        {
            System.out.println("Building list of all assignments");
        }

        /**
         * Add User to database in the background
         */
        protected String doInBackground(String... args)
        {
            long newId = db.insertRow
                    (
                            name,
                            course,
                            url,
                            dueDate
                    );
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url)
        {
            Intent i = new Intent(ViewAssignment.this, ViewAll.class);
            startActivity(i);
        }
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * DeleteDb class is used to delete a row in the database in the background        *
     *                                                                                 *
     *                                                                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class DeleteDb extends AsyncTask<String, String, String>
    {
        int id;
        boolean success;

        private DeleteDb(int id)
        {
            this.id = id;
        }

        /**
         * Before starting background thread print a message saying what is happening
         */
        @Override
        protected void onPreExecute() {
            System.out.println("Deleting row from database");
        }

        /**
         * Add User to database in the background
         */
        protected String doInBackground(String... args) {
            if (db.deleteRow(id)) {
                success = true;
            }
            else
            {
                success = false;
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url)
        {
            editName.setText("");
            editCourse.setText("");
            editUrl.setText("");
            editDue.setText("");

            Toast toast;
            Context context = getApplicationContext();
            if(success)
            {
                toast = Toast.makeText(context, "deletion successful", Toast.LENGTH_SHORT);
            }
            else
            {
                toast = Toast.makeText(context, "deletion unsuccessful", Toast.LENGTH_SHORT);
            }

            toast.show();

            Intent i = new Intent(ViewAssignment.this, ViewAll.class);
            startActivity(i);
        }
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * UpdateDb class is used to delete a row in the database in the background        *
     *                                                                                 *
     *                                                                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class UpdateDb extends AsyncTask<String, String, String>
    {
        int id;
        String assignmentName, courseName, url, dueDate;
        boolean success;

        private UpdateDb(int id, String assignmentName, String courseName, String url, String dueDate)
        {
            this.assignmentName = assignmentName;
            this.courseName = courseName;
            this.url = url;
            this.dueDate = dueDate;
            this.id = id;
        }

        /**
         * Before starting background thread print a message saying what is happening
         */
        @Override
        protected void onPreExecute()
        {
            System.out.println("Updating assignment: " +  assignmentName);
        }

        /**
         * Add User to database in the background
         */
        protected String doInBackground(String... args)
        {
            if (db.updateRow(id, assignmentName, courseName, url, dueDate))
            {
                success = true;
            }
            else
            {
                success = false;
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url)
        {

            Toast toast;
            Context context = getApplicationContext();
            if(success)
            {
                toast = Toast.makeText(context, "update successful", Toast.LENGTH_SHORT);
            }
            else
            {
                toast = Toast.makeText(context, "update unsuccessful", Toast.LENGTH_SHORT);
            }
            toast.show();
            Intent i = new Intent(ViewAssignment.this, ViewAll.class);
            startActivity(i);
        }
    }
}
