package com.s32xlevel.dictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class EditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD_ID = "wordId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // correct toolbar title
        toolbar.setTitle(getIntent().getIntExtra(EditActivity.EXTRA_WORD_ID, -1) != -1 ?
                getString(R.string.edit_word_title) : getString(R.string.add_word_title));
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
}
