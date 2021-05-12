package com.makarov.wonderfulnotes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private NotesRecViewAdapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();

    private static final int STORAGE_PERMISSION_CODE = 0;

    private SQLiteDatabase db;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView notesRecView = root.findViewById(R.id.notesRecView);
        ExtendedFloatingActionButton newNoteButton = root.findViewById(R.id.newNoteButton);

        db = getActivity().getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        read_from_db();

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EditFragment()).commit();
            }
        });

        adapter = new NotesRecViewAdapter();
        adapter.setNotes(notes);

        notesRecView.setAdapter(adapter);
        notesRecView.setLayoutManager(new LinearLayoutManager(getContext()));

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


        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey("error")) {
            String error = getArguments().getString("error", null);
            Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), error, Snackbar.LENGTH_LONG);
            errorSnackBar.setTextColor(Color.RED);
            errorSnackBar.show();
        }

        return root;
    }

    private void read_from_db() {
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
