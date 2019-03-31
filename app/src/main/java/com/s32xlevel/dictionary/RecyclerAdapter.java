package com.s32xlevel.dictionary;

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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    interface Listener {
        void onClick(int position);
        boolean onLongClick(int position);
    }

    private Listener listener;

    private Context context;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dictionary_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        DBHelper helper = new DBHelper(context);

        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("dictionary",
                    new String[] {"ru_word", "en_word"},
                    "_id = ?",
                    new String[] {String.valueOf(position + 1)},
                    null,
                    null,
                    "ru_word");

            if (cursor.moveToFirst()) {
                viewHolder.ruWord.setText(cursor.getString(0));
                viewHolder.enWord.setText(cursor.getString(1));
            }

            cursor.close();

        } catch (SQLiteException e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position + 1); // +1 for correct work with db
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    return listener.onLongClick(position + 1); // +1 for correct work with db
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        DBHelper helper = new DBHelper(context);
        try {
            SQLiteDatabase db = helper.getReadableDatabase();

            Cursor cursor = db.query("dictionary", new String[] {"_id"},
                    null, null, null, null, "ru_word");

            int countRows = cursor.getCount();

            cursor.close();

            return countRows;
        } catch (SQLiteException e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        }
        return 0;
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
