package com.project.cst2335.Utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * contains the utility functions for helping
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class Utilities {

    /**
     * check if internet is available
     *
     * @param context context to use
     * @return if internet is available or not
     */
    public static boolean checkConnectivity(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE); //https://developer.android.com/reference/android/net/ConnectivityManager#getActiveNetworkInfo()
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.isAvailable())
            return true;
        else
            return false;

    }

    /**
     * get search term from shared prefs to load in search edittext
     *
     * @param context context to use
     * @return search term
     */
    public static  String getSavedSearchTerm(Context context){
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        //check if file associated with name "value" is there, if not set to empty string
        //getXXX(key, default value)
        return prefs.getString(Constants.KEY_PREFS,Constants.DEFAULT_SEARCH_TERM);
    }

    /**
     * save search term to shared prefs to load it again form there
     *
     * @param context context to use
     * @param term search term to save
     */
    public static  void setSearchTerm(Context context, String term){
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_PREFS,term);//store key and value
        editor.apply();
    }

    /**
     * close the dialog if it is not null
     *
     * @param dialog dialog to close
     */
    public static  void closeProgressDialog(ProgressDialog dialog){
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * get image from internal storage
     *
     * @param context context to use
     * @param imageName  image name to append on photo id to know if it is large or small
     * @param name name of image which is the photo id
     * @return bitmap from internal storage
     */

    public static  Bitmap getImageFromStorage(Context context,String imageName,String name)
    {
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir(Constants.PEXELS_IMAGE_FOLDER, Context.MODE_PRIVATE); // directory where images are stored
            File file=new File(directory,name+imageName+".jpg");
            if(file.exists()){ //check if file exists then return the bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;
            }
        }
        catch (FileNotFoundException e) { }
        return null;
    }

    /**
     * load image from internal storage if it is there or download it from url and save it to the internal storage for future use
     *
     * @param photo the photo model
     * @param imageSizeToLoad large or small
     * @param context context to use
     * @return bitmap from downloaded form url or loaded from internals storage
     */
    public static Bitmap loadImage(PhotoModel photo,int imageSizeToLoad,Context context) {
        Bitmap bitmap = null;
        String imageUrl=photo.tinyUrl;
        String imageName = Constants.PEXELS_SMALL_IMAGE;
        if(imageSizeToLoad==Constants.PEXELS_LOAD_LARGE_IMAGE){
            imageUrl=photo.large2xUrl;
            imageName = Constants.PEXELS_LARGE_IMAGE;
        }

        try {
            // try to get the images from internal storage first
            bitmap  = Utilities.getImageFromStorage(context,imageName,String.valueOf(photo.getId()));
            if (bitmap == null) {
                // if bitmap if null, meaning there is no image by that name then we download the image from url
                URL url = new URL(imageUrl);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if(imageSizeToLoad==Constants.PEXELS_LOAD_SMALL_IMAGE){
                    // if tiny image is downloaded for list then we save it to the internal storage to load it again from storage instead of downloading again
                    Utilities.saveImageToInternalStorage(context,bitmap,imageName, String.valueOf(photo.getId()));
                }

            }
            return bitmap;
        }
        catch (Exception e) {

        }
        return null;
    }

    /**
     * delete image from internal storage
     *
     * @param context context to use
     * @param imageName image name to append on photo id to know if it is large or small
     * @return if image was deleted successfully
     */
    public static boolean deleteImageFromStorage(String imageName,Context context) {
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir(Constants.PEXELS_IMAGE_FOLDER, Context.MODE_PRIVATE);
            File file=new File(directory,imageName+".jpg"); // get the file to delete
            if(file.exists()){
                // if it exists then delete it
               return file.delete();
            }
        }
        catch (Exception e) {

        }
        return false;
    }

    /**
     * Save image to internal storage
     *
     * @param context context to use
     * @param bitmapImage bitmap to save
     * @param imageName image name to append on photo id to know if it is large or small
     * @param name name of image which is the photo id
     * @return path to the saved image
     */
    public static String saveImageToInternalStorage(Context context, Bitmap bitmapImage,String imageName, String name ){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(Constants.PEXELS_IMAGE_FOLDER, Context.MODE_PRIVATE); // directory to save into
        File path=new File(directory,name+imageName+".jpg"); // file to save

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {}
        finally {
            try {
                fos.close();
            } catch (IOException e) {}
        }
        return path.getAbsolutePath();
    }

    /**
     * closes the soft keyboard
     *
     * @param context context to use
     * @param view view to use
     */
    public static  void closeKeyboard(Context context, View view){
        InputMethodManager inputMethodManager =  (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
    }

    /**
     *  shows the progress dialog
     *
     * @param context context to use
     * @param dialog dialog to show
     * @return showed progress to close in future
     */
    public static  ProgressDialog showProgressDialog(Context context,ProgressDialog dialog){
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.msg_wait) ); // set the message
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * shows alert diallog
     * @param title title to display on alert dialog
     * @param message  message to display on alert dialog
     * @param context context to use
     */
    public static void showAlertDialog(String title, String message,Context context){
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, (dialog, cl)->{}); //when ok button is clicked

        //make the Alert window appear
        builder.create().show();
    }

}