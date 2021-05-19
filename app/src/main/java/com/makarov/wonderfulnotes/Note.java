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

    Note(String note) {
        // {date=yyyy-mm-dd, highlighted=boolean, id=int, text=String, title=String}
        note = note.replace("{", "");
        note = note.replace("}", "");

        String[] tokens = note.split(",");
        for(String token : tokens) {
            String[] tokenSplitted = token.split("=");
            if(token.contains("date")) {
                this.date = tokenSplitted[1];
            }
            else if(token.contains("highlighted")) {
                this.highlighted = tokenSplitted[1].equals("true");
            }
            else if(token.contains("id")) {
                this.id = Integer.parseInt(tokenSplitted[1]);
            }
            else if(token.contains("text")) {
                if(tokenSplitted.length == 1)
                    this.text = "";
                else
                    this.text = token.split("=")[1];
            }
            else if(token.contains("title")) {
                if(tokenSplitted.length == 1)
                    this.title = "Untitled note";
                else
                    this.title = token.split("=")[1];
            }
        }
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
