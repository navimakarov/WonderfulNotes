package com.makarov.wonderfulthoughts;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ThoughtsRecViewAdapter extends RecyclerView.Adapter<ThoughtsRecViewAdapter.ViewHolder> {

    private ArrayList <Thought> thoughts = new ArrayList<>();
    private SQLiteDatabase db;

    public ThoughtsRecViewAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thoughts_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(thoughts.get(position).getDate());
        holder.name.setText(thoughts.get(position).getName());
        holder.text.setText(thoughts.get(position).getText());

        if(thoughts.get(position).highlighted()){
            holder.highlight.setTag("active");
            holder.highlight.setBackgroundResource(R.drawable.star_active_icon);
        }
        else{
            holder.highlight.setTag("inactive");
            holder.highlight.setBackgroundResource(R.drawable.star_inactive_icon);
        }
        holder.itemView.setTag(thoughts.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return thoughts.size();
    }

    public void setThoughts(ArrayList<Thought> thoughts) {
        this.thoughts = thoughts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView date, name, text;
        private ImageButton highlight;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            text = itemView.findViewById(R.id.text);
            highlight = itemView.findViewById(R.id.highlight);

            highlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = highlight.getTag().toString();
                    String id = itemView.getTag().toString();

                    db = v.getContext().openOrCreateDatabase("notes.db", v.getContext().MODE_PRIVATE, null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");

                    if(tag.equals("inactive")){
                        highlight.setTag("active");
                        highlight.setBackgroundResource(R.drawable.star_active_icon);
                        db.execSQL("UPDATE notes SET highlight = 1 WHERE id=" + id);
                    }
                    else{
                        highlight.setTag("inactive");
                        highlight.setBackgroundResource(R.drawable.star_inactive_icon);
                        db.execSQL("UPDATE notes SET highlight = 0 WHERE id=" + id);

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("id", itemView.getTag().toString());
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}