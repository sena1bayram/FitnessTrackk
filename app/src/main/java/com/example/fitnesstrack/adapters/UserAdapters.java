package com.example.fitnesstrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.managers.Screens;
import com.example.fitnesstrack.managers.Utils;
import com.example.fitnesstrack.models.Coach;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapters extends RecyclerView.Adapter<UserAdapters.ViewHolder> {

    private Context mContext;
    private ArrayList<Coach> mUsers;
    private boolean isChat;
    private FirebaseUser firebaseUser;
    private String theLastMsg, txtLastDate;
    private boolean isMsgSeen = false;
    private int unReadCount = 0;
    private Screens screens;

    public UserAdapters(Context mContext, ArrayList<Coach> usersList, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = Utils.removeDuplicates(usersList);
        this.isChat = isChat;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        screens = new Screens(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_users, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Coach user = mUsers.get(i);
        final String strAbout = user.getpersonEmail();
    }

    private void lastMessage(final String userId, final TextView lastMsg, final TextView lastDate, final ImageView imgPath) {

        theLastMsg = "default";
        txtLastDate = "Now";

    }

    private void lastMessageCount(final String userId, final TextView txtUnreadCounter) {

        isMsgSeen = false;
        unReadCount = 0;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView txtUsername;
        private ImageView imgBGWhite;
        private ImageView imgOn;
        private ImageView imgOff;
        private TextView txtLastMsg;
        private TextView txtLastDate;
        private TextView txtUnreadCounter;
        private ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgBGWhite = itemView.findViewById(R.id.imgBGWhite);
            imgOn = itemView.findViewById(R.id.imgOn);
            imgOff = itemView.findViewById(R.id.imgOff);
            txtLastMsg = itemView.findViewById(R.id.txtLastMsg);
            txtLastDate = itemView.findViewById(R.id.txtLastDate);
            txtUnreadCounter = itemView.findViewById(R.id.txtUnreadCounter);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
