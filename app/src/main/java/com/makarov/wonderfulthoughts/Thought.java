package com.makarov.wonderfulthoughts;

public class Thought {
    private String date, name, text;

    public Thought(String date, String name, String text) {
        this.date = date;
        this.name = name;
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
