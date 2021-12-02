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
import com.project.cst2335.Utils.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 *
 * Photo Adapter to populate items in recycler view, it is responsible to downlaading and displaying images from api
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoItemView> {

    private List<PhotoModel> _photos;
    private Context context;
    private boolean _offlineViewing;

    /**
     * Constructor for adapter
     *
     * @param photos list of photos to populate
     * @param context context to use
     * @param offlineViewing if the use is viewing offline photos
     */
    public PhotosAdapter(List<PhotoModel> photos, Context context, boolean offlineViewing) {
        this.context = context;
        this._photos = photos;
        this._offlineViewing = offlineViewing;
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

    /**
     * loads image from internal storage or from api in the background
     *
     * @param photo photo to load
     * @param holder holder to use, which has the imageview
     *
     */
    private void loadImageInBackground(PhotoModel photo,PhotoItemView holder){
        holder.progressbar.setVisibility(View.VISIBLE);
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> {
            Bitmap bitmap = Utilities.loadImage(photo,Constants.PEXELS_LOAD_SMALL_IMAGE,context);
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // hide the progress bar first
                    holder.progressbar.setVisibility(View.GONE);
                    if(bitmap!=null){ //make sure the image is not null before setting
                        holder.img.setImageBitmap(bitmap);
                    }
                    else{
                        Snackbar.make(holder.img,context.getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        });
    }

    /**
     *  viewholder to use in adapter
     */
    protected class PhotoItemView extends  RecyclerView.ViewHolder{

        ImageView img;
        ProgressBar progressbar;

        public PhotoItemView(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            progressbar = itemView.findViewById(R.id.progressBar);

            //when a photo is clicked
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PexelsActivity parentActivity = (PexelsActivity) context;
                  int position = getAdapterPosition(); // like position of the photo in array
                  // when user clicks on a photos we pass that photo to the detail fragment
                  parentActivity.photoClicked(_photos.get(position),_offlineViewing);
                }
            });
        }
    }

}