package com.makarov.wonderfulnotes;

class Note {
    public String date, title, text;
    public boolean highlighted = false;
    public int id;

    Note(String date, String title, String text, int id) {
        this.date = date;
        this.title = title;
        this.text = text;
        this.id = id;
    }

    String getDate() {
        return date;
    }

    String getTitle() {
        return title;
    }

    String getText() {
        return text;
    }

    int getId() {
        return id;
    }

    void highlight() {
        highlighted = true;
    }

    boolean highlighted() {
        return highlighted;
    }
}
