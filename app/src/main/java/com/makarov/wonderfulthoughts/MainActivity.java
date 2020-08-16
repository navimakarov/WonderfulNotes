package com.makarov.wonderfulthoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView thoughtsRecView;

    private ExtendedFloatingActionButton newThoughtBtn;

    private static final String TAG = "Program Logs";
    private SQLiteDatabase db;

    private ThoughtsRecViewAdapter adapter;
    private ArrayList<Thought> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        //db.execSQL("DROP TABLE notes;");

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

        read_from_db(notes);

        adapter = new ThoughtsRecViewAdapter();
        adapter.setThoughts(notes);

        thoughtsRecView.setAdapter(adapter);
        thoughtsRecView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, String.valueOf(viewHolder.itemView.getTag()));
                db.delete("notes", "id=" + viewHolder.itemView.getTag(), null);
                read_from_db(notes);
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }

        }).attachToRecyclerView(thoughtsRecView);
    }

    public void read_from_db(ArrayList<Thought> notes) {
        Cursor query = db.rawQuery("SELECT * FROM notes;", null);

        notes.clear();
        if(query.moveToFirst()){
            do{
                int id = query.getInt(0);
                String date = query.getString(1);
                String title = query.getString(2);
                String note = query.getString(3);
                int highlight = query.getInt(4);

                Thought thought = new Thought(date, title, note, id);
                if(highlight == 1){
                    thought.highlight();
                }
                notes.add(thought);
            }
            while(query.moveToNext());
        }
        Collections.reverse(notes);
        query.close();
    }

}