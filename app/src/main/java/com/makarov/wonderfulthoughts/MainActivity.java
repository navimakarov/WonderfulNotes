package com.makarov.wonderfulthoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private NotesRecViewAdapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();

    private SQLiteDatabase db;
    // TODO icon when no notes found

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView notesRecView = findViewById(R.id.notesRecView);
        ExtendedFloatingActionButton newNoteButton = findViewById(R.id.newNoteButton);

        db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        read_from_db();

        Intent intent = getIntent();
        if(intent.hasExtra("error")) {
            String error = intent.getStringExtra("error");
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
        
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();
            }
        });

        adapter = new NotesRecViewAdapter();
        adapter.setNotes(notes);

        notesRecView.setAdapter(adapter);
        notesRecView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                db.delete("notes", "id=" + viewHolder.itemView.getTag(), null);
                read_from_db();
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }

        }).attachToRecyclerView(notesRecView);

    }

    public void read_from_db() {
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
}