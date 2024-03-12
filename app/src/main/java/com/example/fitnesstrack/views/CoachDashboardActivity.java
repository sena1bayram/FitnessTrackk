package com.example.fitnesstrack.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.memberlist.CoachMemberlistActivity;
import com.example.fitnesstrack.memberlist.CoachMessagesActivity;
import com.example.fitnesstrack.memberlist.CoachTrainingActivity;
import com.example.fitnesstrack.memberlist.SettingsActivity;
import com.example.fitnesstrack.models.Coach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;


public class CoachDashboardActivity extends MyBaseActivity {

    private Coach coach;
    private TextView DisplayName;

    private ProgressBar progressBar;
    private RoundedImageView imCoachProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_dashboard);
        getDataExtras(getIntent().getExtras());
        initComponent();
        initCoachProfile();
    }

    private void initCoachProfile() {
        if(!coach.getPersonProfileImage().isEmpty()) {
            byte[] bytes = Base64.decode(coach.getPersonProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imCoachProfile.setImageBitmap(bitmap);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchGetCoachData();
    }

    private void fetchGetCoachData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Öğretmen koleksiyonunda kontrol et
        CollectionReference teacherCollection = db.collection("FitnessTrackTeacher");
        Query teacherQuery = teacherCollection.whereEqualTo("email", coach.getpersonEmail());
        teacherQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String CoachId = documentSnapshot.getId();
                            String CoachEmail = documentSnapshot.getString("email");
                            String CoachPassword = documentSnapshot.getString("password");
                            String CoachType = documentSnapshot.getString("type");
                            String CoachUsername = documentSnapshot.getString("username");
                            String CoachUserProfile = documentSnapshot.getString("image");
                            String CoachRegisteredDate = documentSnapshot.getString("registeredDate");
                            Coach newCoach = new Coach(CoachId,CoachEmail, CoachUsername, CoachPassword,
                                    CoachType,  CoachUserProfile, CoachRegisteredDate);
                            coach = newCoach;
                            initCoachProfile();
                        }
                    }
                }
            }
        });
    }

    private void initComponent() {
        findViewById(R.id.header).setVisibility(View.GONE);
        findViewById(R.id.big_screen).setVisibility(View.GONE);
        DisplayName =  findViewById(R.id.name);
        DisplayName.setText(coach.getpersonName());
        findViewById(R.id.header).setVisibility(View.VISIBLE);
        findViewById(R.id.big_screen).setVisibility(View.VISIBLE);
        imCoachProfile = findViewById(R.id.person);
        progressBar = findViewById(R.id.loadingPanel);
        Handler handler = new Handler();
        handler.postDelayed(() -> updateProgressBar(), 2000);
    }
    private void updateProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    private void getDataExtras(Bundle extras) {
        coach = (Coach) extras.getSerializable("Coach");
    }

    // eğer Logout butonu tıklanırsa
    public void Logout(View v) {
        // TODO: Logout
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void CoachTraining(View v) {
        Intent intent = new Intent(this, CoachTrainingActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
    }


    // si le menu messages est choisi
    public void CoachMessages(View v) {
       Intent intent = new Intent(this, CoachMessagesActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
    }
    public void Settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
    }


    // si le menu membres est choisi
    public void CoachMemberlist(View v) {
        Intent intent = new Intent(this, CoachMemberlistActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TeacherStudentSelectionActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
        finish();
    }
}