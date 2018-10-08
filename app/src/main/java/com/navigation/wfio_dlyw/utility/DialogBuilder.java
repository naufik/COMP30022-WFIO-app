package com.navigation.wfio_dlyw.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogBuilder {
    private String actionString;
    private int method;

    public static AlertDialog okDialog(String text, Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage(text);
        builder1.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        return builder1.create();
    }

    public static AlertDialog.Builder confirmDialog(String text, Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage(text);

        return builder1;
    }
}
