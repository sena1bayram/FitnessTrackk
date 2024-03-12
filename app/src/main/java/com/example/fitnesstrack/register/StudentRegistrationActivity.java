package com.example.fitnesstrack.register;

import static com.example.fitnesstrack.constants.IConstants.EXTRA_ADDRESS;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_BIRTHDATE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_EMAIL;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_GENDER;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_GROUP_ID;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_HEIGHT;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_PASSWORD;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_STUDENT_REGISTERED_DATE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_TEACHER_ID;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USERNAME;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USER_IMAGE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_USER_TYPE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_WEIGHT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
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
import android.os.Vibrator;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.managers.Utils;
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
import com.shawnlin.numberpicker.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StudentRegistrationActivity extends MyBaseActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_SELECTION = 1;
    private TextView txtStudentRegister;
    private EditText etStudentMail;
    private EditText etStudentUsername;
    private EditText etStudentPassword, etStudentPasswordRepeated;
    private EditText etStudentBirthDay;
    private CheckBox checkBoxStudent;
    private Button btnRegisterStudent, btnStudentPersonalDetails;

    private Drawable showPasswordIcon;
    private Drawable hidePasswordIcon;

    private Drawable passwordIcon;
    private boolean isPasswordVisible = false;
    NumberPicker weightPicker, heightPicker, genderPicker;
    // Vibration duration in milliseconds
    private static final int VIBRATION_DURATION = 50;
    private ConnectivityManager connectivityManager;

    public FirebaseAuth mAuth; //FirebaseAuth init
    public FirebaseUser mUser; //FirebaseUser init
    public DatabaseReference mReference; //DatabaseReference init

    public FirebaseFirestore mFirestore; //DatabaseReference init

    public HashMap<String, Object> mData = new HashMap<>();

    private String global_birthdate;
    private String global_address;
    private String global_weight;
    private String global_height;
    private  String global_name;
    private  String global_surname;

    private String global_gender;

    private TextView txtAddImage;
    private RoundedImageView profileImageView;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        initView();
        checkInternetConnection(isInternetAvailable());
        profileImageView.setOnClickListener(this);
        onClickListener();
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

        Snackbar snackbar = Snackbar.make(findViewById(R.id.btnRegisterStudent), message, duration);
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
        etStudentPasswordRepeated.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int iconRightBound = etStudentPasswordRepeated.getRight()
                            - etStudentPasswordRepeated.getCompoundDrawables()[2].getBounds().width();

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
            etStudentPasswordRepeated.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etStudentPasswordRepeated.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, showPasswordIcon, null);
        } else {
            // Şifre gösteriliyken, gizle
            etStudentPasswordRepeated.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etStudentPasswordRepeated.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, hidePasswordIcon, null);
        }

        // Şifre metninin sonuna git
        etStudentPasswordRepeated.setSelection(etStudentPasswordRepeated.getText().length());

        // Şifre görünürlüğü durumunu güncelle
        isPasswordVisible = !isPasswordVisible;
    }

    private void onClickListener() {
        btnRegisterStudent.setOnClickListener(this);
        etStudentBirthDay.setOnClickListener(view -> {
            // Get the Vibrator service
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            // Check if the device has a vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                // Vibrate for the specified duration
                vibrator.vibrate(VIBRATION_DURATION);
            }
            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

                Calendar currentDate = Calendar.getInstance();

                if (selectedDate.after(currentDate)) {
                    // Gelecekteki bir tarih seçildiğinde bir hata mesajı gösterin
                    Toast.makeText(getApplicationContext(), getString(R.string.select_a_valid_date_of_birth), Toast.LENGTH_SHORT).show();
                } else {
                    String date = String.format("%02d.%02d.%d", dayOfMonth, monthOfYear + 1, year);
                    etStudentBirthDay.setText(date);

                    global_birthdate = etStudentBirthDay.getText().toString().trim();
                }
            };

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        btnStudentPersonalDetails.setOnClickListener(this);
    }

    private void initView() {
        txtStudentRegister = findViewById(R.id.txt_student_register);
        etStudentMail = findViewById(R.id.et_student_mail);
        etStudentUsername = findViewById(R.id.et_student_username);
        etStudentPassword = findViewById(R.id.et_student_password);
        checkBoxStudent = findViewById(R.id.checkBox_student);
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        etStudentBirthDay = findViewById(R.id.et_student_birthday);
        btnStudentPersonalDetails = findViewById(R.id.btnPersonalDetails);
        etStudentPasswordRepeated = findViewById(R.id.et_student_password_repeated);
        showPasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_24dp);
        hidePasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_off_24dp);
        passwordIcon = getResources().getDrawable(R.drawable.ic_passbox);
        txtAddImage = findViewById(R.id.txtAddImage);
        profileImageView = findViewById(R.id.imageProfileAdd);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRegisterStudent) {
            registerStudent();
        }
        if(view.getId() == R.id.btnPersonalDetails){
            calculateStudentPersonalDetails();
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
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 300;
        int previewHeight = bitmap.getHeight()*previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void calculateStudentPersonalDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_personal_details, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        NumberPicker weightPickerDialog = dialogView.findViewById(R.id.np_weight);
        NumberPicker heightPickerDialog = dialogView.findViewById(R.id.np_height);
        NumberPicker genderPickerDialog = dialogView.findViewById(R.id.np_gender);
        ImageView imv_exit = dialogView.findViewById(R.id.iv_exit);
        Button btnDialogCalculate = dialogView.findViewById(R.id.btn_save);
        EditText et_adr = dialogView.findViewById(R.id.et_address);
        EditText et_name = dialogView.findViewById(R.id.et_first_name);
        EditText et_surname = dialogView.findViewById(R.id.et_last_name);

        weightPickerDialog.setMinValue(50);
        weightPickerDialog.setMaxValue(150);

        heightPickerDialog.setMinValue(50);
        heightPickerDialog.setMaxValue(200);

        genderPickerDialog.setMinValue(0);
        genderPickerDialog.setMaxValue(2);
        genderPickerDialog.setDisplayedValues(new String[]{getString(R.string.male), getString(R.string.female),
                getString(R.string.sexless)});

        imv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDialogCalculate.setOnClickListener(view -> {
            int weight = weightPickerDialog.getValue();
            int height = heightPickerDialog.getValue();
            String gender = getString(R.string.male);
            String name = et_name.getText().toString().trim();
            String surname = et_surname.getText().toString().trim();
            String address = et_adr.getText().toString().trim();

            if (name.isEmpty()) {
                et_name.setError(getString(R.string.name_cannot_be_empty));
                return;
            }

            if (surname.isEmpty()) {
                et_surname.setError(getString(R.string.surname_cannot_be_empty));
                return;
            }

            if (address.isEmpty()) {
                et_adr.setError(getString(R.string.address_cannot_be_empty));
                return;
            }

            if (genderPickerDialog.getValue() == 0)
                gender = getString(R.string.male);
            else if (genderPickerDialog.getValue() == 1)
                gender = getString(R.string.female);
            else if (genderPickerDialog.getValue() == 2)
                gender = getString(R.string.sexless);

            global_gender = gender;
            double heightInMeters = height / 100.0;
            double bmi = weight / (heightInMeters * heightInMeters);

            global_height = String.valueOf(height);
            global_weight = String.valueOf(weight);
            global_address = address;
            global_name = name;
            global_surname = surname;

            String resultMessage = String.format(getString(R.string.body_mass_index) + ": %.2f", bmi);
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }
    private void registerStudent() {
        String userMail = etStudentMail.getText().toString().trim();
        String userName = etStudentUsername.getText().toString().trim();
        String userPassword = etStudentPassword.getText().toString().trim();
        String userPasswordRepeated = etStudentPasswordRepeated.getText().toString().trim();
        String userBirthdate = etStudentBirthDay.getText().toString().trim();

        if (!Validation.validateEmail(userMail)) {
            etStudentMail.setError(getString(R.string.user_email_is_not_in_proper_format));
            etStudentMail.requestFocus();
            return;
        }
        if (!Validation.validateFields(userName)) {
            etStudentUsername.setError(getString(R.string.password_is_required));
            etStudentUsername.requestFocus();
            return;
        }
        if (!Validation.strongValidatePassword(userPassword)) {
            etStudentPassword.setError(getString(R.string.password_control));
            etStudentPassword.requestFocus();
            return;
        }
        if (!validatePassword(userPassword, userPasswordRepeated)) {
            Toast.makeText(this, getString(R.string.your_passwords_do_not_match), Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkBoxStudent.isChecked()) {
            Toast.makeText(this, getString(R.string.checkbox_not_selected_warning), Toast.LENGTH_LONG).show();
            return;
        }
        if(encodedImage == null){
            Toast.makeText(this, getString(R.string.select_image_profile), Toast.LENGTH_LONG).show();
            return;
        }
        if (checkBoxStudent.isChecked()) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            progressDialog = AppUtils.dialog(StudentRegistrationActivity.this, dm);
            progressDialog.show();

            if (userBirthdate.isEmpty()) {
                etStudentBirthDay.setError(getString(R.string.date_of_birth_cannot_be_empty));
                etStudentBirthDay.requestFocus();
                progressDialog.dismiss();
                return;
            }


            ///////////////////////
            //FirebaseAuth ile email,parola parametrelerini kullanarak yeni bir kullanıcı oluşturuyoruz.
            if (!global_address.equals("") && !global_weight.equals("")) {
                mAuth.createUserWithEmailAndPassword(userMail, userPassword)
                        .addOnCompleteListener(StudentRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //İşlem başarısız olursa kullanıcıya bir Toast mesajıyla bildiriyoruz.
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.registration_completed_successfully), Toast.LENGTH_LONG).show();
                                    mUser = mAuth.getCurrentUser();

                                    mData.put(EXTRA_EMAIL, userMail);
                                    mData.put(EXTRA_USERNAME, Utils.getCapsWord(userName));
                                    mData.put(EXTRA_PASSWORD, userPassword);
                                    mData.put(EXTRA_BIRTHDATE, userBirthdate);
                                    mData.put(EXTRA_ADDRESS, global_address);
                                    mData.put(EXTRA_WEIGHT, global_weight);
                                    mData.put(EXTRA_HEIGHT, global_height);
                                    mData.put(EXTRA_GENDER, global_gender);
                                    mData.put(EXTRA_USER_TYPE, "student");
                                    mData.put(EXTRA_TEACHER_ID, "null");
                                    mData.put(EXTRA_GROUP_ID, "null");
                                    mData.put(EXTRA_USER_IMAGE, encodedImage);
                                    mData.put(EXTRA_STUDENT_REGISTERED_DATE, new Date().toString());

                                    mFirestore.collection("FitnessTrackStudent").document(mUser.getUid()).set(mData).addOnCompleteListener(
                                            StudentRegistrationActivity.this, (task1) -> {
                                                if (task1.isSuccessful()) {
                                                    startActivity(new Intent(StudentRegistrationActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(StudentRegistrationActivity.this, getString(R.string.fail_to_add_data), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.address_cannot_be_empty), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
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

}