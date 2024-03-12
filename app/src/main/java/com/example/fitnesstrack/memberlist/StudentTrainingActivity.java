package com.example.fitnesstrack.memberlist;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.models.FitnessTrackTraining;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.models.Tasks;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.example.fitnesstrack.views.StudentDashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentTrainingActivity extends MyBaseActivity {
    private Student student;
    private ListView taskListView;
    private Button btnMember;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    List<HashMap<String, String>> aList;
    private ArrayList<String> lngList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_training);
        getDataExtras(getIntent().getExtras());
        initView();
        replaceProgressbar();
        getTraining();
        onClickListener();
    }

    private void onClickListener() {
    }

    private void getTraining() {
        aList = new ArrayList<HashMap<String, String>>();

        CollectionReference trainingCollectionRef = db.collection("FitnessTrackTraining");
        Query trainingQuery = trainingCollectionRef.whereEqualTo("coachId", student.getTeacherId()).
                whereEqualTo("groupName", student.getGroupId());

        trainingQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<FitnessTrackTraining> taskList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            // Öğrenci verilerine erişerek istediğiniz işlemleri yapabilirsiniz
                            String trainingId = documentSnapshot.getId();
                            String coachId = documentSnapshot.getString("coachId");
                            String groupName = documentSnapshot.getString("groupName");
                            Boolean taskCompleted = documentSnapshot.getBoolean("completed");
                            String startDate = documentSnapshot.getString("startDate");
                            String endDate = documentSnapshot.getString("endDate");

                            Map<String, Object> tasksMap = (Map<String, Object>) documentSnapshot.getData().get("tasks");
                            String taskName = (String) tasksMap.get("taskName");
                            String taskInformation = (String) tasksMap.get("taskInformation");
                            String taskRegisteredDate = documentSnapshot.getString("taskRegisteredDate");

                            Tasks tasks = new Tasks();
                            tasks.setTaskName(taskName);
                            tasks.setTaskInformation(taskInformation);
                            tasks.setTaskRegisteredDate(taskRegisteredDate);

                            FitnessTrackTraining fitnessTrackTraining = new FitnessTrackTraining(trainingId,
                                    coachId,groupName, tasks,
                                    startDate, endDate, taskCompleted);
                            taskList.add(fitnessTrackTraining);
                        }
                        sortChatMessagesByDate(taskList);
                        for(FitnessTrackTraining ft: taskList) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("listview_title", ft.getTasks().getTaskName() +"\n"+ ft.getStartDate());
                            hm.put("listview_image", Integer.toString(R.drawable.ic_task_student));
                            aList.add(hm);
                        }
                        /*
                        ArrayAdapter<FitnessTrackTraining> adapter = new ArrayAdapter<FitnessTrackTraining>(StudentTrainingActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, taskList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                TextView text1 = view.findViewById(android.R.id.text1);
                                TextView text2 = view.findViewById(android.R.id.text2);

                                FitnessTrackTraining training = taskList.get(position);
                                text1.setText(training.getTasks().getTaskName());
                                text2.setText(training.getStartDate().toString());

                                return view;
                            }
                        };
                        */
                        String[] from = {"listview_image", "listview_title"};
                        int[] to = {R.id.listview_image, R.id.listview_item_title};
                        SimpleAdapter simpleAdapter = new SimpleAdapter(StudentTrainingActivity.this, aList, R.layout.listview_activity_student_tasks, from, to);

                        taskListView.setAdapter(simpleAdapter);

                    } else {
                        // Koç belgesi bulunamadı
                    }
                } else {
                    // Firestore sorgusu başarısız oldu
                }
            }
        });
    }

    private void sortChatMessagesByDate(List<FitnessTrackTraining> taskList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Collections.sort(taskList, new Comparator<FitnessTrackTraining>() {
            @Override
            public int compare(FitnessTrackTraining message1, FitnessTrackTraining message2) {
                LocalDateTime dateTime1 = convertStringToDate(message1.getStartDate(), formatter);
                LocalDateTime dateTime2 = convertStringToDate(message2.getStartDate(), formatter);

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
    private void initView() {
        progressBar = findViewById(R.id.student_training_progressBar);
        btnMember = findViewById(R.id.startMemberbutton);
        taskListView = findViewById(R.id.recycler_view_add_task_list);
        db = FirebaseFirestore.getInstance();
        lngList = new ArrayList<>();

        Handler handler = new Handler();
        handler.postDelayed(() -> updateProgressBar(), 2000);
    }
    private void clearRecyclerView() {
        if (taskListView.getAdapter() != null) {
            taskListView.setAdapter(null);
        }
    }
    private void updateProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void replaceProgressbar() {
    }

    private void getDataExtras(Bundle extras) {
        student = (Student) extras.getSerializable("Student");
    }
    public void backToDashboard(View view) {
        Intent intent = new Intent(this, StudentDashboardActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
        finish();
    }

    public void makeProcess(View view) {
        Intent intent = new Intent(this, StudentDashboardActivity.class);
        intent.putExtra("Student", student);
        startActivity(intent);
        finish();
    }
}