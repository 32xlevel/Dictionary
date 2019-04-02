package com.s32xlevel.dictionary.repository;

import com.s32xlevel.dictionary.model.Word;

import java.util.List;

public interface WordRepository {
    List<Word> getAll();

    Word save(Word word);

    void delete(int id);

    Word findById(int id);

    Word findByRuAndEnWords(String ruWord, String enWord);

    int countWords();
}
