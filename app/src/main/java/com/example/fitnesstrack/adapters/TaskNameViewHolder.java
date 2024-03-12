package com.example.fitnesstrack.adapters;

import android.view.View;
import android.widget.TextView;

import com.example.fitnesstrack.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class TaskNameViewHolder extends ChildViewHolder {
    private final TextView phoneName;

    public TaskNameViewHolder(View itemView) {
        super(itemView);

        phoneName = itemView.findViewById(R.id.phone_name);
    }

    public void onBind(TaskNameView phone, ExpandableGroup group) {
        phoneName.setText(phone.getName());
//        if (group.getTitle().equals("Android")) {
////            phoneName.setVisibility(View.GONE);
//            phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_btn_expand, 0, 0, 0);
//        } else if (group.getTitle().equals("iOS")) {
//            phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_btn_collapse, 0, 0, 0);
//        } else {
//            phoneName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_btn_expand, 0, 0, 0);
//        }
    }
}
