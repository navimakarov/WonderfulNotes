package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

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
                startActivity(intent);
            }
        });
        thoughtsRecView = (RecyclerView) findViewById(R.id.thoughtsRecView);

        ArrayList<Thought> thoughts = new ArrayList<>();
        thoughts.add(new Thought("18/04/2020", "Birthday", "I had a birthday! Woooohoooooooo!!!"));

        ThoughtsRecViewAdapter adapter = new ThoughtsRecViewAdapter();
        adapter.setThoughts(thoughts);

        thoughtsRecView.setAdapter(adapter);
        thoughtsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}


