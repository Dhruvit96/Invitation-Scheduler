package com.dvc.invitationscheduler.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotActivity extends AppCompatActivity {

    private EditText et_email;
    private TextView tv_submit, tv_back;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        initViews();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.allmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_exit:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.alert)
                        .setTitle("Exiting App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        et_email = (EditText) findViewById(R.id.et_email);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_back = (TextView) findViewById(R.id.tv_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setListeners(){
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtn();
                submitButtonTask();
            }
        });
    }

    private void submitButtonTask() {
        String getEmailId = et_email.getText().toString();

        // Pattern for email id validation
        String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
        Pattern p = Pattern.compile(regEx);

        // Match the pattern
        Matcher m = p.matcher(getEmailId);

        // First check if email id is not null else show error toast
        if (getEmailId.equals("") || getEmailId.length() == 0) {
            new CustomToast().Show_Toast(ForgotActivity.this, getWindow().getDecorView().getRootView(),
                    "Please enter your Email Id.");
            enableBtn();
        }
        // Check if email id is valid or not
        else if (!m.find()) {
            new CustomToast().Show_Toast(ForgotActivity.this, getWindow().getDecorView().getRootView(),
                    "Your Email Id is Invalid.");
            enableBtn();
        }
        // Else submit email id and fetch passwod or do your stuff
        else {
            firebaseAuth.getInstance().sendPasswordResetEmail(getEmailId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotActivity.this,"Reset link is sent successfully to your email",
                                        Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                            else {
                                new CustomToast().Show_Toast(ForgotActivity.this, getWindow().getDecorView().getRootView(), task.getException().getMessage());
                                enableBtn();
                            }
                        }
                    });
        }
    }

    private  void disableBtn(){
        tv_submit.setEnabled(false);
        tv_back.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableBtn(){
        tv_back.setEnabled(true);
        tv_submit.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
