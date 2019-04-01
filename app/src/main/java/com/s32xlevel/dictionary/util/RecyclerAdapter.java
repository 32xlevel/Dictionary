package com.s32xlevel.dictionary.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.s32xlevel.dictionary.R;
import com.s32xlevel.dictionary.model.Word;
import com.s32xlevel.dictionary.repository.DBHelper;
import com.s32xlevel.dictionary.repository.WordRepository;
import com.s32xlevel.dictionary.repository.WordRepositoryImpl;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    // was problem: discrepancy ID in list and in DB
    // fix: onClick(word1, word2)
    public interface Listener {
//        void onClick(int position);
        void onClick(String ruWord, String enWord);

        boolean onLongClick(String ruWord, String enWord);
    }

    private Listener listener;

    private Context context;

    private WordRepository repository;

    public RecyclerAdapter(Context context) {
        this.context = context;
        repository = new WordRepositoryImpl(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dictionary_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        List<Word> words = repository.getAll();
        final Word word = words.get(position);

        viewHolder.ruWord.setText(word.getRuWord());
        viewHolder.enWord.setText(word.getEnWord());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(word.getRuWord(), word.getEnWord());
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    return listener.onLongClick(word.getRuWord(), word.getEnWord());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return repository.countWords();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ruWord;
        TextView enWord;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ruWord = itemView.findViewById(R.id.ru_word);
            enWord = itemView.findViewById(R.id.en_word);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
