package com.aurux.rockstat.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aurux.rockstat.R;
import com.aurux.rockstat.data.models.ClimbLogEntry;


import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ClimbLogEntryAdapter extends RecyclerView.Adapter<ClimbLogEntryAdapter.ViewHolder> {

    private List<ClimbLogEntry> climbLogEntries;

    public ClimbLogEntryAdapter(List<ClimbLogEntry> climbLogEntries) {
        this.climbLogEntries = climbLogEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new ViewHolder(view);
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm E (dd/MM/yyyy)", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClimbLogEntry climbLogEntry = climbLogEntries.get(position);
        if (climbLogEntry.getRouteName().equals("")) {
            holder.tvRouteName.setText(climbLogEntry.getSelectedPlace());
        }
        else {
            holder.tvRouteName.setText(climbLogEntry.getRouteName());
        }

        holder.tvGrade.setText(climbLogEntry.getGrade());
        holder.tvClimbingType.setText(climbLogEntry.getClimbingType());
        if (climbLogEntry.getAttempts() == 1) {
            holder.tvAttempts.setText("Flashed!");
        }
        else {
            holder.tvAttempts.setText(String.valueOf(climbLogEntry.getAttempts())+" attempts");
        }

        holder.tvTimestamp.setText(formatTimestamp(climbLogEntry.getTimestamp()));
        holder.ratingBar.setRating(climbLogEntry.getRating());
        holder.ratingBar.setIsIndicator(true);
    }

    @Override
    public int getItemCount() {
        return climbLogEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRouteName, tvGrade, tvClimbingType, tvAttempts, tvTimestamp;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tv_route_name);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            tvClimbingType = itemView.findViewById(R.id.tv_climbing_type);
            tvAttempts = itemView.findViewById(R.id.tv_attempts);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }

    public ClimbLogEntry getClimbLogEntry(int position) {
        return climbLogEntries.get(position);
    }

    public void removeClimbLogEntry(int position) {
        climbLogEntries.remove(position);
        notifyItemRemoved(position);
    }


    public void setClimbLogEntries(List<ClimbLogEntry> climbLogEntries) {
        this.climbLogEntries = climbLogEntries;
        notifyDataSetChanged();
    }

}

