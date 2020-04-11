package com.dvc.invitationscheduler.event;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.SEND_SMS;

public class EventCountdownActivity extends AppCompatActivity {

    private TextView txtDay, txtHour, txtMinute, txtSecond, eventName, guestNumber, invitation_number, invitation_not_number;
    private EditText eventPlace;
    private Button btn_glist, btn_send;
    private Handler handler;
    private Runnable runnable;
    private EventDetail eventDetail;
    private ArrayList<GuestDetail> guestList = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    public final int RequestPermissionCode  = 1 ;
    private Calendar calendar;
    private int count = 0;
    private DatabaseReference myEventDetailRef= FirebaseDatabase.getInstance().getReference("Event Detail")
            .child(KeyDetails.getUserKey()).child(KeyDetails.getEventKey()),
            myGuestRef = FirebaseDatabase.getInstance().getReference("Guest Detail")
            .child(KeyDetails.getEventKey());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_contdown);
        initViews();
        addData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        MenuItem item1 = menu.findItem(R.id.action_edit);
        item.setVisible(true);
        item1.setVisible(true);
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
                                myEventDetailRef.removeValue();
                                myGuestRef.removeValue();
                                onBackPressed();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
            case R.id.action_edit:
                KeyDetails.setEventEditable(true);
                startEventDetailActivity();
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
                Intent intent = new Intent(EventCountdownActivity.this, AboutActivity.class);
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
                                Intent intent = new Intent(EventCountdownActivity.this, LoginActivity.class);
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

        txtDay = findViewById(R.id.txtDay);
        txtHour = findViewById(R.id.txtHour);
        txtMinute = findViewById(R.id.txtMinute);
        txtSecond = findViewById(R.id.txtSecond);
        eventName = findViewById(R.id.eventName);
        guestNumber = findViewById(R.id.guestNumber);
        invitation_number = findViewById(R.id.invitationNumber);
        eventPlace = findViewById(R.id.eventPlace);
        invitation_not_number = findViewById(R.id.invitationNotNumber);
        btn_glist = findViewById(R.id.btn_glist);
        btn_send = findViewById(R.id.btn_send);
        calendar = Calendar.getInstance();
    }

    private void addData() {
        myEventDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventDetail = dataSnapshot.getValue(EventDetail.class);
                if(eventDetail != null)
                    setListeners();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListeners() {
        btn_glist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGuestList();
                    }
                }
        );
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventDetail.getGuestNumber() != 0)
                    addGuestData();
                else
                    new CustomToast().Show_Toast(EventCountdownActivity.this, getWindow().getDecorView().getRootView(),
                            "Please add guest");
            }
        });
        eventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q="+eventDetail.getPlace());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        String myFormat = "dd-MM-yy hh:mm aa";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date date = new Date();
        try {
            assert eventDetail != null;
            String time = eventDetail.getEvent_date() + " " + eventDetail.getEvent_time();
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        calendar.setTime(date);
        if(calendar.before(Calendar.getInstance())){
            new AlertDialog.Builder(this)
                    .setTitle("Event is finished")
                    .setMessage("Do you want to see guest list or delete this event?")
                    .setPositiveButton("Go to guest list", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGuestList();
                        }

                    })
                    .setNegativeButton("Delete event", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            KeyDetails.setGuestNumber(0);
                            KeyDetails.setInvitation_sent(0);
                            myEventDetailRef.removeValue();
                            onBackPressed();
                        }

                    })
                    .show();
        }else {
            eventName.setText(eventDetail.getEvent_name());
            KeyDetails.setGuestNumber(eventDetail.getGuestNumber());
            KeyDetails.setInvitation_sent(eventDetail.getInvitation_sent());
            guestNumber.setText(String.valueOf(eventDetail.getGuestNumber()));
            eventPlace.setText(eventDetail.getPlace());
            invitation_number.setText(String.valueOf(eventDetail.getInvitation_sent()));
            invitation_not_number.setText(String.valueOf(eventDetail.getGuestNumber()-eventDetail.getInvitation_sent()));
            handler = new Handler();
            runnable = new Runnable() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void run() {
                    handler.postDelayed(this, 1000);
                    try {
                        long time = (calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                        long days = Math.round(time / (24 * 60 * 60 * 1000));
                        time = time - (days * 24 * 60 * 60 * 1000);
                        long hours = Math.round(time / (60 * 60 * 1000));
                        time = time - (hours * 60 * 60 * 1000);
                        long minutes = Math.round(time / (60 * 1000));
                        time = time - (minutes * 60 * 1000);
                        long seconds = Math.round(time / 1000);
                        txtDay.setText("" + String.format("%02d", days));
                        txtHour.setText("" + String.format("%02d", hours));
                        txtMinute.setText(""
                                + String.format("%02d", minutes));
                        txtSecond.setText(""
                                + String.format("%02d", seconds));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(runnable, 1 * 1000);
        }
    }

    private void addGuestData(){
        myGuestRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GuestDetail guestDetail = dataSnapshot.getValue(GuestDetail.class);
                keys.add(dataSnapshot.getKey());
                if(guestDetail != null) {
                    if(!guestDetail.isMessageSent())
                        count++;
                    guestList.add(guestDetail);
                }
                if(guestList.size() == eventDetail.getGuestNumber())
                    enableRuntimePermission();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void enableRuntimePermission(){
        if (checkPermission()){
            if(count == 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.alert)
                        .setTitle("Messages sent once")
                        .setMessage("Do you want to send again?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMessages();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }else
                sendMessages();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)
                    if(count == 0){
                        new AlertDialog.Builder(this)
                                .setIcon(R.drawable.alert)
                                .setTitle("Messages sent once")
                                .setMessage("Do you want to send again?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendMessages();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    }else
                        sendMessages();
                break;

            default:
                Toast.makeText(EventCountdownActivity.this,"Allow permission to continue",Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessages(){
        for(int i = 0 ; i < guestList.size() ; i++) {
            final String phoneNo = String.valueOf(guestList.get(i).getPhoneNumber());
            final String message = guestList.get(i).getName() + ",you're invited to " + eventDetail.getEvent_name() + " on "
                    + eventDetail.getEvent_date() + " at" + eventDetail.getEvent_time() +"\nPlease come at "
                    + eventDetail.getPlace() + getString(R.string.fullstop);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            DatabaseReference messageRef = myGuestRef.child(keys.get(i)).child("messageSent");
            messageRef.setValue(true);
        }
        Toast.makeText(EventCountdownActivity.this, "Messages sent", Toast.LENGTH_LONG).show();
    }

    private void startEventDetailActivity(){
        Intent intent = new Intent(EventCountdownActivity.this,EventDetailActivity.class);
        startActivity(intent);
        finish();
    }

    private void startGuestList() {
        Intent intent = new Intent(EventCountdownActivity.this, GuestListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventCountdownActivity.this,EventListActivity.class);
        startActivity(intent);
        finish();
    }
}
