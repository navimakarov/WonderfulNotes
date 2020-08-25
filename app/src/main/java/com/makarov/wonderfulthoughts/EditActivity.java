package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText titleEdit, noteEdit;
    private FloatingActionButton writeButton;
    private ImageButton backButton;

    private SQLiteDatabase db;
    private int highlight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleEdit = findViewById(R.id.titleEdit);
        noteEdit =  findViewById(R.id.noteEdit);
        backButton = findViewById(R.id.backButton);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        ImageButton infoButton = findViewById(R.id.infoButton);
        ImageButton copyButton = findViewById(R.id.copyButton);
        writeButton = findViewById(R.id.writeButton);

        Intent intent = getIntent();
        final String id;
        if(intent.hasExtra("id"))
            id = intent.getStringExtra("id");
        else
            id = null;

        db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
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
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null; // TODO check this out
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
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", note);
                assert clipboard != null; // TODO check it out
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
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(EditActivity.this);
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

    }



    public boolean save(String id) {
        Date todayDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // TODO checkout SuppressLint
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
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(String error_message){
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        intent.putExtra("error", error_message);
        startActivity(intent);
        finish();
    }

}