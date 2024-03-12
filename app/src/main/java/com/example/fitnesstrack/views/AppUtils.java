package com.example.fitnesstrack.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;

import com.example.fitnesstrack.R;

public class AppUtils {
    public static Dialog dialog(Context context, DisplayMetrics dm){
        Dialog dialog1;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.loading_dialog);

        dialog1.getWindow().setLayout((int) (width*.4) ,(int)(height*.4));
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog1.setCancelable(false);
        return  dialog1;
    }
}
