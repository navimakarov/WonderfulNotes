package com.makarov.wonderfulthoughts;

import android.content.Intent;
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
        holder.itemView.setTag(position);
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
                    if(tag.equals("inactive")){
                        highlight.setTag("active");
                        highlight.setBackgroundResource(R.drawable.star_active_icon);
                    }
                    else{
                        highlight.setTag("inactive");
                        highlight.setBackgroundResource(R.drawable.star_inactive_icon);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("id", String.valueOf(thoughts.size() - Integer.parseInt(itemView.getTag().toString())));
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
