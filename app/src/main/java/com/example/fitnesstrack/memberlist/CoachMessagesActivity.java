package com.example.fitnesstrack.memberlist;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_COLLECTION_CHAT;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_EMAIL;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_MESSAGE;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_RECEIVER_ID;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_SENDER_ID;
import static com.example.fitnesstrack.constants.IConstants.EXTRA_TIMESTAMP;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.adapters.ChatAdapter;
import com.example.fitnesstrack.models.ChatMessage;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.views.CoachDashboardActivity;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CoachMessagesActivity extends MyBaseActivity {

    private Spinner spinnerGroup;
    private TextView txtName;

    private TextView txtSendMessage;

    List<String> teamsList;
    private FirebaseFirestore db;

    Query teamsRef;

    private RecyclerView recyclerView;
    private String coachName;
    private EditText mInputText;
    private ImageView mSendButton;
    String teamidselected;
   Coach coach;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ProgressBar progressBar;

    private String groupName;

    private String global_time_sending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_messages);
        getDataExtras(getIntent().getExtras());
        initComponent();
        initFireBaseStore();
        loadSpinnerData();
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

    private void initFireBaseStore() {
        db = FirebaseFirestore.getInstance();
        CollectionReference studentCollectionForCoach = db.collection("FitnessTrackTeacher");
        teamsRef = studentCollectionForCoach;
    }

    private void initComponent() {
        spinnerGroup = findViewById(R.id.spinnerGroup);
        mInputText = findViewById(R.id.editTextChat);
        txtName = findViewById(R.id.txtMessage);
        txtSendMessage = findViewById(R.id.txtSend_a_message);
        recyclerView = findViewById(R.id.recycler_view_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.loadingPanel);
        teamsList = new ArrayList<>();
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        recyclerView.setAdapter(chatAdapter);
    }

    public void loadSpinnerData(){
        teamsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, teamsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(adapter);

        teamsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getId().equals(coach.getCoachId())){
                        List<String> groupIds = (List<String>) document.get("groupIds");
                        int size = groupIds.size();

                        for(int i = 0; i< size; i++)
                            teamsList.add(groupIds.get(i));

                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearRecyclerView();
                groupName = parent.getSelectedItem().toString();
                // ProgressBar'ı göster
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
                setRecyclerViewByID(groupName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setRecyclerViewByID(String groupName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        chatMessages.clear();
        db.collection("FitnessTrackChat")
                .whereEqualTo("senderId", coach.getCoachId())
                .whereEqualTo("groupIds", groupName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

                            //baaakkkk
                        }
                        // chatMessages listesini tarih damgasına göre sırala
                        sortChatMessagesByDate(chatMessages);
                        chatAdapter.notifyDataSetChanged();

                        db.collection("FitnessTrackStudent")
                                .whereEqualTo("teacherId", coach.getCoachId())
                                .whereEqualTo("groupIds", groupName)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                                        for (DocumentSnapshot documentSnapshot : task1.getResult().getDocuments()) {
                                            String studentId = documentSnapshot.getId();
                                            db.collection("FitnessTrackChat")
                                                    .whereEqualTo("senderId", studentId)
                                                    .whereEqualTo("groupIds", groupName)
                                                    .get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {

                                                            List<DocumentSnapshot> list = task2.getResult().getDocuments();
                                                            for (DocumentSnapshot documentSnapshot1 : list) {
                                                                String messageId = documentSnapshot1.getId();
                                                                String receiverId = documentSnapshot1.getString("receiverId");
                                                                String senderId = documentSnapshot1.getString("senderId");
                                                                String message = documentSnapshot1.getString("message");
                                                                String messageTimestamp = documentSnapshot1.getString("timestamp");
                                                                String messageUserName = documentSnapshot1.getString("userName");

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

                                                                //baaakkkk
                                                            }

                                                            sortChatMessagesByDate(chatMessages);
                                                            chatAdapter.notifyDataSetChanged();
                                                        } else {
                                                            Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            //baaakkkk
                                        }
                                        // chatMessages listesini tarih damgasına göre sırala

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

    private void clearRecyclerView() {

    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        String selectedGroupId = spinnerGroup.getSelectedItem() != null ? spinnerGroup.getSelectedItem().toString() : null;
        if(!input.equals("") && selectedGroupId != null){
            sendMessageToGroup(selectedGroupId, input, coach.getCoachId());
            mInputText.setText("");
        }
    }
    // sendMessageToGroup() method to send a message to all students in a selected group
    private void sendMessageToGroup(String groupId, String message, String coachId) {
        // Retrieve students in the selected group from Firestore
        // Retrieve students in the selected group from Firestore
        FirebaseFirestore.getInstance().collection("FitnessTrackStudent")
                .whereEqualTo("groupIds", groupId)
                .whereEqualTo("teacherId", coachId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());
                    global_time_sending = dateFormat.format(new Date()).toString();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve the student ID
                        String studentId = documentSnapshot.getId();

                        // Create a HashMap with the sender ID, receiver ID, message, and timestamp
                        HashMap<String, Object> messageMap = new HashMap<>();
                        messageMap.put("senderId", coachId);
                        messageMap.put("receiverId", studentId);
                        messageMap.put("message", message);
                        messageMap.put("timestamp", global_time_sending);
                        messageMap.put("groupIds", groupId);
                        messageMap.put("userName", coach.getpersonName());
                        // Add the message to the Firestore collection
                        FirebaseFirestore.getInstance().collection(EXTRA_COLLECTION_CHAT)
                                .add(messageMap)
                                .addOnSuccessListener(documentReference -> {
                                    setRecyclerViewByID(groupName);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();

                });
    }
    // Setup adapter au début.
    public void onStart(){
        super.onStart();
    }

    public void ChangeDate() {

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    private void getDataExtras(Bundle extras) {
        coach = (Coach) extras.getSerializable("Coach");
    }

    public void BacktoDashboard(View v) {
        Intent intent = new Intent(this, CoachDashboardActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
        finish();
    }

}