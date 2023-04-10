package com.jomeva.driving.activities.util;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.jomeva.driving.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Common.isConnectToInternet(context)){
            Log.d("atento","->");
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            View layoutDialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog,null);
            builder.setView(layoutDialog);
            AppCompatButton btn= layoutDialog.findViewById(R.id.comprobarInternet);
            AlertDialog alert =builder.create();
            alert.show();
            alert.setCancelable(false);
            alert.getWindow().setGravity(Gravity.CENTER);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        alert.dismiss();
        onReceive(context,intent);
    }
});


        }
    }
}
