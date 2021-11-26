package com.project.cst2335.Utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.R;

import java.io.InputStream;
import java.io.OutputStream;

public class Utilities {
    public static boolean checkConnectivity(Context ctx) {

        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.isAvailable())
            return true;
        else
            return false;

    }

    public static void showSnackBar(Activity ctx, String message){
        Snackbar.make(ctx.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public static  String getSavedSearchTerm(Context context){
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        //check if file associated with name "value" is there, if not set to empty string
        //getXXX(key, default value)
        return prefs.getString(Constants.KEY_PREFS,Constants.DEFAULT_SEARCH_TERM);
    }

    public static  void setSearchTerm(Context context, String term){
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_PREFS,term);//store key and value
        editor.apply();
    }


    public static  void closeProgressDialog(ProgressDialog dialog){
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static  void closeKeyboard(Context context, View view){
        InputMethodManager inputMethodManager =  (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
    }

    public static  ProgressDialog showProgressDialog(Context context,ProgressDialog dialog){
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.msg_wait) );
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
    public static void showAlertDialog(String title, String message,Context context){
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, (dialog, cl)->{});
        //make the Alert window appear
        builder.create().show();
    }

}