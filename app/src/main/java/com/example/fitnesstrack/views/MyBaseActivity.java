package com.example.fitnesstrack.views;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.managers.Screens;
import com.example.fitnesstrack.managers.Utils;
import com.example.fitnesstrack.utils.SharedPrefencesEngine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyBaseActivity extends AppCompatActivity {
    public Activity mActivity;
    public Screens screens;
    public Dialog progressDialog;
    private ProgressDialog pd = null;
    public FirebaseAuth auth; //Auth init

    public FirebaseUser firebaseUser; //Current User

    public FirebaseDatabase firebaseDatabase;

    public DatabaseReference databaseReference;


    public SharedPrefencesEngine spref() {
        return SharedPrefencesEngine.sharedInstance(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mActivity = this;
        //screens = new Screens(mActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Opening Screen", this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    public void showProgress() {
        try {
            if (pd == null) {
                pd = new ProgressDialog(mActivity);
            }
            pd.setMessage(getString(R.string.msg_please_wait));
            pd.show();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void hideProgress() {
        try {
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }
}
