package com.s32xlevel.dictionary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD_ID = "wordId";
    private SQLiteDatabase db;
//    private static final String YANDEX_URL = "https://translate.yandex.net/api/v1.5/tr/translate?key=trnsl.1.1.20190323T155359Z.d61b1e6ec9c257cd.2e2e095ce68ff4fa10bafe21579e4fb1af687999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Settings dependent on EXTRA_WORD_ID //
        if (getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1) {
            toolbar.setTitle(getString(R.string.edit_word_title));
            fillEditViews();
        } else {
            toolbar.setTitle(getString(R.string.add_word_title));
        }
        setSupportActionBar(toolbar);
        fillCorrectTextForButton();
        //                                    //
//        onKeyPressTextToTranslate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) throws SQLiteException {
        switch (item.getItemId()) {
            case R.id.action_delete_word: {
                db = new DBHelper(this).getWritableDatabase();
                db.delete("dictionary",
                        "_id = ?",
                        new String[]{String.valueOf(getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1))});

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
                new String[]{"ru_word", "en_word"},
                "_id = ?",
                new String[]{String.valueOf(getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1))},
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
        db = new DBHelper(this).getWritableDatabase();
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        if (wordId == -1) {
            DBHelper.insertWord(db, ruWordEdit.getText().toString(), enWordEdit.getText().toString());
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ru_word", ruWordEdit.getText().toString());
            contentValues.put("en_word", enWordEdit.getText().toString());
            db.update("dictionary", contentValues, "_id = ?", new String[]{String.valueOf(wordId)});
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

/*    private void onKeyPressTextToTranslate() {
        EditText ruWord = findViewById(R.id.ru_word_edit);
        ruWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    fillListHints(s.toString());
                } catch (Exception e) {
                    Toast.makeText(EditActivity.this, "Problem with translate API", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void fillListHints(String text) throws Exception {
        ListView listHints = findViewById(R.id.list_hints);
        final EditText enWords = findViewById(R.id.en_word_edit);
        final String[] hints = new AsyncRequest().execute(text, "ru-en").get();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hints);
        listHints.setAdapter(adapter);

        listHints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enWords.setText(hints[position]);
            }
        });
    }

    class AsyncRequest extends AsyncTask<String, Void, String[]> {
        // param[0] - text to translate, params[1] - languages (for example ru-en)
        @Override
        protected String[] doInBackground(String... params) {
            try {
                URL url = new URL(EditActivity.YANDEX_URL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes("text=" + URLEncoder.encode(params[0], "UTF-8") + "&lang=" + params[1]);

                InputStream response = connection.getInputStream();
                String json = new java.util.Scanner(response).nextLine();

                YandexTranslateResponse ytr = new Gson().fromJson(json, YandexTranslateResponse.class);

                dataOutputStream.close();
                response.close();

                return ytr.getText();
            } catch (IOException e) {
                Toast.makeText(EditActivity.this, "Problem with translate API", Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }

    class YandexTranslateResponse {
        private int code;
        private String lang;
        private String[] text;

        public int getCode() {
            return code;
        }

        public String getLang() {
            return lang;
        }

        public String[] getText() {
            return text;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public void setText(String[] text) {
            this.text = text;
        }
    }*/
}
