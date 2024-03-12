package com.example.fitnesstrack.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {

    // initialize listener
    public static ReceiverListener Listener;
    public static final String DEVICE_ADDRESS = "device_address";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "ACTION_BOOT_COMPLETED ", Toast.LENGTH_LONG).show();
            // Telefonun yeniden başlatılmasından sonra servisi başlatmak için burada gerekli işlemleri yapın
            String savedBluetoothAddress = getSavedBluetoothAddress(context);
            if (savedBluetoothAddress != null && !savedBluetoothAddress.isEmpty()) {
                startBluetoothService(context, savedBluetoothAddress);
            }
        }

        // initialize connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // check condition
        if (Listener != null) {

            // when connectivity receiver
            // listener  not null
            // get connection status
            boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

            // call listener method
            Listener.onNetworkChange(isConnected);
        }
    }
    private void startBluetoothService(Context context, String bluetoothAddress) {
        Intent serviceIntent = new Intent(context, BluetoothConnectionService.class);
        serviceIntent.putExtra(DEVICE_ADDRESS, bluetoothAddress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
    private String getSavedBluetoothAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEVICE_ADDRESS, null);
    }
    public interface ReceiverListener {
        // create method
        void onNetworkChange(boolean isConnected);
    }
}