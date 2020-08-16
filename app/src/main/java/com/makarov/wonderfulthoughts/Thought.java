package com.makarov.wonderfulthoughts;

public class Thought {
    private String date, name, text;
    private boolean highlighted = false;
    private int id;

    public Thought(String date, String name, String text, int id) {
        this.date = date;
        this.name = name;
        this.text = text;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void highlight() {
        highlighted = true;
    }

    public void remove_highlight() {
        highlighted = false;
    }
}
