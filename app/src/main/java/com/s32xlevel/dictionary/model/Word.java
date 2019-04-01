package com.s32xlevel.dictionary.model;

public class Word {
    private Integer id;

    private String ruWord;

    private String enWord;

    public Word(int id, String ruWord, String enWord) {
        this.id = id;
        this.ruWord = ruWord;
        this.enWord = enWord;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuWord() {
        return ruWord;
    }

    public void setRuWord(String ruWord) {
        this.ruWord = ruWord;
    }

    public String getEnWord() {
        return enWord;
    }

    public void setEnWord(String enWord) {
        this.enWord = enWord;
    }
}
