package com.makarov.wonderfulnotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class EditFragment extends Fragment {
    private EditText titleEdit, noteEdit;
    private FloatingActionButton writeButton;
    private ImageButton backButton;

    private SQLiteDatabase db;
    private int highlight = 0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        titleEdit = root.findViewById(R.id.titleEdit);
        noteEdit =  root.findViewById(R.id.noteEdit);
        backButton = root.findViewById(R.id.backButton);
        ImageButton deleteButton = root.findViewById(R.id.deleteButton);
        ImageButton infoButton = root.findViewById(R.id.infoButton);
        ImageButton copyButton = root.findViewById(R.id.copyButton);
        writeButton = root.findViewById(R.id.writeButton);

        Bundle arguments = getArguments();
        final String id;
        if(arguments != null && arguments.containsKey("id")) {
            id = getArguments().getString("id", null);
        }
        else {
            id = null;
        }
        db = getActivity().getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");

        Cursor query = db.rawQuery("SELECT * FROM notes;", null);
        if(id != null && query.moveToFirst()){
            while(query.getInt(0) != Integer.parseInt(id)){
                query.moveToNext();
            }
            titleEdit.setText(query.getString(2));
            noteEdit.setText(query.getString(3));
            highlight = query.getInt(4);
            query.close();
        }

        if(id == null){
            backButton.setBackgroundResource(R.drawable.tick_icon);
            backButton.setTag("Save");
        }

        noteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    writeButton.setVisibility(View.INVISIBLE);
                }
                else{
                    writeButton.setVisibility(View.VISIBLE);
                }
            }
        });

        noteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                backButton.setBackgroundResource(R.drawable.tick_icon);
                backButton.setTag("Save");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                backButton.setBackgroundResource(R.drawable.tick_icon);
                backButton.setTag("Save");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(noteEdit, InputMethodManager.SHOW_IMPLICIT);
                writeButton.setVisibility(View.INVISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backButton.getTag().toString().equals("Save")) {
                    boolean savedSuccessfully = save(id);
                    if(!savedSuccessfully) {
                        exit("Cannot save an empty note");
                    }
                    else {
                        if (id == null)
                            exit();
                        else {
                            backButton.setBackgroundResource(R.drawable.back_icon);
                            backButton.setTag("Back");
                        }
                    }
                }
                else{
                    exit();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add do you want to delete?
                if (id != null) {
                    db.delete("notes", "id=" + id, null);
                }
                exit();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteEdit.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", note);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(v, "Text copied to clipboard", Snackbar.LENGTH_SHORT).show();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteEdit.getText().toString();
                int lines = note.split("\n").length;
                int charsNumber = note.length();
                int charsNumberWithoutSpaces = note.replace(" ", "").length();
                int words = note.split("\\s+").length;

                String message = "Lines: " + lines + "\n"
                        + "Words: " + words + "\n"
                        + "Characters (With Spaces): " + charsNumber + "\n"
                        + "Characters (Without Spaces): " + charsNumberWithoutSpaces;
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Statistics");
                alertDialog.setMessage(message);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });

        return root;
    }

    public boolean save(String id) {
        Date todayDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(todayDate);
        String title = titleEdit.getText().toString();
        String note = noteEdit.getText().toString();

        if(title.equals("")) {
            if(note.equals("") && id == null)
                return false;
            else
                title = "Untitled note";
        }

        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("title", title);
        cv.put("note", note);
        cv.put("highlight", highlight);


        if(id == null){
            db.insert("notes", null, cv);
        }
        else{
            db.update("notes", cv, "id = ?", new String[] { id });
        }
        return true;

    }

    public void exit() {
        hide_keyboard();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    public void exit(String error_message){
        hide_keyboard();
        Fragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("error", error_message);
        homeFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
    }

    private void hide_keyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
