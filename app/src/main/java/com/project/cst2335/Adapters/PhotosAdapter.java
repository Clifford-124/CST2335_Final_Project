package com.project.cst2335.Adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Activities.PexelsActivity;
import com.project.cst2335.R;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.Utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoItemView> {

    private List<PhotoModel> _photos;
    private Context context;

    public PhotosAdapter(List<PhotoModel> photos, Context context) {
        this.context = context;
        this._photos = photos;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.photo_item;
    }


    @NonNull
    @Override
    public PhotoItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new PhotoItemView(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoItemView holder, int position) {
        PhotoModel photo = _photos.get(position);

        loadImageInBackground(photo,holder);
    }

    @Override
    public int getItemCount() {
        return _photos.size();
    }

    private void loadImageInBackground(PhotoModel photo,PhotoItemView holder){
        holder.progressbar.setVisibility(View.VISIBLE);
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> {
            Bitmap bitmap = loadImage(photo.tinyUrl,String.valueOf(photo.getId()));
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.progressbar.setVisibility(View.GONE);
                    if(bitmap!=null){
                        holder.img.setImageBitmap(bitmap);
                    }
                    else{
                        Snackbar.make(holder.img,context.getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        });
    }

    protected class PhotoItemView extends  RecyclerView.ViewHolder{

        ImageView img;
        ProgressBar progressbar;

        public PhotoItemView(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            progressbar = itemView.findViewById(R.id.progressBar);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PexelsActivity parentActivity = (PexelsActivity) context;
                  int position = getAdapterPosition();
                  parentActivity.userClickMessage(_photos.get(position),position);
                }
            });

        }
    }

    public Bitmap loadImage(String imageUrl, String name) {
        Bitmap bitmap = null;
        try {
            bitmap  = getImageFromStorage(name);
            if (bitmap == null) {
                URL url = new URL(imageUrl);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                saveImageToInternalStorage(bitmap, name);
            }
            return bitmap;

        }
        catch (Exception e) { }
        return null;
    }

    private Bitmap getImageFromStorage(String name)
    {
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir(Constants.PEXELS_IMAGE_FOLDER, Context.MODE_PRIVATE);
            File file=new File(directory,name+".jpg");
            if(file.exists()){
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;
            }


        }
        catch (FileNotFoundException e) { }
        return null;
    }

    public String saveImageToInternalStorage(Bitmap bitmapImage,String name ){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(Constants.PEXELS_IMAGE_FOLDER, Context.MODE_PRIVATE);
        File path=new File(directory,name+".jpg");

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
        return directory.getAbsolutePath();
    }
}