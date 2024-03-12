package com.example.fitnesstrack.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fitnesstrack.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class TaskNameRecyclerAdapter extends ExpandableRecyclerViewAdapter<OSViewHolder, TaskNameViewHolder> {
    private Activity activity;
    private OSViewHolder expandedViewHolder; // Genişletilmiş ViewHolder'ı takip etmek için değişken

    public TaskNameRecyclerAdapter(Activity activity, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.activity = activity;
    }

    @Override
    public OSViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_view_holder, parent, false);

        return new OSViewHolder(view);
    }

    @Override
    public TaskNameViewHolder onCreateChildViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.child_view_holder, parent, false);
        Log.i("RecyclerAdapter", "onCreateChildViewHolder");

        return new TaskNameViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(TaskNameViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        TaskNameView phone = ((DoctorTasksList) group).getItems().get(childIndex);
        holder.onBind(phone,group);
        Log.i("RecyclerAdapter", "onBindChildViewHolder");
    }

    @Override
    public void onBindGroupViewHolder(OSViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupName(group);

        Log.i("Adapter", "onBindGroupViewHolder");

    }

}
