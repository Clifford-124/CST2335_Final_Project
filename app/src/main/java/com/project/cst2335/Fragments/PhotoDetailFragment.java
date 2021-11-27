package com.project.cst2335.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;
import com.project.cst2335.Utils.Utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 *
 * fragment displying the detail or larger view of photo with options to save and delete
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class PhotoDetailFragment extends Fragment {


    PhotoModel _photo;
    ProgressDialog dialog;
    DatabaseHelper db;
    Bitmap bitmap;
    boolean _offlineViewing;
    ImageView imageView;
    boolean success;

    /**
     * Constructor for the fragment
     *
     * @param photo photo to dosplay
     * @param offlineViewing if user if viewing the photo offline
     */
    public PhotoDetailFragment(PhotoModel photo, boolean offlineViewing){
        _photo=photo;
        _offlineViewing = offlineViewing;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        imageView = view.findViewById(R.id.imgDetail);
        Button btnClose = view.findViewById(R.id.btnClose);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnDelete.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);

        if(_offlineViewing){
            // if user if viewing saved photos the hide the save button and display the delete button
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        }

        db= new DatabaseHelper(getContext());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
           }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBackgroundProcessing(Constants.TASK_SAVE_IMAGE);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        // load the photo in background
        doBackgroundProcessing(Constants.TASK_LOAD);

        return view;
    }


    /**
     * does the background processing of the task passed
     *
     * @param taskId the task id of the task to perform in background
     */
    private void doBackgroundProcessing(int taskId){

        dialog= Utilities.showProgressDialog(getContext(),dialog);
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> {


            if(taskId==Constants.TASK_SAVE_IMAGE){
                success = saveImage(); // save the image
            }
            else if(taskId==Constants.TASK_LOAD){
                bitmap = Utilities.loadImage(_photo,Constants.PEXELS_LOAD_LARGE_IMAGE,getContext()); // just load the image
            }
            else if(taskId==Constants.TASK_DELETE_IMAGE){
                success = deleteImage(); // delete the image
            }


            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utilities.closeProgressDialog(dialog);
                    // display the results accordingly
                    if(taskId==Constants.TASK_SAVE_IMAGE){
                        if(!success){
                            Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            Snackbar.make(getView(), getResources().getString(R.string.task_completed), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else if(taskId==Constants.TASK_LOAD){
                        if(bitmap!=null){
                            imageView.setImageBitmap(bitmap);
                        }
                        else{
                            Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else if(taskId==Constants.TASK_DELETE_IMAGE){
                        if(!success){
                            Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            Snackbar.make(getView(), getResources().getString(R.string.task_completed), Snackbar.LENGTH_LONG).show();
                        }
                    }

                }
            });
        });
    }

    /**
     * Save image in internal storage and database
     *
     * @return if the image was successfully saved
     */
    private boolean saveImage(){
        if(bitmap!=null){
            // save image to internal storage and get the path
            String path= Utilities.saveImageToInternalStorage(getContext(),bitmap,Constants.PEXELS_LARGE_IMAGE,String.valueOf(_photo.getId()));
            if(path!=null && path!=""){
                // insert photo in the database
                db.insertPhoto(_photo);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete image from internal storage and database
     *
     * @return if the image was successfully deleted
     */
    private boolean deleteImage(){
        // delete the large image from storage
        boolean largeDeleted= Utilities.deleteImageFromStorage(String.valueOf(_photo.getId())+Constants.PEXELS_LARGE_IMAGE,getContext());
        // delete the small image form storage
        boolean smallDeleted= Utilities.deleteImageFromStorage(String.valueOf(_photo.getId())+Constants.PEXELS_SMALL_IMAGE,getContext());
        if(largeDeleted && smallDeleted){
            // if files are successfully deleted then delete them from the database
           return db.deletePhotoByID(_photo.getId());
        }
        return  false;
    }

    /**
     * Alert dialog to ask for delete confirmation, deletes the photo only when user click yes
     *
     */
    private void showAlertDialog(){
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.msg_confirm_delete);
        builder.setTitle(R.string.msg_title_confirm_delete);
        builder.setNegativeButton(R.string.no, (dialog, cl)->{
            //if  user clicks no, do nothing

        });

        builder.setPositiveButton(R.string.yes, (dialog, cl)->{
            // of user confirms then delete the image
            doBackgroundProcessing(Constants.TASK_DELETE_IMAGE);
        });

        //make the Alert window appear
        builder.create().show();

    }
}
