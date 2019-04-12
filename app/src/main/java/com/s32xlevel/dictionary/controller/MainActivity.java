package com.s32xlevel.dictionary.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.s32xlevel.dictionary.R;
import com.s32xlevel.dictionary.model.Word;
import com.s32xlevel.dictionary.repository.WordRepository;
import com.s32xlevel.dictionary.repository.WordRepositoryImpl;
import com.s32xlevel.dictionary.util.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    private WordRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new WordRepositoryImpl(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.dictionary_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final RecyclerAdapter adapter = new RecyclerAdapter(this);
        adapter.setListener(new RecyclerAdapter.Listener() {
            @Override
            public void onClick(String ruWord, String enWord) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.EXTRA_RU_WORD, ruWord);
                intent.putExtra(EditActivity.EXTRA_EN_WORD, enWord);
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(final String ruWord, final String enWord) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Word word = repository.findByRuAndEnWords(ruWord, enWord);
                                repository.delete(word.getId());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_word: {
                Intent intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
