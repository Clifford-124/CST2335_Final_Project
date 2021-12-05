package com.project.cst2335.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cst2335.Models.OwlWord;
import com.project.cst2335.R;

import java.util.ArrayList;

public class OwlModelAdapter extends RecyclerView.Adapter<OwlModelAdapter.OwlWordsView> {

    ArrayList<OwlWord> OwlWords;
    Context context;
    String display;

    ItemClickListener mItemClickListener;

    //Define Interface method here
    public interface ItemClickListener {
        void onItemClick(OwlWord model,String Display);
    }

    public OwlModelAdapter(Context context, ArrayList<OwlWord> OwlWords) {
        this.OwlWords = OwlWords;
        this.context = context;
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @NonNull
    @Override
    public OwlWordsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_word_model, parent, false);
        OwlWordsView holder = new OwlWordsView(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OwlWordsView holder, int position) {
        holder.wordType.setText((OwlWords.get(position)).getType());
        holder.word.setText((OwlWords.get(position)).getWord());
    }

    @Override
    public int getItemCount() {
        return OwlWords.size();
    }

    protected class OwlWordsView extends RecyclerView.ViewHolder {
        TextView word;
        TextView wordType;

        public OwlWordsView(View itemView) {
            super(itemView);
            itemView.setOnClickListener(click -> {

                int position = getAdapterPosition();

                mItemClickListener.onItemClick(OwlWords.get(position),display);

            });

            wordType = itemView.findViewById(R.id.wordType);
            word = itemView.findViewById(R.id.word);
        }
    }
}