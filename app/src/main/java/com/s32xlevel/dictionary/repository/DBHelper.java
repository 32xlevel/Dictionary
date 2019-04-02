package com.s32xlevel.dictionary.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DICTIONARY";
    public static final String TABLE_NAME = "dictionary";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE dictionary ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ru_word TEXT NOT NULL, " +
                "en_word TEXT NOT NULL, " +
                "CONSTRAINT unique_word_and_translate UNIQUE (ru_word, en_word));");

        insertWord(db, "Работа", "Work");
        insertWord(db, "Учеба", "Study");
        insertWord(db, "Предложение", "Offer");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void insertWord(SQLiteDatabase db, String ruWord, String enWord) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ru_word", ruWord);
        contentValues.put("en_word", enWord);
        db.insert(TABLE_NAME, null, contentValues);
    }
}
