package com.example.fitnesstrack.adapters;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.fitnesstrack.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class OSViewHolder extends GroupViewHolder {
    private final TextView osName;
    private final View view;
    private boolean expanded = false; // Genişletilmiş durumu takip etmek için değişken

    public OSViewHolder(View itemView) {
        super(itemView);

        osName = itemView.findViewById(R.id.mobile_os);
        view   = itemView.findViewById(R.id.view_header);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void expand() {
        view.setBackgroundResource(R.drawable.bg_item_white_half_bottom);
        osName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_btn_expand, 0); //down_arrow
        Log.i("OSViewHolder", "expand");
        expanded = true; // Genişletildiği zaman expanded değişkenini true yap
    }

    @Override
    public void collapse() {
        view.setBackgroundResource(R.color.white);
        osName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_btn_collapse, 0); //up_arrow
        Log.i("OSViewHolder", "collapse");
        expanded = false; // Daraltıldığı zaman expanded değişkenini false yap
    }

    public void setGroupName(ExpandableGroup group) {
        osName.setText(group.getTitle());
    }
}
