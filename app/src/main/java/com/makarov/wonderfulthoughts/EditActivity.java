package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {

    private EditText titleEdit, thoughtEdit;
    private FloatingActionButton createButton;

    private final String TAG = "ERROR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleEdit = (EditText) findViewById(R.id.titleEdit);
        thoughtEdit = (EditText) findViewById(R.id.thoughtEdit);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        if (thoughtEdit.hasFocus()) {
                            createButton.setVisibility(View.INVISIBLE);
                        } else {
                            createButton.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception e){
                        Log.d(TAG, e.toString());
                    }
                }
            }
        });
        thread.start();


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
