package com.s32xlevel.dictionary.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

import com.s32xlevel.dictionary.R;
import com.s32xlevel.dictionary.model.Word;
import com.s32xlevel.dictionary.repository.WordRepository;
import com.s32xlevel.dictionary.repository.WordRepositoryImpl;
import com.s32xlevel.dictionary.util.ValidationUtil;
import com.s32xlevel.dictionary.util.YandexTranslateAPI;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_RU_WORD = "ruWord";
    public static final String EXTRA_EN_WORD = "enWord";
    private WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        repository = new WordRepositoryImpl(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Settings dependent on intent came //
        if (getWordFromIntent() != null) {
            toolbar.setTitle(getString(R.string.edit_word_title));
            fillEditTexts();
        } else {
            toolbar.setTitle(getString(R.string.add_word_title));
        }
        fillCorrectTextForButton();
        //                                    //

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onKeyPressTextToTranslate();
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

    private void onKeyPressTextToTranslate() {
        EditText ruWord = findViewById(R.id.ru_word_edit);
        ruWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    fillListHints(s.toString());
                } catch (Exception e) {
                    Toast.makeText(EditActivity.this, R.string.api_problem, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValidationUtil.isBlank(s.toString())) {
                    Toast.makeText(EditActivity.this, R.string.validation_null, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillListHints(String text) throws ExecutionException, InterruptedException {
        ListView listHints = findViewById(R.id.list_hints);
        final EditText enWord = findViewById(R.id.en_word_edit);
        final String[] hints = new AsyncRequest().execute(text).get();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hints);
        listHints.setAdapter(adapter);

        listHints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enWord.setText(hints[position]);
            }
        });
    }


    private class AsyncRequest extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            if (ValidationUtil.isBlank(params[0])) {
                return new String[]{" "};
            }
            try {
                return YandexTranslateAPI.translateText(params[0]);
            } catch (IOException e) {
                return new String[]{" "};
            }
        }
    }

    private void fillCorrectTextForButton() {
        Button button = findViewById(R.id.add_edit_button);

        button.setText(getWordFromIntent() != null ? R.string.edit_button_text : R.string.add_button_text);
    }

    private void fillEditTexts() {
        EditText ruWordEdit = findViewById(R.id.ru_word_edit);
        EditText enWordEdit = findViewById(R.id.en_word_edit);

        Word word = getWordFromIntent();
        ruWordEdit.setText(word.getRuWord());
        enWordEdit.setText(word.getEnWord());
    }

    private Word getWordFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return repository.findByRuAndEnWords(extras.getString(EXTRA_RU_WORD), extras.getString(EXTRA_EN_WORD));
        }
        return null;
    }
}
