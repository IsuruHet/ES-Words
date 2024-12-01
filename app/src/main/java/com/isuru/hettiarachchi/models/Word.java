package com.isuru.hettiarachchi.models;

public class Word {
    private final String term;
    private final String meaning;

    public Word(String term,String meaning){
        this.term = term;
        this.meaning = meaning;
    }


    public String getTerm() {
        return term;
    }

    public String getMeaning() {
        return meaning;
    }


}
