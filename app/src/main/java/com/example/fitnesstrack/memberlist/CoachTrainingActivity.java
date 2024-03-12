package com.example.fitnesstrack.memberlist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.adapters.DoctorTasksList;
import com.example.fitnesstrack.adapters.TaskNameRecyclerAdapter;
import com.example.fitnesstrack.adapters.TaskNameView;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Tasks;
import com.example.fitnesstrack.models.FitnessTrackTraining;
import com.example.fitnesstrack.views.CoachDashboardActivity;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.aviran.cookiebar2.CookieBar;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CoachTrainingActivity extends MyBaseActivity {
    private static final int VIBRATION_DURATION = 50;
    private CookieBar currentCookieBar;
    private String globalSelectedDate = ""; // Seçilen tarihi tutmak için bir değişken
    private FirebaseFirestore db;
    private Coach coach;
    Button addTaskForGroup;
    ArrayList<String> groupIds;
    private Spinner spin_coachGroups;
    private ProgressBar progressBar;
    Query teamsRef;
    List<String> teamsList;
    RecyclerView recyclerView;
    private ArrayList<DoctorTasksList> device_about_question_list;
    private TaskNameRecyclerAdapter coach_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_training);
        getDataExtras(getIntent().getExtras());
        initComponent();
        loadSpinnerData();
        onClickListener();
        groupIds = getGroupIdsFromFirestore();
    }

    private void onClickListener() {
        addTaskForGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskForGroupToStudents();
            }
        });
        spin_coachGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearRecyclerView();
                String team = parent.getSelectedItem().toString();
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
                setRecyclerViewByCoachId(coach.getCoachId(),team);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    private void clearRecyclerView() {
        if (recyclerView.getAdapter() != null) {
            recyclerView.setAdapter(null);
        }
    }
    private void addTaskForGroupToStudents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CoachTrainingActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_task_details, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ImageView imv_task_exit = dialogView.findViewById(R.id.iv_task_exit);
        Button btnDialogAddTask = dialogView.findViewById(R.id.btn_add_task_group);
        EditText et_group_task_name = dialogView.findViewById(R.id.et_group_task_name);
        EditText et_group_sub_task_information_name = dialogView.findViewById(R.id.et_group_sub_task_information_name);
        ListView listExistingGroupTasks  = dialogView.findViewById(R.id.listViewTask);
        Spinner chooseGroupSpinner = dialogView.findViewById(R.id.chooseGroup);
        Button btSetTaskEnd = dialogView.findViewById(R.id.btnSetTaskEnd);
        TextView txChooseDateTask = dialogView.findViewById(R.id.txChooseDateTask);

        // Kullanıcı tarih seçmeden önce düğmeyi etkinleştiremez
        imv_task_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btSetTaskEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Check if the device has a vibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    // Vibrate for the specified duration
                    vibrator.vibrate(VIBRATION_DURATION);
                }
                DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);

                    Calendar currentDate = Calendar.getInstance();

                    if (selectedDate.before(currentDate)) {
                        // Gelecekteki bir tarih seçildiğinde bir hata mesajı gösterin
                        Toast.makeText(getApplicationContext(), getString(R.string.select_a_valid_date_of_birth), Toast.LENGTH_SHORT).show();
                    } else {
                        String date = String.format("%02d.%02d.%d", dayOfMonth, monthOfYear + 1, year);
                        globalSelectedDate = date; // Seçilen tarihi değişkene atayın
                        txChooseDateTask.setText("End Date: " + globalSelectedDate);
                    }
                };

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CoachTrainingActivity.this, dateSetListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Bugünden önceki tarihleri devre dışı bırakır
                //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        //////////////////////////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference teacherCollection = db.collection("FitnessTrackTraining");

        Query taskQuery = teacherCollection.whereEqualTo("coachId", coach.getCoachId()).whereEqualTo("groupName",
                et_group_task_name.getText().toString().trim());

        
        taskQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<FitnessTrackTraining> taskListDialog = new ArrayList<>();
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

                            Tasks tasks = new Tasks();
                            tasks.setTaskName(taskName);
                            tasks.setTaskInformation(taskInformation);
                            FitnessTrackTraining fitnessTrackTraining = new FitnessTrackTraining(trainingId,
                                    coachId,groupName, tasks,
                                    startDate, endDate, taskCompleted);
                            taskListDialog.add(fitnessTrackTraining);
                        }
                        sortChatMessagesByDate(taskListDialog);

                        ArrayAdapter<FitnessTrackTraining> adapter = new ArrayAdapter<FitnessTrackTraining>(CoachTrainingActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, taskListDialog) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                TextView text1 = view.findViewById(android.R.id.text1);
                                TextView text2 = view.findViewById(android.R.id.text2);

                                FitnessTrackTraining training = taskListDialog.get(position);
                                text1.setText(training.getTasks().getTaskName());
                                text2.setText(training.getStartDate().toString());

                                return view;
                            }
                        };

                        listExistingGroupTasks.setAdapter(adapter);
                    } else {
                        // Grup boş veya öğrenci bulunamadı
                    }
                } else {
                    // Firestore sorgusu başarısız oldu
                }
            }
        });
        /////////////////////////
        ArrayAdapter<String> adapter_spinner = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, teamsList);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGroupSpinner.setAdapter(adapter_spinner);

        btnDialogAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_group_task_name.getText().toString().trim();
                String task_information_name = et_group_sub_task_information_name.getText().toString().trim();

                if (TextUtils.isEmpty(name) || teamsList.isEmpty() || TextUtils.isEmpty(globalSelectedDate)) {
                    // Eksik alanlar varsa kullanıcıya bir hata mesajı göster
                    et_group_task_name.setError(getString(R.string.name_cannot_be_empty));
                    Toast.makeText(getApplicationContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();

                    if(teamsList.isEmpty()){
                        cookieBar_error_create(getString(R.string.warning),getString(R.string.dear_coach_there_is_no_version_available_in_our_database));
                    }
                }
                else{
                    String selectedGroupName = chooseGroupSpinner.getSelectedItem().toString();
                    spin_coachGroups.setSelection(adapter_spinner.getPosition(selectedGroupName));
                // Yeni bir FitnessTrackTraining belgesi oluştur
                FitnessTrackTraining newTraining = new FitnessTrackTraining();
                newTraining.setCoachId(coach.getCoachId());
                newTraining.setGroupName(selectedGroupName);
                newTraining.setStartDate(new Date().toString());
                newTraining.setEndDate(globalSelectedDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault());

                newTraining.setTasks(new Tasks(name, task_information_name,
                        dateFormat.format(new Date()).toString())); // Yeni bir Task nesnesi oluşturarak task'ı ekleyin

                // Firestore'da "FitnessTrackTraining" koleksiyonuna belge ekle
                db.collection("FitnessTrackTraining")
                        .add(newTraining)
                        .addOnSuccessListener(documentReference -> {
                            clearRecyclerView();
                            setRecyclerViewByCoachId(coach.getCoachId(), selectedGroupName);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();                        });

                dialog.dismiss();
            }
            }
        });
        dialog.show();
    }
    private ArrayList<String> getGroupIdsFromFirestore() {
        CollectionReference groupsCollectionRef = db.collection("FitnessTrackTeacher");
        DocumentReference coachDocument = groupsCollectionRef.document(coach.getCoachId());
        ArrayList<String> groupIds = new ArrayList<>();
        coachDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> retrievedGroupIds = (ArrayList<String>) document.get("groupIds");
                        if (retrievedGroupIds != null) {
                            groupIds.addAll(retrievedGroupIds);
                            // Add the retrieved group IDs to the existing groupIds list
                        }
                    } else {
                        // Coach document not found
                    }
                } else {
                    Log.d("TAG", "Error getting coach document: ", task.getException());
                }
            }
        });

        return groupIds;
    }
    private void loadSpinnerData() {

        teamsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, teamsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_coachGroups.setAdapter(adapter);

        teamsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getId().equals(coach.getCoachId())){
                        List<String> groupIds = (List<String>) document.get("groupIds");
                        int size = groupIds.size();
                        if(size == 0){
                            cookieBar_error_create(getString(R.string.warning),getString(R.string.there_are_no_groups_that_can_be_listed));
                        }
                        for(int i = 0; i< size; i++)
                            teamsList.add(groupIds.get(i));

                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setRecyclerViewByCoachId(String coachId, String team) {
        device_about_question_list = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference teacherCollection = db.collection("FitnessTrackTraining");

        Query taskQuery = teacherCollection.whereEqualTo("coachId", coachId);

        if (team != null && !team.isEmpty()) {
            taskQuery = taskQuery.whereEqualTo("groupName", team);
        } else {
            Toast.makeText(this, "Team is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        taskQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        clearRecyclerView();
                        for(FitnessTrackTraining ft: taskList) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("listview_title", ft.getTasks().getTaskName() +"\n"+ ft.getStartDate());
                            hm.put("listview_image", Integer.toString(R.drawable.ic_task));
                            ArrayList<TaskNameView> answer_one = new ArrayList<>();
                            answer_one.add(new TaskNameView( ft.getTasks().getTaskInformation() + "\n" + ft.getStartDate()));

                            device_about_question_list.add(new DoctorTasksList(ft.getTasks().getTaskName(), answer_one));
                        }
                        coach_adapter = new TaskNameRecyclerAdapter(CoachTrainingActivity.this, device_about_question_list);
                        recyclerView.setAdapter(coach_adapter);
                    } else {
                        // Grup boş veya öğrenci bulunamadı
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
    private void initComponent() {
        db = FirebaseFirestore.getInstance();
        addTaskForGroup =findViewById(R.id.startCoachbutton);
        progressBar = findViewById(R.id.progressBarTraining);
        spin_coachGroups = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.recycler_view_add_task_list);
        device_about_question_list = new ArrayList<>();
        teamsList = new ArrayList<>();

        Handler handler = new Handler();
        handler.postDelayed(() -> updateProgressBar(), 2000);

        CollectionReference studentCollection = db.collection("FitnessTrackTeacher");
        teamsRef = studentCollection;


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        coach_adapter = new TaskNameRecyclerAdapter(this, device_about_question_list);
        recyclerView.setAdapter(coach_adapter);
    }
    private void getDataExtras(Bundle extras) {
        coach = (Coach) extras.getSerializable("Coach");
    }

    private void updateProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CoachDashboardActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    public void BacktoDashboard(View v) {
        Intent intent = new Intent(this, CoachDashboardActivity.class);
        intent.putExtra("Coach", coach);
        startActivity(intent);
        finish();
    }
    private void cookieBar_error_create(String messageType, String message) {
        // Mevcut CookieBar'ı kontrol edin ve kapatın
        if (currentCookieBar != null) {
            currentCookieBar.dismiss();
        }
        currentCookieBar = CookieBar.build(this)
                .setTitle(messageType)
                .setMessage(message)
                .setMessageColor(R.color.fancyMessage)
                .setBackgroundColor(R.color.cookiebar)
                .setIcon(R.drawable.ic_launcher_foreground_snackbar)
                .setDuration(5000)
                .setCookiePosition(CookieBar.BOTTOM)
                .setAction(R.string.Close, () -> CookieBar.dismiss(CoachTrainingActivity.this))
                .show();
    }
}