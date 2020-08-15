package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText titleEdit, thoughtEdit;

    private FloatingActionButton createButton;

    private ImageButton backButton, deleteButton, infoButton, copyButton, settingsButton;

    private final String TAG = "ERROR";

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");


        db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        //db.execSQL("DROP TABLE notes;");

        titleEdit = (EditText) findViewById(R.id.titleEdit);
        thoughtEdit = (EditText) findViewById(R.id.thoughtEdit);

        if(!id.equals("null")){
            Cursor query = db.rawQuery("SELECT * FROM notes;", null);
            for(int i = 0; i < Integer.parseInt(id); i++){
                query.moveToNext();
            }
            titleEdit.setText(query.getString(2));
            thoughtEdit.setText(query.getString(3));
            query.close();
        }

        backButton = (ImageButton) findViewById(R.id.backButton);
        deleteButton = (ImageButton)  findViewById(R.id.deleteButton);
        infoButton = (ImageButton) findViewById(R.id.infoButton);
        copyButton = (ImageButton) findViewById(R.id.copyButton);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);

        titleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                createButton.setVisibility(View.VISIBLE);
                backButton.setBackgroundResource(R.drawable.tick_icon);
                backButton.setTag("Save");
            }
        });
        thoughtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                backButton.setBackgroundResource(R.drawable.tick_icon);
                backButton.setTag("Save");
                if(hasFocus){
                    createButton.setVisibility(View.INVISIBLE);
                }
                else{
                    createButton.setVisibility(View.VISIBLE);
                }
            }
        });

        thoughtEdit.addTextChangedListener(new TextWatcher() {
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



        createButton = (FloatingActionButton) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thoughtEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(thoughtEdit, InputMethodManager.SHOW_IMPLICIT);
                createButton.setVisibility(View.INVISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backButton.getTag().toString().equals("Save")) {
                    backButton.setBackgroundResource(R.drawable.back_icon);
                    save(id);
                    backButton.setTag("Back");
                }
                else{
                    exit();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals("null")){
                    exit();
                }
                else{
                    Log.d(TAG, id);
                    db.delete("notes", "id=" + id, null);
                    exit();
                }
            }
        });

    }

    public void save(String id) {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(todayDate);
        String title = titleEdit.getText().toString();
        String note = thoughtEdit.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("title", title);
        cv.put("note", note);
        cv.put("highlight", 0);


        if(id.equals("null")){
            db.insert("notes", null, cv);
        }
        else{
            db.update("notes", cv, "id = ?", new String[] { id });
        }

    }

    public void exit() {
        db.close();
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
