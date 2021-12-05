package com.project.cst2335.Models;

import java.io.Serializable;

public class OwlWord implements Serializable {

    private String type;
    private String definition;
    private String example;
    private String imageURL;
    private String word;
    private String pronunciation;
    private int id;

    public OwlWord(String type, String definition, String example, String imageURL, String word,String pronunciation) {
        this.type = type;
        this.definition = definition;
        this.example = example;
        this.imageURL = imageURL;
        this.word = word;
        this.pronunciation = pronunciation;
    }

    public OwlWord(int id,String type, String definition, String example, String imageURL, String word,String pronunciation) {
        this.id = id;
        this.type = type;
        this.definition = definition;
        this.example = example;
        this.imageURL = imageURL;
        this.word = word;
        this.pronunciation = pronunciation;
    }
    public String getPronunciation() {
        if (pronunciation == null || pronunciation.contentEquals(""))
            return "-";
        else
            return pronunciation;
    }

    public String getType() {
        if (type == null || type.contentEquals(""))
            return "-";
        else
            return type;
    }

    public String getWord() {
        if (word == null || word.contentEquals(""))
            return "-";
        else
            return word;
    }

    public String getDefinition() {
        if (definition == null || definition.contentEquals(""))
            return "-";
        else
            return definition;
    }

    public String getExample() {
        if (example == null || example.contentEquals(""))
            return "-";
        else
            return example;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getId() {
        return id;
    }
}