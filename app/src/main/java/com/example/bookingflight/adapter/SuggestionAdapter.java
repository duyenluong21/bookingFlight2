package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private List<String> suggestions;
    private OnSuggestionClickListener suggestionClickListener;

    public SuggestionAdapter(List<String> suggestions, OnSuggestionClickListener suggestionClickListener) {
        this.suggestions = suggestions;
        this.suggestionClickListener = suggestionClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String suggestion = suggestions.get(position);
        holder.suggestionText.setText(suggestion);
        holder.itemView.setOnClickListener(v -> {
            // Call the listener when an item is clicked
            suggestionClickListener.onSuggestionClick(suggestion);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView suggestionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.suggestionText);
        }
    }
}
