package com.makarov.wonderfulthoughts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            text = itemView.findViewById(R.id.text);

        }
    }
}
