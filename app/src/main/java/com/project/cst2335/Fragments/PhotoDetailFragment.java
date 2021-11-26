package com.project.cst2335.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Utilities;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PhotoDetailFragment extends Fragment {


    PhotoModel _photo;
    int _position;
    ProgressDialog dialog;

    public PhotoDetailFragment(PhotoModel photo, int position){
        _photo=photo;
        _position=position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView img = view.findViewById(R.id.imageView4);
        Button btnClose = view.findViewById(R.id.btnClose);
        Button btnDelete = view.findViewById(R.id.btnDelete);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();

        //        getParentFragmentManager().beginTransaction().remove(MessageDetailFragment.this).commit();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialog();
            }
        });

        dialog= Utilities.showProgressDialog(getContext(),dialog);
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> {
            Bitmap bitmap = loadImage(_photo.getLarge2xUrl());
            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utilities.closeProgressDialog(dialog);
                    if(bitmap!=null){
                        img.setImageBitmap(bitmap);
                    }
                    else{
                        Snackbar.make(img, getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        });



        return view;
    }



    private void showAlertDialog(){
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.msg_confirm_delete);
        builder.setTitle(R.string.msg_title_confirm_delete);
        builder.setNegativeButton(R.string.no, (dialog, cl)->{
            //if  user clicks no, do nothing

        });

        builder.setPositiveButton(R.string.yes, (dialog, cl)->{


        });

        //make the Alert window appear
        builder.create().show();

    }



    public static Bitmap loadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        }
        catch (Exception e) { }
        return null;
    }
}
