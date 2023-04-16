package com.aurux.rockstat.ui.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aurux.rockstat.R;
import com.aurux.rockstat.databinding.FragmentHistoryBinding;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aurux.rockstat.data.database.AppDatabase;
import com.aurux.rockstat.data.models.ClimbLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private AppDatabase appDatabase;

    private RecyclerView rvClimbLogEntries;
    private ClimbLogEntryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel dashboardViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvClimbLogEntries = root.findViewById(R.id.rv_climb_log_entries);
        adapter = new ClimbLogEntryAdapter(new ArrayList<>());
        rvClimbLogEntries.setAdapter(adapter);

        rvClimbLogEntries.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpItemTouchHelper();

        // Fetch data from the database and update the adapter
        appDatabase = AppDatabase.getInstance(getActivity());
        appDatabase.climbLogEntryDao().getAllClimbLogEntries().observe(getViewLifecycleOwner(), new Observer<List<ClimbLogEntry>>() {
            @Override
            public void onChanged(List<ClimbLogEntry> climbLogEntries) {
                Collections.sort(climbLogEntries, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                adapter.setClimbLogEntries(climbLogEntries);
            }
        });

        return root;
    }

    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ClimbLogEntry logEntryToDelete = adapter.getClimbLogEntry(position);

                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the entry if the user confirms
                                new Thread(() -> appDatabase.climbLogEntryDao().delete(logEntryToDelete)).start();
                                adapter.removeClimbLogEntry(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Revert the swipe if the user cancels
                                adapter.notifyItemChanged(position);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvClimbLogEntries);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}