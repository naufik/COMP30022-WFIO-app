package com.navigation.wfio_dlyw.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/***
 * a class to help simplify the making of an alert dialog
 */
public class DialogBuilder {

    /***
     * Create a informative dialog, that reiterate user action
     * @param text String to be displayed on the alert dialog
     * @param ctx Current activity context
     * @return Alert Dialog to use with .show()
     */
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

    /***
     * Create an alert builder with customizable positive and negative button
     * @param text String to show on the alert dialog
     * @param ctx current activity context
     * @return it only returns a builder that still needs to implement its own positive and negative button, and used with .create() methoid before using .show()
     */
    public static AlertDialog.Builder confirmDialog(String text, Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage(text);

        return builder1;
    }
}
