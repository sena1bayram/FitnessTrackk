package com.example.fitnesstrack.register;

import static com.example.fitnesstrack.constants.IConstants.EXTRA_COACH_REGISTERED_DATE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_EMAIL;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_GROUP_ID;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_PASSWORD;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_STUDENT_IDS;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USERNAME;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USER_IMAGE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USER_TYPE;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.managers.Utils;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.utils.Validation;
import com.example.fitnesstrack.views.AppUtils;
import com.example.fitnesstrack.views.MainActivity;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TeacherRegistrationActivity extends MyBaseActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_SELECTION = 1;
    private Button btnRegisterCoach;
    private EditText etCoachRegister;
    private EditText etCoachUsername;
    private EditText etCoachPassword, etCoachPasswordRepeated;
    private CheckBox checkBoxCoach;
    private TextView txtCoachRegister;

    private Drawable showPasswordIcon;
    private Drawable hidePasswordIcon;

    private Drawable passwordIcon;
    private boolean isPasswordVisible = false;
    private ConnectivityManager connectivityManager;

    public FirebaseAuth mAuth; //FirebaseAuth init
    public FirebaseUser mUser; //FirebaseUser init
    public DatabaseReference mReference; //DatabaseReference init

    public FirebaseFirestore mFirestore; //DatabaseReference init

    public HashMap<String, Object> mData = new HashMap<>();
    Coach coach;

    // Declare the RoundedImageView as a member variable
    private TextView txtAddImage;
    private RoundedImageView profileImageView;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);
        initView();
        checkInternetConnection(isInternetAvailable());
        btnRegisterCoach.setOnClickListener(this);
        profileImageView.setOnClickListener(this);
        onTouchListener();
    }

    private void checkInternetConnection(boolean internetAvailable) {
        showSnackBar(internetAvailable);

    }
    private void showSnackBar(boolean isConnected) {
        String message;
        int duration;
        int textColor;
        String actionText;
        View.OnClickListener actionClickListener;
        if (isConnected) {
            message = "Connected to Internet";
            duration = Snackbar.LENGTH_SHORT;
            textColor = Color.WHITE;
            actionText = "";
            actionClickListener = null;
        } else {
            message = "No internet connection!";
            duration = Snackbar.LENGTH_LONG;
            textColor = Color.RED;
            actionText = getString(R.string.retry);
            actionClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS ));
                }
            };
        }

        Snackbar snackbar = Snackbar.make(findViewById(R.id.btnRegisterCoach), message, duration);
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarText.setTextColor(textColor);
        if (!actionText.equals("")) {
            snackbar.setAction(actionText, actionClickListener);
            snackbar.show();
        }
    }

    public boolean isInternetAvailable() {
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
    private void onTouchListener() {
        etCoachPasswordRepeated.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int iconRightBound = etCoachPasswordRepeated.getRight()
                            - etCoachPasswordRepeated.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= iconRightBound) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Şifre gizliyken, göster
            etCoachPasswordRepeated.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etCoachPasswordRepeated.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, showPasswordIcon, null);
        } else {
            // Şifre gösteriliyken, gizle
            etCoachPasswordRepeated.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etCoachPasswordRepeated.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, hidePasswordIcon, null);
        }

        // Şifre metninin sonuna git
        etCoachPasswordRepeated.setSelection(etCoachPasswordRepeated.getText().length());

        // Şifre görünürlüğü durumunu güncelle
        isPasswordVisible = !isPasswordVisible;
    }

    private void initView() {
        btnRegisterCoach = findViewById(R.id.btnRegisterCoach);
        etCoachRegister = findViewById(R.id.et_coach_register);
        etCoachUsername = findViewById(R.id.et_coach_Username);
        etCoachPassword = findViewById(R.id.et_coach_Password);
        checkBoxCoach = findViewById(R.id.checkBoxCoach);
        txtCoachRegister = findViewById(R.id.txt_coach_register);
        etCoachPasswordRepeated = findViewById(R.id.et_coach_Password_repeated);
        profileImageView = findViewById(R.id.imageProfileAdd);
        txtAddImage = findViewById(R.id.txtAddImage);
        showPasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_24dp);
        hidePasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_off_24dp);
        passwordIcon = getResources().getDrawable(R.drawable.ic_passbox);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //coach = new Coach();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRegisterCoach) {
            registerCoach();
        }
        if(view.getId() == R.id.imageProfileAdd){
            chooseProfileImage();
        }
    }

    private void chooseProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_SELECTION);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK && data != null) {
            // Retrieve the selected image URI
            Uri selectedImageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImageView.setImageBitmap(bitmap);
                txtAddImage.setVisibility(View.GONE);
                encodedImage = encodeImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void registerCoach() {
        String userMail = etCoachRegister.getText().toString().trim();
        String userName = etCoachUsername.getText().toString().trim();
        String userPassword = etCoachPassword.getText().toString().trim();
        String userPasswordRepeated = etCoachPasswordRepeated.getText().toString().trim();

        if (!Validation.validateEmail(userMail)) {
            etCoachRegister.setError(getString(R.string.user_email_is_not_in_proper_format));
            etCoachRegister.requestFocus();
            return;
        }
        if (!Validation.validateFields(userName)) {
            etCoachUsername.setError(getString(R.string.password_is_required));
            etCoachUsername.requestFocus();
            return;
        }
        if (!Validation.strongValidatePassword(userPassword)) {
            etCoachPassword.setError(getString(R.string.password_control));
            etCoachPassword.requestFocus();
            return;
        }
        if(!validatePassword(userPassword, userPasswordRepeated)){
            Toast.makeText(this, getString(R.string.your_passwords_do_not_match), Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkBoxCoach.isChecked()) {
            Toast.makeText(this, getString(R.string.checkbox_not_selected_warning), Toast.LENGTH_LONG).show();
            return;
        }
        if(encodedImage == null){
            Toast.makeText(this, getString(R.string.select_image_profile), Toast.LENGTH_LONG).show();
            return;
        }
        if (checkBoxCoach.isChecked()) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            progressDialog = AppUtils.dialog(TeacherRegistrationActivity.this,dm);
            progressDialog.show();

            ///////////////////////
            //FirebaseAuth ile email,parola parametrelerini kullanarak yeni bir kullanıcı oluşturuyoruz.
            mAuth.createUserWithEmailAndPassword(userMail,userPassword)
                    .addOnCompleteListener(TeacherRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //İşlem başarısız olursa kullanıcıya bir Toast mesajıyla bildiriyoruz.
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.registration_completed_successfully), Toast.LENGTH_LONG).show();
                                mUser = mAuth.getCurrentUser();

                                mData.put(EXTRA_EMAIL, userMail);
                                mData.put(EXTRA_USERNAME, Utils.getCapsWord(userName));
                                mData.put(EXTRA_PASSWORD, userPassword);
                                mData.put(EXTRA_USER_TYPE, "teacher");
                                mData.put(EXTRA_STUDENT_IDS, new ArrayList<String>());
                                mData.put(EXTRA_GROUP_ID, new ArrayList<String>());
                                mData.put(EXTRA_USER_IMAGE, encodedImage);
                                mData.put(EXTRA_COACH_REGISTERED_DATE, new Date().toString());
                                mFirestore.collection("FitnessTrackTeacher").document(mUser.getUid()).set(mData).addOnCompleteListener(
                                        TeacherRegistrationActivity.this, (task1) -> {
                                            if(task.isSuccessful()){
                                                Toast.makeText(TeacherRegistrationActivity.this, getString(R.string.registration_completed_successfully), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TeacherRegistrationActivity.this, MainActivity.class));
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(TeacherRegistrationActivity.this, getString(R.string.fail_to_add_data), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }}).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            progressDialog.dismiss();
                        }
                    });
            //////////////////////
        }
    }
    private boolean validatePassword(String password, String repeatedPassword) {
        // Parolaların boş olup olmadığını kontrol edin
        if (password.isEmpty() || repeatedPassword.isEmpty()) {
            return false;
        }

        // Parolaların eşleşip eşleşmediğini kontrol edin
        if (!password.equals(repeatedPassword)) {
            return false;
        }

        // Diğer gereksinimleri kontrol edebilirsiniz (örneğin, büyük harf, küçük harf, sayı gereksinimleri)

        // Tüm kontroller başarılıysa true dönün
        return true;
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 300;
        int previewHeight = bitmap.getHeight()*previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}