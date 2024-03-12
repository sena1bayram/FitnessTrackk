package com.example.fitnesstrack.views;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.adapters.StudentAdapter;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import com.makeramen.roundedimageview.RoundedImageView;


import org.aviran.cookiebar2.CookieBar;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TeacherStudentSelectionActivity extends MyBaseActivity {
    private ListView listViewStudents;
    private CookieBar currentCookieBar;

    private ImageView refreshImageView;

    private TextView txtTeacherName;
    private TextView tvStudentChoose;
    private Button btnSkip;
    private Coach coach;
    List<Student> studentList;

    private RoundedImageView imCoachProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_selection_activity);
        getDataExtras(getIntent().getExtras());
        initComponent();
        initCoachProfile();
        initOnclickListener();
        initLoadStudents();
    }

    private void initCoachProfile() {
        if(!coach.getPersonProfileImage().isEmpty()) {
            byte[] bytes = Base64.decode(coach.getPersonProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imCoachProfile.setImageBitmap(bitmap);
        }
    }

    private void getDataExtras(Bundle extras) {
        coach = (Coach) extras.getSerializable("Coach");
    }

    private void initLoadStudents() {
        // Öğrencileri Firestore'dan al ve listele
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentCollection = db.collection("FitnessTrackStudent");
        Query studentQuery = studentCollection;
        studentQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                studentList = new ArrayList<>();
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

                        Student student = new Student(studentId, studentAddres,studentBirthday,
                                studentEmail,studentGender, studentHeight, studentPassword,
                                studentTeacherId,studentType,studentUsername, studentWeight,
                                studentGroupId, studentProfileImage, studentRegisteredDate);
                        if(studentTeacherId.equals("null"))
                            studentList.add(student);
                    }

                    // Öğrenci listesini ListView'e bağla
                    StudentAdapter studentAdapter = new StudentAdapter(TeacherStudentSelectionActivity.this, studentList);
                    listViewStudents.setAdapter(studentAdapter);
                }
                if(studentList.size() == 0){
                    cookieBar_error_create(getString(R.string.warning), getString(R.string.no_idle_students));
                }
            } else {
                cookieBar_error_create(getString(R.string.error), getString(R.string.error_occurred));
            }
        });
    }

    private void cookieBar_error_create(String messageType, String message) {
        // Mevcut CookieBar'ı kontrol edin ve kapatın
        if (currentCookieBar != null) {
            currentCookieBar.dismiss();
        }
        currentCookieBar = CookieBar.build(this)
                .setTitle(messageType)
                .setMessage(message)
                .setMessageColor(R.color.fancyMessage)
                .setBackgroundColor(R.color.cookiebar)
                .setIcon(R.drawable.ic_launcher_foreground_snackbar)
                .setDuration(5000)
                .setCookiePosition(CookieBar.BOTTOM)
                .setAction(R.string.Close, () -> CookieBar.dismiss(TeacherStudentSelectionActivity.this))
                .show();
    }

    private void initOnclickListener() {
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            //Intent triaj= new Intent(DoctorActivity.this, TriajActivity.class);
            ImageView imageView = view.findViewById(R.id.imgAddStudent);
            TextView ad = view.findViewById(R.id.textViewUsername);



            imageView.setOnClickListener(v -> new SweetAlertDialog(TeacherStudentSelectionActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.Are_you_sure))
                    .setContentText(getString(R.string.Wont_be_able_to_recover_this_file))
                    .setCancelText(getString(R.string.No_cancel_plx))
                    .setConfirmText(getString(R.string.Yes_delete_it))
                    .showCancelButton(true)
                    .setCancelClickListener(sDialog -> {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.setTitleText(getString(R.string.cancelled))
                                .setContentText(getString(R.string.Your_imaginary_file_is_safe))
                                .setConfirmText(getString(R.string.ok))
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                        // or you can new a SweetAlertDialog to show
               /* sDialog.dismiss();
                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Cancelled!")
                        .setContentText("Your imaginary file is safe :)")
                        .setConfirmText("OK")
                        .show();*/
                    })
                    .setConfirmClickListener(sDialog -> {
                        sDialog.setTitleText(getString(R.string.Added))
                                .setContentText(getString(R.string.imaginary_file_has_been_deleted))
                                .setConfirmText(getString(R.string.ok))
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        // Update the student's teacherId field with the coach's documentId
                        Student selectedStudent = studentList.get(i);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference studentRef = db.collection("FitnessTrackStudent").document(selectedStudent.getStudentId());
                        studentRef.update("teacherId", coach.getCoachId())
                                .addOnSuccessListener(aVoid -> {
                                    cookieBar_error_create(getString(R.string.warning), getString(R.string.student_teacherId_updated));
                                    loadStudents();
                                })
                                .addOnFailureListener(e -> cookieBar_error_create(getString(R.string.error), getString(R.string.failede_to_update_Student_coachId)));

                        DocumentReference teacherRef = db.collection("FitnessTrackTeacher").document(coach.getCoachId());
                        teacherRef.update("studentIds", FieldValue.arrayUnion(selectedStudent.getStudentId()))
                                .addOnSuccessListener(aVoid -> cookieBar_error_create(getString(R.string.warning), getString(R.string.student_added_to_teachers_studentIds)))
                                .addOnFailureListener(e -> cookieBar_error_create(getString(R.string.error), getString(R.string.failed_to_add_student_to_coachs_student_id_list)));
                    })
                    .show());
        });
        refreshImageView.setOnClickListener(view -> loadStudents());
        btnSkip.setOnClickListener(view -> {
            Intent Coach= new Intent(TeacherStudentSelectionActivity.this, CoachDashboardActivity.class);
            Coach.putExtra("Coach", coach);
            startActivity(Coach);
        });
    }

    private void loadStudents() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);

        // Döndürme animasyonu oluştur
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);

        // Küçültme animasyonu oluştur
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation2.setDuration(1000);

        // Animasyonları birleştir
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation2);

        // Butona animasyonu uygula

        // Butona animasyonu uygula
        refreshImageView.startAnimation(animationSet);
        initLoadStudents();
    }

    private void initComponent() {
        listViewStudents = findViewById(R.id.idLVLanguages);
        refreshImageView = findViewById(R.id.imgRefresh);
        txtTeacherName = findViewById(R.id.idCoach);
        tvStudentChoose = findViewById(R.id.idEdtItemName);
        tvStudentChoose.setTextColor(getResources().getColor(R.color.white));
        txtTeacherName.setText(getString(R.string.coach) +" " + coach.getpersonName());
        studentList = new ArrayList<>();
        btnSkip = findViewById(R.id.btnSkip);
        imCoachProfile = findViewById(R.id.imgCoachProfile);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}