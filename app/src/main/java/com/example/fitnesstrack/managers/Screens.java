package com.example.fitnesstrack.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

public class Screens {
      private static Context context;

      public Screens(Context context) {
        Screens.context = context;
    }
  
    public void showClearTopScreen(final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void showCustomScreen(final Class<?> cls) {
        final Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
    public void showToast(String strMsg) {
        Toast.makeText(context, strMsg, Toast.LENGTH_LONG).show();
    }
}
