package com.example.fitnesstrack.views;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.fitnesstrack.R;

import com.example.fitnesstrack.register.StudentRegistrationActivity;
import com.example.fitnesstrack.register.TeacherRegistrationActivity;

public class RegisterActivity extends MyBaseActivity implements DatePickerFragment.OnDateSelectedListener{
    private ImageView imgCoach;
    private ImageView imgStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        onClickListener();
    }

    private void onClickListener() {
        imgCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Öğretmen kayıt ekranına geçiş
                rotateImage(imgCoach, new Intent(RegisterActivity.this, TeacherRegistrationActivity.class));
            }
        });

        imgStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Öğrenci kayıt ekranına geçiş
                rotateImage(imgStudent, new Intent(RegisterActivity.this, StudentRegistrationActivity.class));
            }
        });
    }

    private void initView() {
        imgStudent = findViewById(R.id.imgStudent);
        imgCoach = findViewById(R.id.imgCoach);

    }
    private void rotateImage(final ImageView imageView, final Intent intent) {
        // Küçültme animasyonu
        ObjectAnimator scaleDownAnimator = ObjectAnimator.ofPropertyValuesHolder(
                imageView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f)
        );
        scaleDownAnimator.setDuration(500);
        scaleDownAnimator.setInterpolator(new DecelerateInterpolator());

        // Dönme animasyonu
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2000);
        rotateAnimator.setInterpolator(new DecelerateInterpolator());

        // Büyütme animasyonu
        ObjectAnimator scaleUpAnimator = ObjectAnimator.ofPropertyValuesHolder(
                imageView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
        );
        scaleUpAnimator.setDuration(500);
        scaleUpAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleDownAnimator, rotateAnimator, scaleUpAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(intent);
            }
        });

        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onDateSelected(int year, int month, int day) {

    }
}
