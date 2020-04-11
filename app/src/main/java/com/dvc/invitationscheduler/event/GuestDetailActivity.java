package com.dvc.invitationscheduler.event;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class GuestDetailActivity extends AppCompatActivity {


    private DatabaseReference myEventDetailRef= FirebaseDatabase.getInstance().getReference("Event Detail")
            .child(KeyDetails.getUserKey()).child(KeyDetails.getEventKey()),
            myGuestDetailRef= FirebaseDatabase.getInstance().getReference("Guest Detail").child(KeyDetails.getEventKey());
    private Button btn_sent, btn_not_sent, btn_add;
    private EditText et_guestname, et_email, et_phonenumber, et_address;
    private int guestNumber = KeyDetails.getGuestNumber(), invitation_sent = KeyDetails.getInvitation_sent();
    private boolean sent = false;
    private final int PICK_CONTACT = 55;
    public final int RequestPermissionCode  = 1 ;
    private GuestDetail guestDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_detail);
        this.getSupportActionBar().setTitle("Guest details");
        initViews();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        MenuItem item = menu.findItem(R.id.action_import);
        item.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_import:
                enableRuntimePermission();
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
                Intent intent = new Intent(GuestDetailActivity.this, AboutActivity.class);
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
                                Intent intent = new Intent(GuestDetailActivity.this,LoginActivity.class);
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

    private void initViews() {
       btn_sent = findViewById(R.id.btn_sent);
       btn_not_sent = findViewById(R.id.btn_not_sent);
       btn_add = findViewById(R.id.btn_add);
       et_address = findViewById(R.id.et_address);
       et_email = findViewById(R.id.et_email);
       et_guestname = findViewById(R.id.et_guestname);
       et_phonenumber = findViewById(R.id.et_phonenumber);
       guestDetail = new GuestDetail();
    }
    
    private void setListeners() {
        btn_add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(et_guestname.getText().length() == 0 || et_guestname.getText().equals("")){
                            new CustomToast().Show_Toast(GuestDetailActivity.this, getWindow().getDecorView().getRootView(),
                                    "Enter guest name.");
                        }else{
                            guestDetail.setAddress(et_address.getText().toString());
                            guestDetail.setEmail(et_email.getText().toString());
                            guestDetail.setName(et_guestname.getText().toString());
                            guestDetail.setPhoneNumber(et_phonenumber.getText().toString());
                            guestDetail.setSent(sent);
                            guestDetail.setMessageSent(false);
                            String key = myGuestDetailRef.push().getKey();
                            myGuestDetailRef.child(key).setValue(guestDetail);
                            myEventDetailRef.child("guestNumber").setValue(++guestNumber);
                            if(sent) {
                                myEventDetailRef.child("invitation_sent").setValue(++invitation_sent);
                            }
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
    }

    public void importContanct(){
        try {
            Uri uri = Uri.parse("content://contacts");
            Intent intent = new Intent(Intent.ACTION_PICK, uri);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableRuntimePermission(){
        if (checkPermission()){
            importContanct();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, RequestPermissionCode);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)
                    importContanct();
                break;

            default:
                Toast.makeText(GuestDetailActivity.this,"Allow permission to continue",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_CONTACT:
                    Uri contactData = data.getData();
                    Cursor contact = getContentResolver().query(contactData, null, null, null, null);
                    contact.moveToFirst();
                    String number = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contactName = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    et_phonenumber.setText(number);
                    et_guestname.setText(contactName);
                    break;
            }
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(GuestDetailActivity.this, GuestListActivity.class);
        startActivity(intent);
        finish();
    }
}
