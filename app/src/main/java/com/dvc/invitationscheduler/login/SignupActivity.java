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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText et_fname, et_email, et_mnum,
            et_pswd, et_cpswd;
    private TextView tv_login;
    private Button btn_signup;
    private ProgressBar progressBar;
    private boolean test;
    private FirebaseAuth firebaseAuth;
    private final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    private final String pswdRegExALL = "((?=.*[a-z])(?=.\\d)(?=.*[@#$%])(?=.*[A-Z]).{8,16})";
    private final String pswdRegExDigit = "(?=.*(\\d))";
    private final String pswdRegExSmall = "(?=.*[a-z])";
    private final String pswdRegExBig = "(?=.*[A-Z])";
    private final String pswdRegExSpecial = "(?=.*[@#$%])";
    Pattern p = Pattern.compile(regEx);
    Pattern pa = Pattern.compile(pswdRegExALL);
    Pattern pd = Pattern.compile(pswdRegExDigit);
    Pattern psm = Pattern.compile(pswdRegExSmall);
    Pattern pb = Pattern.compile(pswdRegExBig);
    Pattern ps = Pattern.compile(pswdRegExSpecial);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        et_fname = findViewById(R.id.et_fname);
        et_email = findViewById(R.id.et_email);
        et_mnum = findViewById(R.id.et_mnum);
        et_pswd = findViewById(R.id.et_pswd);
        et_cpswd = findViewById(R.id.et_cpswd);
        btn_signup = findViewById(R.id.btn_signup);
        tv_login = findViewById(R.id.tv_login);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtn();
                final String getFullName = et_fname.getText().toString();
                final String getEmailId = et_email.getText().toString();
                final String getMobileNumber = et_mnum.getText().toString();
                final String getPassword = et_pswd.getText().toString();
                final String getConfirmPassword = et_cpswd.getText().toString();
                Matcher m = p.matcher(getEmailId);
                Matcher ma = pa.matcher(getPassword);
                Matcher md = pd.matcher(getPassword);
                Matcher msm = psm.matcher(getPassword);
                Matcher mb = pb.matcher(getPassword);
                Matcher ms = ps.matcher(getPassword);
                if (getFullName.equals("") || getFullName.length() == 0
                        || getEmailId.equals("") || getEmailId.length() == 0
                        || getMobileNumber.equals("") || getMobileNumber.length() == 0
                        || getPassword.equals("") || getPassword.length() == 0
                        || getConfirmPassword.equals("")
                        || getConfirmPassword.length() == 0) {

                    new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                            "All fields are required.");
                    enableBtn();
                }else if (!m.find()) {
                    new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                            "Your Email Id is Invalid.");
                    enableBtn();
                }else if (getMobileNumber.length() != 10) {
                    new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                            "Your Mobile Number is Invalid.");
                    enableBtn();
                }else if (!ma.find()) {
                    if(getPassword.length() < 8){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Create password with atleast 8 characters.");
                        enableBtn();
                    }else if(getPassword.length() > 16){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Password can't have more than 16 characters.");
                        enableBtn();
                    }else if(!md.find()){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Password should have atleast 1 digit.");
                        enableBtn();
                    }else if(!msm.find()){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Password should have atleast 1 small letter.");
                        enableBtn();
                    }else if(!mb.find()){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Password should have atleast 1 capital letter.");
                        enableBtn();
                    }else if(!ms.find()){
                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                                "Password should have atleast 1 special character from @,#,$,%.");
                        enableBtn();
                    }
                }else if (!getConfirmPassword.equals(getPassword)) {
                    new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(),
                            "Both password doesn't match.");
                    enableBtn();
                }else{
                    Toast.makeText(SignupActivity.this, "Registering...", Toast.LENGTH_SHORT).show();
                    firebaseAuth.createUserWithEmailAndPassword(getEmailId,getPassword).
                            addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //checking if success
                                    if (task.isSuccessful()) {
                                        enterUserinfo(getFullName);
                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SignupActivity.this,"Registered successfully check your email for verification. ",
                                                            Toast.LENGTH_LONG).show();
                                                    onBackPressed();
                                                }
                                                else{
                                                    new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(), task.getException().getMessage());
                                                    enableBtn();
                                                }
                                            }
                                        });
                                    } else {
                                        new CustomToast().Show_Toast(SignupActivity.this, getWindow().getDecorView().getRootView(), task.getException().getMessage());
                                        enableBtn();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void disableBtn(){
        tv_login.setEnabled(false);
        btn_signup.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableBtn(){
        btn_signup.setEnabled(true);
        tv_login.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void enterUserinfo(String getFullName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(getFullName).build();
        user.updateProfile(profileUpdates);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
