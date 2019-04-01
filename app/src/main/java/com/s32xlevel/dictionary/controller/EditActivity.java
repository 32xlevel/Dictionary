package com.s32xlevel.dictionary.controller;

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

import com.s32xlevel.dictionary.R;
import com.s32xlevel.dictionary.model.Word;
import com.s32xlevel.dictionary.repository.DBHelper;
import com.s32xlevel.dictionary.repository.WordRepository;
import com.s32xlevel.dictionary.repository.WordRepositoryImpl;

public class EditActivity extends AppCompatActivity {

//    public static final String EXTRA_WORD_ID = "wordId";
    public static final String EXTRA_RU_WORD = "ruWord";
    public static final String EXTRA_EN_WORD = "enWord";
    private WordRepository repository;
//    private static final String YANDEX_URL = "https://translate.yandex.net/api/v1.5/tr/translate?key=trnsl.1.1.20190323T155359Z.d61b1e6ec9c257cd.2e2e095ce68ff4fa10bafe21579e4fb1af687999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        repository = new WordRepositoryImpl(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Settings dependent on intent came //
        if (getWordFromIntent() != null) {
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
        if (getWordFromIntent() != null) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_word: {
                repository.delete(getWordFromIntent().getId());
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void onClickAddEditButton(View view) {
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        if (getWordFromIntent() == null) {
            repository.save(new Word(null, ruWordEdit.getText().toString(), enWordEdit.getText().toString()));
        } else {
            repository.save(new Word(getWordFromIntent().getId(), ruWordEdit.getText().toString(), enWordEdit.getText().toString()));
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

    private void fillCorrectTextForButton() {
        Button button = findViewById(R.id.add_edit_button);

        button.setText(getWordFromIntent() != null ? R.string.edit_button_text : R.string.add_button_text);
    }

    private void fillEditViews() throws SQLiteException {
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        Word word = getWordFromIntent();
        ruWordEdit.setText(word.getRuWord());
        enWordEdit.setText(word.getEnWord());
    }

    private Word getWordFromIntent() {
        if (getIntent().getExtras() != null) {
            return repository.findByRuAndEnWords(getIntent().getExtras().getString(EXTRA_RU_WORD), getIntent().getExtras().getString(EXTRA_EN_WORD));
        }
        return null;
    }
}
