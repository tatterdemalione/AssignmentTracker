package com.example.tatterdemalione.assignmenttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter
{
    private static final String TAG = "DBAdapter"; //used for logging

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final String KEY_assignmentName = "assignmentName";
    public static final String KEY_courseName = "courseName";
    public static final String KEY_courseUrl = "courseWebsite";
    public static final String KEY_dueDate = "dueDate";

    // DB Columns associate with each field
    public static final int COL_ROWID = 0;
    public static final int COL_assignmentName = 1;
    public static final int COL_courseName = 2;
    public static final int COL_courseWebsite = 3;
    public static final int COL_dueDate = 4;

    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_assignmentName, KEY_courseName, KEY_courseUrl, KEY_dueDate};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "AssignmentDatabase";
    public static final String DATABASE_TABLE = "assignmentTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_assignmentName + " text not null, "
                    + KEY_courseName + " integer not null, "
                    + KEY_courseUrl + " string not null,"
                    + KEY_dueDate + " string not null"
                    + ");";

    // Context of application who uses us.
    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                  PUBLIC METHODS                                                         *
     *                                                                                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String courseName, String courseUrl, String assignmentName, String dueDate)
    {
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_courseName, courseName);
        initialValues.put(KEY_courseUrl, courseUrl);
        initialValues.put(KEY_assignmentName, assignmentName);
        initialValues.put(KEY_dueDate, dueDate);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(int rowId)
    {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll()
    {
        Cursor c = getAllRows();
        int rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow( c.getInt(rowId) );
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows()
    {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(int rowId)
    {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null)
        {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(int rowId, String courseName, String courseUrl, String assignmentNum, String dueDate)
    {
        String where = KEY_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_courseName, courseName);
        newValues.put(KEY_courseUrl, courseUrl);
        newValues.put(KEY_assignmentName, assignmentNum);
        newValues.put(KEY_dueDate, dueDate);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }



    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }

}

