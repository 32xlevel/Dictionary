package com.s32xlevel.dictionary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD_ID = "wordId";

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Settings dependent on EXTRA_WORD_ID
        if (getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1) {
            toolbar.setTitle(getString(R.string.edit_word_title));
            fillEditViews();
        } else {
            toolbar.setTitle(getString(R.string.add_word_title));
        }
        setSupportActionBar(toolbar);
        fillCorrectTextForButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_word: {

                // TODO: Код по удалению слова

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void fillCorrectTextForButton() {
        Button button = findViewById(R.id.add_edit_button);

        button.setText(getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1 ?
                R.string.edit_button_text : R.string.add_button_text);
    }

    private void fillEditViews() throws SQLiteException {
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        DBHelper helper = new DBHelper(this);
        db = helper.getReadableDatabase();

        Cursor cursor = db.query("dictionary",
                new String[] {"ru_word", "en_word"},
                "_id = ?",
                new String[] {String.valueOf(getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1))},
                null,
                null,
                "ru_word");

        if (cursor.moveToFirst()) {
            ruWordEdit.setText(cursor.getString(0));
            enWordEdit.setText(cursor.getString(1));
        }

        cursor.close();
    }

    public void onClickAddEditButton(View view) throws SQLiteException {
        int wordId = getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1);
        db = new DBHelper(this).getReadableDatabase();
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        if (wordId == -1) {
            DBHelper.insertWord(db, ruWordEdit.getText().toString(), enWordEdit.getText().toString());
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ru_word", ruWordEdit.getText().toString());
            contentValues.put("en_word", enWordEdit.getText().toString());
            db.update("dictionary", contentValues, "_id = ?", new String[] {String.valueOf(wordId)});
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
