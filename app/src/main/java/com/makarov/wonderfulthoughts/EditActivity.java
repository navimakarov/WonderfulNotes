package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {

    private EditText titleEdit, thoughtEdit;

    private FloatingActionButton createButton;

    private ImageButton backButton, deleteButton, infoButton, copyButton, settingsButton;

    private final String TAG = "ERROR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleEdit = (EditText) findViewById(R.id.titleEdit);
        thoughtEdit = (EditText) findViewById(R.id.thoughtEdit);

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
            }
        });
        thoughtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                backButton.setBackgroundResource(R.drawable.tick_icon);
                if(hasFocus){
                    createButton.setVisibility(View.INVISIBLE);
                }
                else{
                    createButton.setVisibility(View.VISIBLE);
                }
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



    }
}
