package com.grs21.supervisor.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastMessage {

    public void errorMessage(String message, Context context){
        Toast toastSuccess = Toasty.error(context,message
                , Toast.LENGTH_LONG, true);
        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
        toastSuccess.show();
    }
    public void successMessage(String message, Context context){
        Toast toastSuccess = Toasty.success(context,message
                , Toast.LENGTH_LONG, true);
        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
        toastSuccess.show();

    }
    public void warningMessage(String message, Context context){
        Toast toastSuccess = Toasty.warning(context,message
                , Toast.LENGTH_LONG, true);
        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
        toastSuccess.show();
    }
}
