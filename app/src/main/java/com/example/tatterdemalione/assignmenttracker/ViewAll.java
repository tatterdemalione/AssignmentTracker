package com.example.tatterdemalione.assignmenttracker;

/**
 * Created by Tatterdemalione on 2016-03-29.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ViewAll extends Activity
{
    ListView lv;
    DBAdapter db;
    Assignment[] listPopuli;
    ArrayAdapter<String> arrayAdapter;
    private int length = 0;
    private int day, month, year;
    private int cDay, cMonth, cYear;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewall_layout);

        lv = (ListView) findViewById(R.id.listView);

        // Open the Database and start executing thread to populate GUI listview
        openDB();
        BuildList buildList = new BuildList();
        buildList.execute();

        // get the current date
        final Calendar c = Calendar.getInstance();
        cYear = c.get(Calendar.YEAR);
        cMonth = c.get(Calendar.MONTH);
        cDay = c.get(Calendar.DAY_OF_MONTH);


    }

    private void sortArray(Assignment[] arr)
    {
      SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");



        for(int i=0;i<arr.length;i++)
        {
           //Get the due date of the assignment
           String dueDate = arr[i].getDueDate();
           String currentDate = cDay + " " + cMonth + " " + cYear;


           try
           {
               // Parse the difference between the due date and current date and save the information in the Assignment object
               Date date1 = myFormat.parse(dueDate);
               Date date2 = myFormat.parse(currentDate);
               long diff = date1.getTime() - date2.getTime();
               arr[i].setPriority(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
               System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{ " + arr[i].getPriority() + " }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
           }
           catch(ParseException e)
           {
               e.printStackTrace();
           }
        }
        sort();
    }

    public void sort()
    {
        // check for empty or null array
        if ( listPopuli==null || listPopuli.length==0 ){
            return;
        }
        quicksort(0, listPopuli.length - 1);
    }

    private void quicksort(int low, int high)
    {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        long pivot = listPopuli[low + (high-low)/2].getPriority();

        // Divide into two lists
        while (i <= j)
        {
            // If the current value from the left list is smaller then the pivot
            // element then get the next element from the left list
            while (listPopuli[i].getPriority() < pivot) {
                i++;
            }
            // If the current value from the right list is larger then the pivot
            // element then get the next element from the right list
            while (listPopuli[j].getPriority() > pivot) {
                j--;
            }

            // If we have found a values in the left list which is larger then
            // the pivot element and if we have found a value in the right list
            // which is smaller then the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(low, j);
        if (i < high)
            quicksort(i, high);
    }

    private void exchange(int i, int j)
    {
        Assignment temp = listPopuli[i];
        listPopuli[i] = listPopuli[j];
        listPopuli[j] = temp;
    }



    public void getAllAssignments()
    {
        System.out.println("in method getAllAssignments");
        Cursor cursor = db.getAllRows();
        fillArray(cursor);
    }

    AdapterView.OnItemClickListener buildList = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position, long id)
        {
            Intent i = new Intent(ViewAll.this, ViewAssignment.class);
            String assignmentName = listPopuli[position].getAssignmentName();
            int assignmentId = listPopuli[position].getId();
            String courseName = listPopuli[position].getCourseName();
            String url = listPopuli[position].getUrl();
            String dueDate = listPopuli[position].getDueDate();
            System.out.println("*********************************************" + assignmentId);
            i.putExtra("newAssignment", "false");
            i.putExtra( "assignmentId", Integer.toString(assignmentId) );
            i.putExtra("assignmentName", assignmentName);
            i.putExtra("courseName", courseName);
            i.putExtra("assignmentUrl", url);
            i.putExtra("dueDate", dueDate);
            startActivity(i);
        }

    };

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

    // Display an entire recordset to the screen.
    private void fillArray(Cursor cursor)
    {
        System.out.println("in method fillArray");
        // Reset cursor to start, checking to see if there's data:
        if (cursor.moveToFirst())
        {
            int i = 0;
            do
            {
                // Get attributes from database and build an assignment object
                int assignmentId = cursor.getInt(DBAdapter.COL_ROWID);
                String assignmentName = cursor.getString(DBAdapter.COL_assignmentName);
                String courseName = cursor.getString(DBAdapter.COL_courseName);
                String assignmentUrl = cursor.getString(DBAdapter.COL_courseWebsite);
                String assignmentDueDate = cursor.getString(DBAdapter.COL_dueDate);

                Assignment assignment = new Assignment
                        (
                                assignmentId, courseName, assignmentUrl, assignmentName, assignmentDueDate
                        );

                // Put the assignment in the array:
                listPopuli[i] = assignment;
                i++;
            } while(cursor.moveToNext());
        }
        // Close the cursor to avoid a resource leak.
        cursor.close();
    }
    private int countRows(Cursor cursor)
    {
        System.out.println("in method countRows");
        int count = 0;
        if(cursor.moveToFirst())
        {
            do
            {
                count++;
            } while(cursor.moveToNext());
        }
        System.out.println("There are " + count + " Assignments in the database");
        return count;
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * buildList class is used to fetch the list of assignments from the databse       *
     * and populate the GUI                                                            *
     *                                                                                 *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private class BuildList extends AsyncTask<String, String, String>
    {

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
            // Set the length of the arrays
            Cursor cursor = db.getAllRows();
            length = countRows(cursor);
            listPopuli = new Assignment[length];

            // fill the array with assignments
            getAllAssignments();

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url)
        {
            sortArray(listPopuli);
            for(int i = 0;i<length;i++)
            {
                System.out.println(listPopuli[i].getCourseName());
            }
            String[] assignmentArr = new String[length];

            for(int i = 0;i<length;i++)
            {
                assignmentArr[i] = listPopuli[i].getAssignmentName();
            }


            // Set the list view to contain all the assignments
            arrayAdapter = new ArrayAdapter<String> (ViewAll.this, android.R.layout.simple_list_item_1,assignmentArr);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(buildList);

            try
            {
                String priorityAssignment = "The Assignment " + listPopuli[0].getAssignmentName() + " is due in " + listPopuli[0].getPriority() + " days ";
                SmsManager.getDefault().sendTextMessage("613-791-2698", null, priorityAssignment, null, null );
            }
            catch(Exception e)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewAll.this);
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.setMessage(e.getMessage());
                dialog.show();
            }
        }
    }

}
