package com.example.fitnesstrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {
    private Context context;
    private List<Student> studentList;


    public StudentAdapter(Context context, List<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewUsername = convertView.findViewById(R.id.textViewUsername);
            viewHolder.textViewEmail = convertView.findViewById(R.id.textViewEmail);
            viewHolder.textViewAddress = convertView.findViewById(R.id.textViewAddress);
            viewHolder.textViewBirthday = convertView.findViewById(R.id.textViewBirthday);
            viewHolder.textViewHeight = convertView.findViewById(R.id.textViewHeight);
            viewHolder.textViewWeight = convertView.findViewById(R.id.textViewWeight);
            viewHolder.imageViewGender = convertView.findViewById(R.id.imageViewGender);
            viewHolder.imageAddStudentPerson = convertView.findViewById(R.id.imgAddStudent);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Student student = studentList.get(position);

        viewHolder.textViewUsername.setText("Username: " + student.getUsername());
        viewHolder.textViewEmail.setText("Email: " + student.getEmail());
        viewHolder.textViewAddress.setText("Address: " + student.getAddress());
        viewHolder.textViewBirthday.setText("Birthday: " + student.getBirthday());
        viewHolder.textViewHeight.setText("Height: " + student.getHeight() + " kg");
        viewHolder.textViewWeight.setText("Weight: " + student.getWeight() + " cm");

        if (student.getGender().equals(context.getString(R.string.female))) {
            viewHolder.imageViewGender.setImageResource(R.drawable.ic_female);
        } else if (student.getGender().equals(context.getString(R.string.male))) {
            viewHolder.imageViewGender.setImageResource(R.drawable.ic_male);
        } else {
            viewHolder.imageViewGender.setImageResource(R.drawable.ic_male);
        }

        return convertView;
    }
    private static class ViewHolder {
        TextView textViewUsername;
        TextView textViewEmail;
        TextView textViewAddress;
        TextView textViewBirthday;
        TextView textViewHeight;
        TextView textViewWeight;
        ImageView imageViewGender;
        ImageView imageAddStudentPerson;
    }
}