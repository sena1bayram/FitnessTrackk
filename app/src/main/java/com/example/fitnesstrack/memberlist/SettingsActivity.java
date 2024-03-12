package com.example.fitnesstrack.memberlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.aviran.cookiebar2.CookieBar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_SELECTION = 1;
    private ImageView imgAvatar;

    private Button btnSave;
    private FrameLayout editFrame;

    private Student student;
    private Coach coach;
    private TextView txtUsername;
    private TextView tCity;
    private TextView txtAbout;

    private CookieBar currentCookieBar;
    private String encodedImage = "";

    private int personType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        txtUsername = findViewById(R.id.txtUsername);
        tCity = findViewById(R.id.tCity);
        txtAbout = findViewById(R.id.txtAbout);
        editFrame = findViewById(R.id.editFrame);
        btnSave = findViewById(R.id.btnSaveChanges);
        imgAvatar = findViewById(R.id.imgAvatar);

        if (intent.hasExtra("Student")) {
            personType=0;
            student = (Student) getIntent().getExtras().getSerializable("Student");

            txtUsername.setText(student.getUsername());
            tCity.setText(student.getAddress());
            txtAbout.setText(student.getUsername() + " " + student.getWeight());


            if (!student.getStudentProfileImage().isEmpty()) {
                byte[] bytes = Base64.decode(student.getStudentProfileImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgAvatar.setImageBitmap(bitmap);
            }
        }

        else {

            if (intent.hasExtra("Coach")) {
                personType=1;
                coach = (Coach) getIntent().getExtras().getSerializable("Coach");

                txtUsername.setText(coach.getpersonName());

                tCity.setText(coach.getpersonEmail());

                txtAbout.setText(coach.getpersonName());



                if (!coach.getPersonProfileImage().isEmpty()) {
                    byte[] bytes = Base64.decode(coach.getPersonProfileImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAvatar.setImageBitmap(bitmap);
                }
            }
        }


        editFrame.setOnClickListener(this);
        btnSave.setOnClickListener(this);


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editFrame:
                chooseProfileImage();
                break;
            case R.id.btnSaveChanges:
                saveChanges();
                break;
        }
    }

    private void saveChanges() {

        if(personType==0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference studentRef = db.collection("FitnessTrackStudent").document(student.getStudentId());
            studentRef.update("image", encodedImage)
                    .addOnSuccessListener(aVoid -> {
                        cookieBar_error_create(getString(R.string.warning), getString(R.string.profile_update_process_was_completed));
                    })
                    .addOnFailureListener(e -> cookieBar_error_create(getString(R.string.error), getString(R.string.an_unexpected_error_occurred)));
        }

        if(personType==1){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference studentRef = db.collection("FitnessTrackTeacher").document(coach.getCoachId());
            studentRef.update("image", encodedImage)
                    .addOnSuccessListener(aVoid -> {
                        cookieBar_error_create(getString(R.string.warning), getString(R.string.profile_update_process_was_completed));
                    })
                    .addOnFailureListener(e -> cookieBar_error_create(getString(R.string.error), getString(R.string.an_unexpected_error_occurred)));
        }

    }

    private void chooseProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_SELECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK && data != null) {
            // Retrieve the selected image URI
            Uri selectedImageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatar.setImageBitmap(bitmap);
                encodedImage = encodeImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 300;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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
                .setAction(R.string.Close, () -> CookieBar.dismiss(this))
                .show();
    }


}