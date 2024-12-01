package com.isuru.hettiarachchi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isuru.hettiarachchi.R;
import com.isuru.hettiarachchi.WordMeaningActivity;
import com.isuru.hettiarachchi.models.Word;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordviewHolder> {

    private final Context context;
    private List<Word> wordList;

    public WordAdapter(Context context,List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }


    @NonNull
    @Override
    public WordAdapter.WordviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word,parent,false);
            return new WordviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.WordviewHolder holder, int position) {
        Word word = wordList.get(position);
        if (word != null) {  // Check if the word is not null
            holder.wordTextView.setText(word.getTerm());

            // Set up the click listener
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, WordMeaningActivity.class);
                intent.putExtra("word", word.getTerm());
                intent.putExtra("meaning", word.getMeaning());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateWordList(List<Word> newWordList) {
        this.wordList = newWordList;
        notifyDataSetChanged();  // Notify the adapter that the data set has been updated
    }


    public static class WordviewHolder extends RecyclerView.ViewHolder {
        public TextView wordTextView;


        public WordviewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordText);

        }
    }
}
