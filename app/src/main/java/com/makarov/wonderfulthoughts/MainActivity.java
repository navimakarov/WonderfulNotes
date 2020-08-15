package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView thoughtsRecView;

    private ExtendedFloatingActionButton newThoughtBtn;

    private static final String TAG = "Program Logs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newThoughtBtn = findViewById(R.id.newThoughtBtn);
        newThoughtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id", "null");
                startActivity(intent);
            }
        });
        thoughtsRecView = (RecyclerView) findViewById(R.id.thoughtsRecView);

        ArrayList<Thought> notes = new ArrayList<>();
        read_from_db(notes);

        ThoughtsRecViewAdapter adapter = new ThoughtsRecViewAdapter();
        adapter.setThoughts(notes);

        thoughtsRecView.setAdapter(adapter);
        thoughtsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void read_from_db(ArrayList<Thought> notes) {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        Cursor query = db.rawQuery("SELECT * FROM notes;", null);

        notes.clear();
        if(query.moveToFirst()){
            do{
                String date = query.getString(1);
                String title = query.getString(2);
                String note = query.getString(3);
                int highlight = query.getInt(4);

                Thought thought = new Thought(date, title, note);
                if(highlight == 1){
                    thought.highlight();
                }
                notes.add(thought);
            }
            while(query.moveToNext());
        }
        Collections.reverse(notes);
        query.close();
        db.close();
    }
}


