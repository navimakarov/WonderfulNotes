package com.makarov.wonderfulnotes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private NotesRecViewAdapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();

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
        DataBase.read_from_db(db, notes);

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new EditFragment()).addToBackStack("Home").commit();
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
                DataBase.read_from_db(db, notes);
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red))
                            .addActionIcon(R.drawable.delete_recview)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
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

}
