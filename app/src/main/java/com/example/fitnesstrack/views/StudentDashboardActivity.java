package com.example.fitnesstrack.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.memberlist.CalendarActivity;
import com.example.fitnesstrack.memberlist.ChatListActivity;
import com.example.fitnesstrack.memberlist.SettingsActivity;
import com.example.fitnesstrack.memberlist.StudentTrainingActivity;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

public class StudentDashboardActivity extends MyBaseActivity {
    private TextView studentName;
    private Student student;
    
    private ProgressBar progressBar;

    private RoundedImageView imStudentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        getDataExtras(getIntent().getExtras());
        initView();
        initStudentProfile();
    }

    private void initStudentProfile() {
        if(!student.getStudentProfileImage().isEmpty()) {
            byte[] bytes = Base64.decode(student.getStudentProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imStudentProfile.setImageBitmap(bitmap);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchGetCoachData();
    }

    private void fetchGetCoachData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference studentCollection = db.collection("FitnessTrackStudent");
        Query studentQuery = studentCollection.whereEqualTo("email", student.getEmail());
        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String studentId = documentSnapshot.getId();
                            String studentAddres = documentSnapshot.getString("address");
                            String studentBirthday = documentSnapshot.getString("birthday");
                            String studentEmail = documentSnapshot.getString("email");
                            String studentGender = documentSnapshot.getString("gender");
                            String studentHeight = documentSnapshot.getString("height");
                            String studentPassword = documentSnapshot.getString("password");
                            String studentTeacherId = documentSnapshot.getString("teacherId");
                            String studentType = documentSnapshot.getString("type");
                            String studentUsername = documentSnapshot.getString("username");
                            String studentWeight = documentSnapshot.getString("weight");
                            String studentGroupId = documentSnapshot.getString("groupIds");
                            String studentProfileImage = documentSnapshot.getString("image");
                            String studentRegisteredDate = documentSnapshot.getString("registeredDate");

                            Student newstudent = new Student(studentId, studentAddres, studentBirthday,
                                    studentEmail, studentGender, studentHeight, studentPassword,
                                    studentTeacherId, studentType, studentUsername,
                                    studentWeight, studentGroupId, studentProfileImage,
                                    studentRegisteredDate);
                            student=newstudent;
                            initStudentProfile();

                        }
                    }
                }
            }
        });
    }
    private void getDataExtras(Bundle extras) {
        student = (Student) extras.getSerializable("Student");
    }

    private void initView() {
        studentName = findViewById(R.id.studentName);
        progressBar = findViewById(R.id.loadingPanel);
        studentName.setText(student.getUsername().toString());
        imStudentProfile = findViewById(R.id.person);
        Handler handler = new Handler();
        handler.postDelayed(() -> updateProgressBar(), 2000);
    }
    private void updateProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    public void Training(View view) {
        Intent intent = new Intent(this, StudentTrainingActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
    }

    public void Calendar(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
        finish();
    }

    public void Messages(View view) {
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
    }

    public void Settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
    }

    public void Logout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
