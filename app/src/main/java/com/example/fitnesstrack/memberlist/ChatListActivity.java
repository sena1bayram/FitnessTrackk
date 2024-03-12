package com.example.fitnesstrack.memberlist;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_COLLECTION_CHAT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.adapters.ChatAdapter;
import com.example.fitnesstrack.models.ChatMessage;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatListActivity extends MyBaseActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private Student student;
    private ImageView mSendButton;
    private EditText mInputText;

    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        student = (Student) getIntent().getExtras().getSerializable("Student");
        mInputText=(EditText) findViewById(R.id.editTextChat);
        recyclerView = findViewById(R.id.recycler_view_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        recyclerView.setAdapter(chatAdapter);
        mSendButton = findViewById(R.id.sendButton);
        fetchMessages(student.getTeacherId());
        onClickListener();
    }
    private void onClickListener() {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        String selectedGroupId = student.getGroupId();
        if(!input.equals("")){
            sendMessageToGroup(selectedGroupId, input, student.getTeacherId());
            mInputText.setText("");
        }
    }

    private void sendMessageToGroup(String groupId, String message, String coachId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());

        time = dateFormat.format(new Date()).toString();

        FirebaseFirestore.getInstance().collection("FitnessTrackStudent")
                .whereEqualTo("groupIds", groupId)
                .whereEqualTo("teacherId", coachId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    // Iterate through the query results
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve the student ID
                        String studentId = documentSnapshot.getId();

                        // Create a HashMap with the sender ID, receiver ID, message, and timestamp
                        HashMap<String, Object> messageMap = new HashMap<>();
                        messageMap.put("senderId", student.getStudentId());
                        messageMap.put("receiverId", studentId);
                        messageMap.put("message", message);
                        messageMap.put("timestamp", time);
                        messageMap.put("groupIds", groupId);
                        messageMap.put("userName", student.getUsername());

                        // Add the message to the Firestore collection
                        FirebaseFirestore.getInstance().collection(EXTRA_COLLECTION_CHAT)
                                .add(messageMap)
                                .addOnSuccessListener(documentReference -> {
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();

                });

        // Create a HashMap with the sender ID, receiver ID, message, and timestamp
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("senderId", student.getStudentId());
        messageMap.put("receiverId", student.getTeacherId());
        messageMap.put("message", message);
        messageMap.put("timestamp", time);
        messageMap.put("groupIds", groupId);
        messageMap.put("userName", student.getUsername());

        // Add the message to the Firestore collection
        FirebaseFirestore.getInstance().collection(EXTRA_COLLECTION_CHAT)
                .add(messageMap)
                .addOnSuccessListener(documentReference -> {
                    setRecyclerViewByID(student.getGroupId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
                });
    }
    // grup mesajlarını çeker
    private void setRecyclerViewByID(String groupName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FitnessTrackChat")
                .whereEqualTo("senderId", student.getStudentId())
                .whereEqualTo("groupIds", groupName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatMessages.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            String messageId = documentSnapshot.getId();
                            String receiverId = documentSnapshot.getString("receiverId");
                            String senderId = documentSnapshot.getString("senderId");
                            String message = documentSnapshot.getString("message");
                            String messageTimestamp = documentSnapshot.getString("timestamp");
                            String messageUserName = documentSnapshot.getString("userName");

                            ChatMessage chatMessage = new ChatMessage(messageId, senderId, receiverId,
                                    message, messageTimestamp.toString(), groupName, messageUserName);

                            boolean state_message = false;

                            for (ChatMessage compareMessage : chatMessages) {
                                if(compareMessage.getDateTime().equals(messageTimestamp.toString())){
                                    state_message = true;
                                }
                            }
                            if(!state_message || chatMessages.isEmpty())
                                chatMessages.add(chatMessage);


                        }

                        sortChatMessagesByDate(chatMessages);
                        chatAdapter.notifyDataSetChanged();

                        db.collection("FitnessTrackChat")
                                .whereEqualTo("receiverId", student.getStudentId())
                                .whereEqualTo("groupIds", groupName)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {

                                        for (DocumentSnapshot documentSnapshot : task2.getResult().getDocuments()) {
                                            String messageId = documentSnapshot.getId();
                                            String receiverId = documentSnapshot.getString("receiverId");
                                            String senderId = documentSnapshot.getString("senderId");
                                            String message = documentSnapshot.getString("message");
                                            String messageTimestamp = documentSnapshot.getString("timestamp");
                                            String messageUserName = documentSnapshot.getString("userName");

                                            ChatMessage chatMessage = new ChatMessage(messageId, senderId, receiverId,
                                                    message, messageTimestamp.toString(), groupName, messageUserName);

                                            boolean state_message = false;

                                            for (ChatMessage compareMessage : chatMessages) {
                                                if(compareMessage.getDateTime().equals(messageTimestamp.toString())){
                                                    state_message = true;
                                                }
                                            }
                                            if(!state_message || chatMessages.isEmpty())
                                                chatMessages.add(chatMessage);


                                        }
                                        // chatMessages listesini tarih damgasına göre sırala
                                        sortChatMessagesByDate(chatMessages);
                                        chatAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private void sortChatMessagesByDate(List<ChatMessage> chatMessages) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);

        Collections.sort(chatMessages, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage message1, ChatMessage message2) {
                LocalDateTime dateTime1 = convertStringToDate(message1.getDateTime(), formatter);
                LocalDateTime dateTime2 = convertStringToDate(message2.getDateTime(), formatter);

                if (dateTime1 != null && dateTime2 != null) {
                    return dateTime1.compareTo(dateTime2);
                }
                return 0;
            }
        });
    }
    private static LocalDateTime convertStringToDate(String dateTime, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
    }

    public void removeChatMessage(ChatMessage chatMessage) {
        chatMessages.remove(chatMessage);
        chatAdapter.notifyDataSetChanged();
    }
    // koç mesajlarını çeker
    private void fetchMessages(String coachId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FitnessTrackChat")
                .whereEqualTo("senderId", student.getStudentId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatMessages.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            String messageId = documentSnapshot.getId();
                            String receiverId = documentSnapshot.getString("receiverId");
                            String senderId = documentSnapshot.getString("senderId");
                            String message = documentSnapshot.getString("message");
                            String messageTimestamp = documentSnapshot.getString("timestamp");
                            String messageUserName = documentSnapshot.getString("userName");

                            ChatMessage chatMessage = new ChatMessage(messageId, senderId, receiverId,
                                    message, messageTimestamp, student.getGroupId(), messageUserName);

                            boolean state_message = false;
                            for (ChatMessage compareMessage : chatMessages) {
                                if(compareMessage.getDateTime().equals(messageTimestamp.toString())){
                                    state_message = true;
                                }
                            }
                            if(!state_message || chatMessages.isEmpty())
                                chatMessages.add(chatMessage);
                        }
                        sortChatMessagesByDate(chatMessages);
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        // Firestore sorgusu başarısız oldu
                        // Hata işleme yapabilirsiniz
                    }
                });

        db.collection("FitnessTrackChat")
                .whereEqualTo("receiverId", student.getStudentId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatMessages.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            String messageId = documentSnapshot.getId();
                            String receiverId = documentSnapshot.getString("receiverId");
                            String senderId = documentSnapshot.getString("senderId");
                            String message = documentSnapshot.getString("message");
                            String messageTimestamp = documentSnapshot.getString("timestamp");
                            String messageUserName = documentSnapshot.getString("userName");

                            ChatMessage chatMessage = new ChatMessage(messageId, senderId, receiverId,
                                    message, messageTimestamp, student.getGroupId(), messageUserName);

                            chatMessages.add(chatMessage);
                        }
                        sortChatMessagesByDate(chatMessages);
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        // Firestore sorgusu başarısız oldu
                        // Hata işleme yapabilirsiniz
                    }
                });
    }
}