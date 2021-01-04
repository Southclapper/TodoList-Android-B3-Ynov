package com.ynov.todolist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ynov.todolist.models.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView emailText;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String UserOnlineId;
    private DatabaseReference reference;
    private String key = "";
    private String task;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Database connect and reference to model
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        UserOnlineId = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Task").child(UserOnlineId);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        emailText = (TextView) findViewById(R.id.emailText);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        UserOnlineId = mUser.getEmail();
        mUser.sendEmailVerification();
        emailText.setText(UserOnlineId);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void addTask() {
        AlertDialog.Builder createTaskAlert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView =  inflater.inflate(R.layout.input_task, null);

        createTaskAlert.setView(mView);

        AlertDialog dialog = createTaskAlert.create();
        dialog.setCancelable(true);

        final EditText task = mView.findViewById(R.id.title_task);
        final EditText description = mView.findViewById(R.id.description_task);
        Button saveTask = mView.findViewById(R.id.add_task_button);
        Button cancel = mView.findViewById(R.id.cancel_task_button);

        cancel.setOnClickListener(v -> dialog.dismiss());
        saveTask.setOnClickListener(v -> {
            String mTask = task.getText().toString().trim();
            String mDesc = description.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());

            if(TextUtils.isEmpty(mTask)) {
                task.setError("Required Title of task");
                return;
            }
            if (TextUtils.isEmpty(mDesc)) {
                description.setError("Required description of task");
                return;
            }else {
                Model model = new Model(mTask, mDesc, id, date);
                reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Task added successfuly !!", Toast.LENGTH_SHORT).show();
                        } e
                    }
                });
            }
        });

        dialog.show();
    }
}