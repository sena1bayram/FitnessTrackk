package com.example.fitnesstrack.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.models.ChatMessage;
import com.example.fitnesstrack.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final int MSG_TYPE_RIGHT = 0;

    private final int MSG_TYPE_LEFT = 1;
    private Context context;
    private List<ChatMessage> chatList;

    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<ChatMessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.list_chat_right, parent, false);
            return new ChatViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.list_chat_left, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatList.get(position);
        switch (holder.getItemViewType()) {
            case MSG_TYPE_LEFT:
                holder.txtName.setText(chatMessage.getUserName());
                break;
            case MSG_TYPE_RIGHT:
                break;
        }
        holder.bind(chatMessage);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid().toString();
        String getsenderid = chatMessage.getSenderId();

        if (chatMessage.getSenderId().equalsIgnoreCase(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public ImageView chatImageView;
        public TextView txtName;
        public TextView txtShowMessage;
        public TextView txtMsgTime;
        public ImageView imgMsgSeen;
        public TextView txtOnlyDate;
        public ImageView imgPath;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOnlyDate = itemView.findViewById(R.id.txtOnlyDate);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            txtShowMessage = itemView.findViewById(R.id.txtShowMessage);
            txtName = itemView.findViewById(R.id.txtName);
            imgMsgSeen = itemView.findViewById(R.id.imgMsgSeen);
            txtMsgTime = itemView.findViewById(R.id.txtMsgTime);
            imgPath = itemView.findViewById(R.id.imgPath);
        }

        public void bind(ChatMessage chatMessage) {
            txtShowMessage.setText(chatMessage.getMessage());
            txtMsgTime.setText(chatMessage.getDateTime());
            txtOnlyDate.setText(Validation.formatStringToDate(chatMessage.getDateTime()));
        }
    }
    private static String convertDateTimeFormat(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date date = inputFormat.parse(inputDateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
