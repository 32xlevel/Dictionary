package com.s32xlevel.dictionary.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.s32xlevel.dictionary.R;
import com.s32xlevel.dictionary.model.Word;
import com.s32xlevel.dictionary.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

import static com.s32xlevel.dictionary.util.ValidationUtil.isBlank;

public class WordRepositoryImpl implements WordRepository {

    private Context context;
    private SQLiteDatabase readeableDb;
    private SQLiteDatabase writableDb;

    public WordRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<Word> getAll() {
        try {
            readeableDb = new DBHelper(context).getReadableDatabase();
            Cursor cursor = readeableDb.query(DBHelper.TABLE_NAME,
                    new String[]{"_id", "ru_word", "en_word"},
                    null, null, null, null, "ru_word");

            List<Word> words = new ArrayList<>();
            while (cursor.moveToNext()) {
                words.add(new Word(cursor.getInt(0),
                        cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();
            return words;
        } catch (Exception e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    @Override
    public Word save(Word word) {
        try {
            writableDb = new DBHelper(context).getWritableDatabase();

            if (isBlank(word.getRuWord()) || isBlank(word.getEnWord())) {
                throw new IllegalArgumentException();
            }

            ContentValues values = new ContentValues();
            values.put("ru_word", word.getRuWord());
            values.put("en_word", word.getEnWord());

            if (word.getId() == null) {
                word.setId((int) writableDb.insert(DBHelper.TABLE_NAME, null, values));
            } else {
                writableDb.update(DBHelper.TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(word.getId())});
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, R.string.validation_null, Toast.LENGTH_LONG).show();
        }

        return word;
    }

    @Override
    public boolean delete(int id) {
        try {
            writableDb = new DBHelper(context).getWritableDatabase();
            return writableDb.delete(DBHelper.TABLE_NAME, "_id = ?", new String[]{String.valueOf(id)}) != 0;
        } catch (Exception e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public Word findById(int id) {
        try {
            readeableDb = new DBHelper(context).getReadableDatabase();
            Cursor cursor = readeableDb.query(DBHelper.TABLE_NAME,
                    new String[]{"_id", "ru_word", "en_word"},
                    "_id = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            Word word = null;
            if (cursor.moveToFirst()) {
                word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            }
            cursor.close();
            return word;
        } catch (Exception e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    public Word findByRuAndEnWords(String ruWord, String enWord) {
        try {
            readeableDb = new DBHelper(context).getReadableDatabase();
            Cursor cursor = readeableDb.query(DBHelper.TABLE_NAME,
                    new String[]{"_id", "ru_word", "en_word"},
                    "ru_word = ? AND en_word = ?",
                    new String[]{ruWord, enWord},
                    null, null, null);
            if (cursor.moveToFirst()) {
                return new Word(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(context, "database unavailable", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    public int countWords() {
        readeableDb = new DBHelper(context).getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(readeableDb, DBHelper.TABLE_NAME);
    }
}
