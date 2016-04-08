package com.example.tatterdemalione.assignmenttracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Tatterdemalione on 2016-04-06.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    String date;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of the DatePickerDialog and return it
        return new DatePickerDialog(this.getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        date = day + "/" + month + "/" + year;
    }
}
