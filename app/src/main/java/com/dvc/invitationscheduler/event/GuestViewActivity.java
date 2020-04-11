package com.dvc.invitationscheduler.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class GuestViewActivity extends AppCompatActivity {
    private DatabaseReference myEventDetailRef= FirebaseDatabase.getInstance().getReference("Event Detail")
            .child(KeyDetails.getUserKey()).child(KeyDetails.getEventKey()),
            myGuestDetailRef= FirebaseDatabase.getInstance().getReference("Guest Detail").child(KeyDetails.getEventKey()).child(KeyDetails.getGuestKey());
    private Button btn_sent, btn_not_sent, btn_add;
    private EditText et_guestname, et_email, et_phonenumber, et_address;
    private ImageButton btn_call, btn_mail , btn_map;
    private int guestNumber = KeyDetails.getGuestNumber(), invitation_sent = KeyDetails.getInvitation_sent();
    private boolean sent = false;
    private GuestDetail guestDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_detail);
        this.getSupportActionBar().setTitle("Guest details");
        initViews();
        addData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        item.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.alert)
                        .setTitle("Deleting Event")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myGuestDetailRef.removeValue();
                                if(guestDetail.isSent())
                                    myEventDetailRef.child("invitation_sent").setValue(--invitation_sent);
                                myEventDetailRef.child("guestNumber").setValue(--guestNumber);
                                onBackPressed();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
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
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.alert)
                        .setTitle("Signing out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(GuestViewActivity.this,  LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addData(){
        myGuestDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guestDetail = dataSnapshot.getValue(GuestDetail.class);
                if(guestDetail != null) {
                    if(guestDetail.isSent()){
                        btn_not_sent.setBackground(getResources().getDrawable(R.drawable.button_selector));
                        btn_sent.setBackground(getResources().getDrawable(R.drawable.button_selector_s));
                        sent = true;
                    }
                    setListeners();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        btn_sent = findViewById(R.id.btn_sent);
        btn_not_sent = findViewById(R.id.btn_not_sent);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setText("Update");
        et_address = findViewById(R.id.et_address);
        et_email = findViewById(R.id.et_email);
        et_guestname = findViewById(R.id.et_guestname);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        btn_call = findViewById(R.id.btn_call);
        btn_call.setVisibility(View.VISIBLE);
        btn_mail = findViewById(R.id.btn_mail);
        btn_mail.setVisibility(View.VISIBLE);
        btn_map = findViewById(R.id.btn_map);
        btn_map.setVisibility(View.VISIBLE);
        guestDetail = new GuestDetail();
    }

    private void setListeners() {
        et_address.setText(guestDetail.getAddress());
        et_email.setText(guestDetail.getEmail());
        et_guestname.setText(guestDetail.getName());
        et_phonenumber.setText(guestDetail.getPhoneNumber());
        btn_add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(et_guestname.getText().length() == 0 || et_guestname.getText().equals("")){
                            new CustomToast().Show_Toast(GuestViewActivity.this, getWindow().getDecorView().getRootView(),
                                    "Enter guest name.");
                        }else{
                            if(sent == true && sent != guestDetail.isSent())
                                myEventDetailRef.child("invitation_sent").setValue(++invitation_sent);
                            else if(sent == false && sent != guestDetail.isSent())
                                myEventDetailRef.child("invitation_sent").setValue(--invitation_sent);
                            guestDetail.setAddress(et_address.getText().toString());
                            guestDetail.setEmail(et_email.getText().toString());
                            guestDetail.setName(et_guestname.getText().toString());
                            guestDetail.setPhoneNumber(et_phonenumber.getText().toString());
                            guestDetail.setSent(sent);
                            myGuestDetailRef.setValue(guestDetail);
                            onBackPressed();
                        }
                    }
                }
        );
        btn_sent.setOnClickListener(

                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        btn_not_sent.setBackground(getResources().getDrawable(R.drawable.button_selector));
                        btn_sent.setBackground(getResources().getDrawable(R.drawable.button_selector_s));
                        sent = true;
                    }
                }
        );

        btn_not_sent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_sent.setBackground(getResources().getDrawable(R.drawable.button_selector));
                        btn_not_sent.setBackground(getResources().getDrawable(R.drawable.button_selector_s));
                        sent = false;
                    }
                }
        );

        btn_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = et_email.getText().toString();
                Uri uri = Uri.parse("mailto:" + mail);
                final Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(uri);
                startActivity(intent);
            }
        });
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String number = et_phonenumber.getText().toString();
                Uri uri = Uri.parse("tel:" + number);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                try {
                    startActivity(intent);
                }catch (SecurityException s){
                    Toast.makeText(GuestViewActivity.this, s.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q="+guestDetail.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(GuestViewActivity.this, GuestListActivity.class);
        startActivity(intent);
        finish();
    }
}
