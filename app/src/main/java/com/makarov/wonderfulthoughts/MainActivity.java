package com.makarov.wonderfulthoughts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView thoughtsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thoughtsRecView = (RecyclerView) findViewById(R.id.thoughtsRecView);

        ArrayList<Thought> thoughts = new ArrayList<>();
        thoughts.add(new Thought("18/04/2020", "Birthday", "I had a birthday! Woooohooo!!!"));

        ThoughtsRecViewAdapter adapter = new ThoughtsRecViewAdapter();
        adapter.setThoughts(thoughts);

        thoughtsRecView.setAdapter(adapter);
        thoughtsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}
