package com.makarov.wonderfulnotes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class DataBase {
    public static void read_from_db(SQLiteDatabase db, ArrayList<Note> notes) {
        Cursor query = db.rawQuery("SELECT * FROM notes;", null);
        notes.clear();

        if(query.moveToFirst()){
            do{
                int id = query.getInt(0);
                String date = query.getString(1);
                String title = query.getString(2);
                String text = query.getString(3);
                int highlight = query.getInt(4);

                Note note = new Note(date, title, text, id);
                if(highlight == 1){
                    note.highlight();
                }
                notes.add(note);
            }
            while(query.moveToNext());
        }
        Collections.reverse(notes);
        query.close();
    }

    public static void write_to_db(SQLiteDatabase db, ArrayList<Note> notes) {
        db.delete("notes", null, null);
        for(Note note : notes) {
            ContentValues cv = new ContentValues();
            cv.put("date", note.getDate());
            cv.put("title", note.getTitle());
            cv.put("note", note.getText());
            int highlight = 0;
            if(note.highlighted())
                highlight = 1;
            cv.put("highlight", highlight);
            db.insert("notes", null, cv);
        }
    }
}
