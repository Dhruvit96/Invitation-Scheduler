package com.dvc.invitationscheduler.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.event.EventListActivity;
import com.dvc.invitationscheduler.event.KeyDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_pswd;
    private Button btn_login;
    private TextView tv_fpswd, tv_signup;
    private CheckBox cb_show_pswd;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()){
            KeyDetails.setUserKey(firebaseAuth.getUid());
            Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
            startActivity(intent);
            finish();
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        et_email = (EditText) findViewById(R.id.et_email);
        et_pswd = (EditText) findViewById(R.id.et_pswd);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_fpswd = (TextView) findViewById(R.id.tv_fpswd);
        tv_signup = (TextView) findViewById(R.id.tv_signup);
        cb_show_pswd = (CheckBox) findViewById(R.id.cb_show_pswd);
    }

    private void setListeners() {
        // Set check listener over checkbox for showing and hiding password
        cb_show_pswd
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            cb_show_pswd.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            et_pswd.setInputType(InputType.TYPE_CLASS_TEXT);
                            et_pswd.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            cb_show_pswd.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            et_pswd.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            et_pswd.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableBtn();
                checkValidation();
            }
        });

        tv_fpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkValidation() {
        String getEmailId = et_email.getText().toString();
        String getPassword = et_pswd.getText().toString();

        // Check patter for email id
        String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            new CustomToast().Show_Toast(this,getWindow().getDecorView().getRootView(),
                    "Enter both credentials.");
            enableBtn();
        }
        // Check if email id is valid or not
        else if (!m.find()) {
            new CustomToast().Show_Toast(this, getWindow().getDecorView().getRootView(),
                    "Your Email Id is Invalid.");
            enableBtn();
        }
        // Else do login and do your stuff
        else{
            firebaseAuth.signInWithEmailAndPassword(getEmailId, getPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        new CustomToast().Show_Toast(LoginActivity.this, getWindow().getDecorView().getRootView(), task.getException().getMessage());
                        enableBtn();
                    }
                    else{
                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                            KeyDetails.setUserKey(firebaseAuth.getUid());
                            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                            Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            new CustomToast().Show_Toast(LoginActivity.this, getWindow().getDecorView().getRootView(), "Please verify your email");
                            enableBtn();
                        }
                    }
                }
            });
        }
    }


    private void disableBtn() {
        tv_signup.setEnabled(false);
        tv_fpswd.setEnabled(false);
        btn_login.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableBtn() {
        tv_signup.setEnabled(true);
        tv_fpswd.setEnabled(true);
        btn_login.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
