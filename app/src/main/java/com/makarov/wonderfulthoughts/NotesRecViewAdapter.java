package com.makarov.wonderfulthoughts;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NotesRecViewAdapter extends RecyclerView.Adapter<NotesRecViewAdapter.ViewHolder> {

    private ArrayList <Note> notes = new ArrayList<>();
    private SQLiteDatabase db;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(notes.get(position).getDate());
        holder.title.setText(notes.get(position).getTitle());
        holder.text.setText(notes.get(position).getText());

        if(notes.get(position).highlighted()){
            holder.highlight.setTag("active");
            holder.highlight.setBackgroundResource(R.drawable.star_active_icon);
        }
        else{
            holder.highlight.setTag("inactive");
            holder.highlight.setBackgroundResource(R.drawable.star_inactive_icon);
        }
        holder.itemView.setTag(notes.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView date, title, text;
        private ImageButton highlight;
        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            highlight = itemView.findViewById(R.id.highlight);

            highlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = highlight.getTag().toString();
                    String id = itemView.getTag().toString();

                    v.getContext();
                    db = v.getContext().openOrCreateDatabase("notes.db", Context.MODE_PRIVATE, null);
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