package com.example.fitnesstrack.memberlist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.example.fitnesstrack.views.StudentDashboardActivity;

import java.util.Calendar;

public class CalendarActivity extends MyBaseActivity {
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        getDataExtras(getIntent().getExtras());
        DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, monthOfYear, dayOfMonth);
            // Tarihle ilgili diğer işlemleri burada yapabilirsiniz
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,  getString(R.string.ok), datePickerDialog);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, null, datePickerDialog); // İptal düğmesini kaldırır

        datePickerDialog.setOnDismissListener(dialog -> {
            // On dismiss, return to the StudentActivity
            Intent intent = new Intent(CalendarActivity.this, StudentDashboardActivity.class);
            intent.putExtra("Student", student);
            startActivity(intent);
        });

        datePickerDialog.show();


    }

    private void getDataExtras(Bundle extras) {
        student = (Student) extras.getSerializable("Student");
    }

}