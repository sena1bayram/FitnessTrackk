package com.example.fitnesstrack.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.network.ConnectionReceiver;
import com.example.fitnesstrack.utils.BuildConfig;
import com.example.fitnesstrack.R;
import com.example.fitnesstrack.utils.CustomAdapter;
import com.example.fitnesstrack.utils.SharedPrefencesEngine;
import com.example.fitnesstrack.utils.Validation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.aviran.cookiebar2.CookieBar;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends MyBaseActivity implements View.OnClickListener, ConnectionReceiver.ReceiverListener{
    private Button mBtnSigIn,mTxtNewUser;
    private CookieBar currentCookieBar;
    private TextView versText, SignIn, forgotPassword;
    private EditText tvMail, tvPassword;

    private CheckBox checkBoxRememberMe;
    Spinner mSpinner;
    String[] spinnerTitles;
    int[] spinnerImages;
    boolean isConnected;
    private ConnectivityManager connectivityManager;
    private int local_language_index;
    private Locale mCurrentLocale;
    private boolean isUserInteracting;

    private Drawable showPasswordIcon;
    private Drawable hidePasswordIcon;

    private Drawable passwordIcon;
    private boolean isPasswordVisible = false;

    private boolean rememberMe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        initView();
        cookieBar_create(getString(R.string.tedavi_icin_degil), getString(R.string.bu_uygulamada_sunulan));
        checkInternetConnection(isInternetAvailable());

        CustomAdapter mCustomAdapter = new CustomAdapter(MainActivity.this, spinnerTitles, spinnerImages);
        mCustomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mCustomAdapter);

        String language_lbl = spref().getAppLanguage(SharedPrefencesEngine.APP_LANGUAGE);

        if (Objects.equals(language_lbl, ""))
             language_lbl = "en";

        local_language_index = language_index(language_lbl);
        mSpinner.setSelection(local_language_index);

        mTxtNewUser.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        mBtnSigIn.setOnClickListener(this);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isUserInteracting) {
                    local_language_index = i;
                    Locale locale = new Locale(choose_language(local_language_index)); // where 'hi' is Language code, set this as per your Spinner Item selected
                    Locale.setDefault(locale);

                    spref().setAppLanguage(SharedPrefencesEngine.APP_LANGUAGE, choose_language(local_language_index));

                    //Toast.makeText(MainActivity.this, "locale: " + locale.getISO3Language(), Toast.LENGTH_SHORT).show();
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().createConfigurationContext(config);
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                    cookieBar_create(getString(R.string.tedavi_icin_degil), getString(R.string.bu_uygulamada_sunulan));
                    mBtnSigIn.setText(getString(R.string.login));
                    mTxtNewUser.setText(getString(R.string.sign_up));
                    SignIn.setText(getString(R.string.login_panel));
                    forgotPassword.setText(getString(R.string.forgot_password));
                    tvMail.setHint(getString(R.string.email_address));
                    tvPassword.setHint(getString(R.string.password));
                    checkBoxRememberMe.setText(getString(R.string.remember_me));

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // on below line we are setting selection for our spinner to spinner position.

            }
        });

        onTouchListener();
    }

    private void onTouchListener() {
        tvPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int iconRightBound = tvPassword.getRight()
                            - tvPassword.getCompoundDrawables()[2].getBounds().width();

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
            tvPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            tvPassword.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, showPasswordIcon, null);
        } else {
            // Şifre gösteriliyken, gizle
            tvPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            tvPassword.setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, hidePasswordIcon, null);
        }

        // Şifre metninin sonuna git
        tvPassword.setSelection(tvPassword.getText().length());

        // Şifre görünürlüğü durumunu güncelle
        isPasswordVisible = !isPasswordVisible;
    }

    private String choose_language(int localLanguageIndex) {
        String language_abbreviation="";

        switch (localLanguageIndex) {
            case 0:
                language_abbreviation = "en";
                break;
            case 1:
                language_abbreviation = "tr";
                break;
            case 2:
                language_abbreviation = "ar";
                break;
            case 3:
                language_abbreviation = "fr";
                break;
        }
        spref().setAppLanguage(SharedPrefencesEngine.APP_LANGUAGE, language_abbreviation);

        return language_abbreviation;
    }

    public void initView(){
        versText = findViewById(R.id.versText);
        mSpinner = findViewById(R.id.spinner);
        mBtnSigIn = findViewById(R.id.btnSigIn);
        mTxtNewUser = findViewById(R.id.txtNewUser);
        SignIn = findViewById(R.id.txt1);
        tvMail = findViewById(R.id.txtEmail);
        tvPassword = findViewById(R.id.txtPassword);
        forgotPassword = findViewById(R.id.txtForgotPassword);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mCurrentLocale = getResources().getConfiguration().locale;
        showPasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_24dp);
        hidePasswordIcon = getResources().getDrawable(R.drawable.ic_visibility_off_24dp);
        passwordIcon = getResources().getDrawable(R.drawable.ic_passbox);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        /*********************************************************/

        /*********************************************************/

        versText.setText("v" + BuildConfig.VERSION_NAME);
        spinnerTitles = new String[]{"", "", "", ""};
        spinnerImages = new int[]{R.drawable.flag_australia
                ,R.drawable.flag_tr
                ,R.drawable.flag_arabic
                ,R.drawable.flag_france};

        if(!spref().getStringPreferences(SharedPrefencesEngine.USER_LOGIN_PASSWORD).trim().equals("")){
            tvPassword.setText(spref().getStringPreferences(SharedPrefencesEngine.USER_LOGIN_PASSWORD).trim());
        }
        if(!spref().getStringPreferences(SharedPrefencesEngine.USER_LOGIN_MAIL).trim().equals("") &&
                !spref().getStringPreferences(SharedPrefencesEngine.USER_LOGIN_MAIL).trim().equals("empty")){
            tvMail.setText(spref().getStringPreferences(SharedPrefencesEngine.USER_LOGIN_MAIL).trim());
        }

        checkBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rememberMe = isChecked;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    public int language_index(String langauge){
        int default_index = -1;
        switch (langauge){
            case "en":
                default_index = 0;
                break;
            case "tur":
            case "tr":
                default_index = 1;
                break;
            case "ar":
                default_index = 2;
                break;
            case "fra":
            case "fr":
                default_index = 3;
                break;
        }
        spref().savePreferences(SharedPrefencesEngine.APP_LANGUAGE, langauge);

        return default_index;
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteracting = true;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSigIn:
                loginUser();
                break;
            case R.id.txtNewUser:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.txtForgotPassword:
                showRecoverPasswordDialog();
                break;
        }
    }

    private void loginUser() {
        String userName = tvMail.getText().toString().trim();
        String password = tvPassword.getText().toString().trim();

        if (!Validation.validateEmail(userName)) {
            tvMail.setError(getString(R.string.username_is_required));
            tvMail.requestFocus();
            return;
        } else if (!Validation.validateFields(password)) {
            tvPassword.setError(getString(R.string.password_is_required));
            tvPassword.requestFocus();
            return;
        }

        login(userName, password);
    }

    private void login(String userName, String password) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        progressDialog = AppUtils.dialog(MainActivity.this,dm);
        progressDialog.show();

        auth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgress();
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    if(rememberMe) {
                        spref().savePreferences(SharedPrefencesEngine.USER_LOGIN_PASSWORD, password);
                        spref().savePreferences(SharedPrefencesEngine.USER_LOGIN_MAIL, userName);
                    }
                    // Kullanıcının rolünü kontrol et
                    checkUserRole(userName);
                } else {
                    cookieBar_create(getString(R.string.warning), task.getException().toString());

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
                hideProgress();
            }
        });
    }

    private void checkUserRole(String userName) {
        // Firestore'dan kullanıcının rolünü kontrol et
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Öğretmen koleksiyonunda kontrol et
        CollectionReference teacherCollection = db.collection("FitnessTrackTeacher");
        Query teacherQuery = teacherCollection.whereEqualTo("email", userName);
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
                            Coach coach = new Coach(CoachId,CoachEmail, CoachUsername, CoachPassword,
                                    CoachType,  CoachUserProfile, CoachRegisteredDate);


                        // Kullanıcı öğretmen olarak bulundu
                        Intent intent = new Intent(MainActivity.this,
                                TeacherStudentSelectionActivity.class);
                        intent.putExtra("Coach", coach);
                        startActivity(intent);
                        return; // İşlemi sonlandır
                        }
                    }
                } else {
                    // Hata durumunu yönet
                }

                // Öğrenci koleksiyonunda kontrol et
                CollectionReference studentCollection = db.collection("FitnessTrackStudent");
                Query studentQuery = studentCollection.whereEqualTo("email", userName);
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

                                    Student student = new Student(studentId, studentAddres,studentBirthday,
                                            studentEmail,studentGender, studentHeight,studentPassword,
                                            studentTeacherId,studentType,studentUsername,
                                            studentWeight,studentGroupId, studentProfileImage,
                                            studentRegisteredDate);

                                    Intent intent = new Intent(MainActivity.this,
                                            StudentDashboardActivity.class);
                                    intent.putExtra("Student", student);
                                    startActivity(intent);
                                    return;
                                }
                            } else {
                                // Kullanıcı öğretmen veya öğrenci olarak bulunamadı
                                cookieBar_create(getString(R.string.error), getString(R.string.invalid_user));

                            }
                        } else {
                            // Hata durumunu yönet
                        }
                    }
                });
            }
        });
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //builder.setTitle(R.string.recover_password);
        TextView title_of_dialog = new TextView(getApplicationContext());
        title_of_dialog.setHeight(150);
        title_of_dialog.setBackgroundColor(Color.argb(255,14,134,145));
        title_of_dialog.setText(getString(R.string.recover_password));
        title_of_dialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title_of_dialog.setTextColor(Color.BLACK);
        title_of_dialog.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title_of_dialog);

        LinearLayout linearLayout=new LinearLayout(this);
        EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setHint(getString(R.string.prompt_email));
        emailet.setMinEms(10);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailet.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 0, 5);
        emailet.setLayoutParams(params);

        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,0,5);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id

        builder.setPositiveButton(R.string.recover, (dialog, which) -> {
            String email=tvMail.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                // Firebase'e şifre sıfırlama e-postası gönderme işlemini burada gerçekleştirin
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Şifre sıfırlama e-postası başarıyla gönderildi
                                cookieBar_create(getString(R.string.warning), getString(R.string.a_password_reset_email_has_been_sent));

                            } else {
                                // Şifre sıfırlama e-postası gönderilemedi
                                cookieBar_create(getString(R.string.warning), getString(R.string.the_password_reset_email_could_not_be_sent));
                            }
                        });
            }
            else {
                cookieBar_create(getString(R.string.error), getString(R.string.user_email_is_not_in_proper_format));

            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            String email=tvMail.getText().toString().trim();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void cookieBar_create(String title, String message) {
        // Mevcut CookieBar'ı kontrol edin ve kapatın
        if (currentCookieBar != null) {
            currentCookieBar.dismiss();
        }
        currentCookieBar = CookieBar.build(this)
                .setTitle(title)
                .setMessage(message)
                .setMessageColor(R.color.fancyMessage)
                .setBackgroundColor(R.color.cookiebar)
                .setIcon(R.drawable.ic_launcher_foreground_snackbar)
                .setDuration(5000)
                .setCookiePosition(CookieBar.BOTTOM)
                .setAction(R.string.Close, () -> CookieBar.dismiss(MainActivity.this))
                .show();


    }
    @Override
    public void onNetworkChange(boolean isConnected) {

    }
    public boolean isInternetAvailable() {
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
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

        Snackbar snackbar = Snackbar.make(findViewById(R.id.btnSigIn), message, duration);
        View snackbarView = snackbar.getView();
        TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarText.setTextColor(textColor);
        if (!actionText.equals("")) {
            snackbar.setAction(actionText, actionClickListener);
            snackbar.show();
        }
    }
    private void checkInternetConnection(boolean state) {

        // display snack bar
        showSnackBar(state);
    }

}
