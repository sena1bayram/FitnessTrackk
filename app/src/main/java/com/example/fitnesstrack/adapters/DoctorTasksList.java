package com.example.fitnesstrack.adapters;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class DoctorTasksList extends ExpandableGroup<TaskNameView> {

    public DoctorTasksList(String title, List<TaskNameView> items) {
        super(title, items);
    }
}
