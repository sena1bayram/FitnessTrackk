package com.example.fitnesstrack.memberlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnesstrack.R;
import com.example.fitnesstrack.adapters.StudentAdapter;
import com.example.fitnesstrack.models.Coach;
import com.example.fitnesstrack.models.Student;
import com.example.fitnesstrack.views.CoachDashboardActivity;
import com.example.fitnesstrack.views.MyBaseActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CoachMemberlistActivity extends MyBaseActivity {
    private FirebaseFirestore db;
    private Coach coach;
    Query teamsRef;
    private ImageView img;
    private TextView txtNewTeam,txtGroupAdd;
    List<String> teamsList;
    private String GroupName;
    ArrayList<Student> selectedStudents;
    ArrayList<String> groupIds;

    ListView recyclerView;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_memberlist);
        getDataExtras(getIntent().getExtras());
        initComponent();

        db = FirebaseFirestore.getInstance();
        CollectionReference studentCollection = db.collection("FitnessTrackTeacher");
        teamsRef = studentCollection;

        loadSpinnerData();
        onClickListener();
        selectedStudents = new ArrayList<>();
        groupIds = getGroupIdsFromFirestore(); // Firestore'dan grup kimliklerini almak için uygun bir yöntem kullanın

    }

    private void onClickListener() {

        txtGroupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllStudentsDialog();
            }
        });
        txtNewTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CoachMemberlistActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_group_to_student, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                ImageView imv_exit = dialogView.findViewById(R.id.iv_exit);
                Button btnDialogCalculate = dialogView.findViewById(R.id.btn_save_group);
                EditText et_group_name = dialogView.findViewById(R.id.et_group_name);
                ListView listExistingGroup  = dialogView.findViewById(R.id.list_existing_group);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachMemberlistActivity.this, android.R.layout.simple_list_item_1, groupIds);
                listExistingGroup.setAdapter(adapter);
                imv_exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                btnDialogCalculate.setOnClickListener(view -> {
                    String name = et_group_name.getText().toString().trim();

                    if (name.isEmpty()) {
                        et_group_name.setError(getString(R.string.name_cannot_be_empty));
                        return;
                    }

                    DocumentReference teacherRef = db.collection("FitnessTrackTeacher").document(coach.getCoachId());
                    teacherRef.update("groupIds", FieldValue.arrayUnion(name))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CoachMemberlistActivity.this, getString(R.string.student_added_to_teachers_studentIds), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CoachMemberlistActivity.this, getString(R.string.failed_to_add_student_to_coachs_student_id_list), Toast.LENGTH_SHORT).show();
                                }
                            });

                    Toast.makeText(CoachMemberlistActivity.this, getString(R.string.registration_completed_successfully), Toast.LENGTH_SHORT).show();
                    groupIds = getGroupIdsFromFirestore(); // Firestore'dan grup kimliklerini almak için uygun bir yöntem kullanın
                    dialog.dismiss();
                });

                dialog.show();
            }
        });
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

    private void showAllStudentsDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentCollection = db.collection("FitnessTrackStudent");
        Query studentQuery = studentCollection;

        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Student> studentList = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String studentId = documentSnapshot.getId();
                            String studentAddres = documentSnapshot.getString("address");
                            String studentBirthday = documentSnapshot.getString("birthday");
                            String studentEmail = documentSnapshot.getString("email");
                            String studentGender = documentSnapshot.getString("gender");
                            String studentHeight = documentSnapshot.getString("height");
                            String studentPassword = documentSnapshot.getString("password");
                            String studentTeacherId = documentSnapshot.getString("teacherId");
                            String studentType = documentSnapshot.getString("type");
                            String studentUsername = documentSnapshot.getString("username");
                            String studentWeight = documentSnapshot.getString("weight");
                            String studentGroupId = documentSnapshot.getString("groupIds");
                            String studentProfileImage = documentSnapshot.getString("image");
                            String studentRegisteredDate = documentSnapshot.getString("registeredDate");
                            Student student = new Student(studentId, studentAddres, studentBirthday,
                                    studentEmail, studentGender, studentHeight, studentPassword,
                                    studentTeacherId, studentType, studentUsername, studentWeight,
                                    studentGroupId, studentProfileImage, studentRegisteredDate);

                            if (studentGroupId.equals("null"))
                                studentList.add(student);
                        }

                        if (studentList.isEmpty()) {
                            Toast.makeText(CoachMemberlistActivity.this, getString(R.string.student_list_empty), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(CoachMemberlistActivity.this);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_add_student_to_group, null);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();

                        ImageView imv_exit = dialogView.findViewById(R.id.iv_exit);
                        Spinner chooseGroupSpinner = dialogView.findViewById(R.id.chooseGroup);

                        teamsList = new ArrayList<>();
                        ArrayAdapter<String> adapter_spinner = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, teamsList);
                        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        chooseGroupSpinner.setAdapter(adapter_spinner);

                        teamsRef.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    if(document.getId().equals(coach.getCoachId())){

                                        List<String> groupIds = (List<String>) document.get("groupIds");
                                        int size = groupIds.size();

                                        for(int i = 0; i< size; i++)
                                            teamsList.add(groupIds.get(i));

                                    }
                                }
                                adapter_spinner.notifyDataSetChanged();
                            }
                        });

                        chooseGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                GroupName = parent.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        imv_exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(CoachMemberlistActivity.this, R.layout.list_item, R.id.textView, studentList) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(R.id.textView);
                                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                                Student student = getItem(position);
                                if (student != null) {
                                    String studentName = student.getUsername() + " - " + student.getGender() + " - " + student.getBirthday();
                                    textView.setText(studentName);
                                }
                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        Student student = getItem(position);
                                        if (isChecked) {
                                            selectedStudents.add(student);
                                        } else {
                                            selectedStudents.remove(student);
                                        }
                                    }
                                });

                                return view;
                            }
                        };
                        ListView listView = dialogView.findViewById(R.id.listView);
                        listView.setAdapter(adapter);

                        Button btn_save = dialogView.findViewById(R.id.btn_save_group);
                        btn_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> selectedStudentIds = new ArrayList<>();
                                for (Student student : selectedStudents) {
                                    selectedStudentIds.add(student.getStudentId());
                                }

                                for (Student student : selectedStudents){
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference studentRef = db.collection("FitnessTrackStudent").document(student.getStudentId());
                                    studentRef.update("groupIds", GroupName)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(CoachMemberlistActivity.this, getString(R.string.student_coachId_updated), Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CoachMemberlistActivity.this, getString(R.string.failed_to_update_student_coachId), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else {
                        Toast.makeText(CoachMemberlistActivity.this, getString(R.string.student_list_empty), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void initComponent() {
        img = findViewById(R.id.backicon);
        txtNewTeam = findViewById(R.id.txtNewTeam);
        txtGroupAdd = findViewById(R.id.txtGroupAdd);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.loadingPanel);
        teamsList = new ArrayList<>();
    }

    private void getDataExtras(Bundle extras) {
        coach = (Coach) extras.getSerializable("Coach");
    }
    public void loadSpinnerData() {
        Spinner spinner = findViewById(R.id.spinner);
        teamsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, teamsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                setRecyclerViewByID(team);
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

    private void setRecyclerViewByID(String team) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference teacherCollection = db.collection("FitnessTrackTeacher");
        DocumentReference coachDocument = teacherCollection.document(coach.getCoachId());
        coachDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        getStudentsInGroup(team);

                    } else {
                        // Koç belgesi bulunamadı
                    }
                } else {
                    // Firestore sorgusu başarısız oldu
                }
            }
        });
    }
    private void getStudentsInGroup(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentCollection = db.collection("FitnessTrackStudent");
        Query studentQuery = studentCollection.whereEqualTo("teacherId", coach.getCoachId()).whereEqualTo("groupIds", groupId);

        studentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Student> studentList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            // Öğrenci verilerine erişerek istediğiniz işlemleri yapabilirsiniz
                            String studentId = documentSnapshot.getId();
                            String studentAddres = documentSnapshot.getString("address");
                            String studentBirthday = documentSnapshot.getString("birthday");
                            String studentEmail = documentSnapshot.getString("email");
                            String studentGender = documentSnapshot.getString("gender");
                            String studentHeight = documentSnapshot.getString("height");
                            String studentPassword = documentSnapshot.getString("password");
                            String studentTeacherId = documentSnapshot.getString("teacherId");
                            String studentType = documentSnapshot.getString("type");
                            String studentUsername = documentSnapshot.getString("username");
                            String studentWeight = documentSnapshot.getString("weight");
                            String studentGroupId = documentSnapshot.getString("groupIds");
                            String studentProfileImage = documentSnapshot.getString("image");
                            String studentRegisteredDate = documentSnapshot.getString("registeredDate");

                            Student student = new Student(studentId, studentAddres,studentBirthday,
                                    studentEmail,studentGender, studentHeight,studentPassword,
                                    studentTeacherId,studentType,studentUsername,studentWeight,
                                    studentGroupId, studentProfileImage, studentRegisteredDate);
                            studentList.add(student);
                        }
                        // RecyclerViewAdapter'ı oluşturun ve verileri RecyclerView'e atayın

                        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(CoachMemberlistActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, studentList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                TextView text1 = view.findViewById(android.R.id.text1);
                                TextView text2 = view.findViewById(android.R.id.text2);

                                Student student = studentList.get(position);
                                text1.setText(student.getUsername());
                                text2.setText(student.getEmail());

                                return view;
                            }
                        };

                        recyclerView.setAdapter(adapter);
                    } else {
                        // Grup boş veya öğrenci bulunamadı
                    }
                } else {
                    // Firestore sorgusu başarısız oldu
                }
            }
        });
    }

    public void backToDashboard(View v) {
        Intent intent = new Intent(this, CoachDashboardActivity.class);
        intent.putExtra("Coach", coach);
        finish();
        startActivity(intent);
    }

}