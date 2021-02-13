package com.grs21.supervisor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.grs21.supervisor.databinding.ActivityUserBinding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        if (!isConnected(this)){
                customDialog();
        }
    }

    private void customDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getApplicationContext());
        alertDialog.setMessage("Please connect to internet to proceed further").setCancelable(true)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.create().show();
    }

    private boolean isConnected(UserActivity userActivity) {
        ConnectivityManager connectivityManager=(ConnectivityManager) userActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn  =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn!=null&&wifiConn.isConnected())||(mobileConn!=null&&mobileConn.isConnected())){
            return true;
        }else{
            return false;
        }
    }
}