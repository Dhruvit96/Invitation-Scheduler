package com.dvc.invitationscheduler.event;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.CustomToast;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

public class EventDetailActivity extends AppCompatActivity {

    private Button btn_add;
    private EditText et_ename,et_edate,et_etime,et_eplace;
    private TimePickerDialog time;
    private EventDetail eventDetail;
    private DatabaseReference myEventDetailRef= FirebaseDatabase.getInstance().getReference("Event Detail").child(KeyDetails.getUserKey());
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        this.getSupportActionBar().setTitle("Event details");
        initViews();
        if(KeyDetails.isEventEditable()){
            myEventDetailRef.child(KeyDetails.getEventKey()).addValueEventListener(new ValueEventListener() {
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
        }else{
            setListeners();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
                Intent intent = new Intent(EventDetailActivity.this, AboutActivity.class);
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
                                Intent intent = new Intent(EventDetailActivity.this, LoginActivity.class);
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
        btn_add = findViewById(R.id.btn_add);
        et_edate = findViewById(R.id.et_edate);
        et_ename = findViewById(R.id.et_ename);
        et_etime = findViewById(R.id.et_etime);
        et_eplace = findViewById(R.id.et_eplace);
    }

    private void setListeners() {
        String default_time, default_date;
        if(eventDetail != null){
            default_time = eventDetail.getEvent_time();
            default_date = eventDetail.getEvent_date();
            et_ename.setText(eventDetail.getEvent_name());
            btn_add.setText("Update");
            Date date = new Date();
            try {
                assert eventDetail != null;
                String time = eventDetail.getEvent_date() + " " + eventDetail.getEvent_time();
                date = new SimpleDateFormat("dd-MM-yy hh:mm aa").parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            myCalendar.setTime(date);
            String place = eventDetail.getPlace();
            et_eplace.setText(place);
        }else{
            default_time = new SimpleDateFormat("hh:mm aa").format(Calendar.getInstance().getTime());
            default_date = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
        }
        et_edate.setText(default_date);
        et_etime.setText(default_time);
        et_edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EventDetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_etime.setOnClickListener(new View.OnClickListener() {
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            @Override
            public void onClick(View v) {
                time = new TimePickerDialog(EventDetailActivity.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                        updateTimeLabel();
                    }
                }, hour, minute, false);//Yes 24 hour time);
                time.setTitle("Add Time");
                time.show();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBtn();
                validateDetail();
            }
        });
    }

    private void validateDetail() {
        final String getEventName = et_ename.getText().toString();
        final String getPlace = et_eplace.getText().toString();
        if(getEventName.equals("") || getEventName.length() == 0) {
            new CustomToast().Show_Toast(EventDetailActivity.this, getWindow().getDecorView().getRootView(),
                    "Enter event name.");
            enableBtn();
        }else if(myCalendar.before(Calendar.getInstance())){
            new CustomToast().Show_Toast(EventDetailActivity.this, getWindow().getDecorView().getRootView(),
                    "Event Date should be in future.");
            enableBtn();
        }else if(getPlace.equals("") || getPlace.length() == 0){
            new CustomToast().Show_Toast(EventDetailActivity.this, getWindow().getDecorView().getRootView(),
                    "Enter event location.");
            enableBtn();
        }else
            addToDatabase(getEventName,getPlace);
    }

    private void addToDatabase(String getEventName,String getPlace) {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setEvent_name(getEventName);
        eventDetail.setPlace(getPlace);
        String dayFormat = "dd-MM-yy";
        String timeFormat =" hh:mm aa";
        eventDetail.setEvent_date(new SimpleDateFormat(dayFormat).format(myCalendar.getTime()));
        eventDetail.setEvent_time(new SimpleDateFormat(timeFormat).format(myCalendar.getTime()));
        if(KeyDetails.isEventEditable()){
            myEventDetailRef.child(KeyDetails.getEventKey()).setValue(eventDetail);
            KeyDetails.setEventEditable(false);
            Intent intent = new Intent(EventDetailActivity.this, EventCountdownActivity.class);
            startActivity(intent);
            finish();
        }else{
            myEventDetailRef.push().setValue(eventDetail);
            Intent intent = new Intent(EventDetailActivity.this, EventListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void disableBtn(){
        et_etime.setEnabled(false);
        et_ename.setEnabled(false);
        et_edate.setEnabled(false);
        et_eplace.setEnabled(false);
    }

    private void enableBtn(){
        et_etime.setEnabled(true);
        et_ename.setEnabled(true);
        et_edate.setEnabled(true);
        et_eplace.setEnabled(true);
    }

    private void updateDateLabel() {
        String myFormat = "dd-MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_edate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel(){
        String myFormat = "hh:mm aa"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_etime.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        if(KeyDetails.isEventEditable()){
            KeyDetails.setEventEditable(false);
            Intent intent = new Intent(EventDetailActivity.this, EventCountdownActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(EventDetailActivity.this,EventListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
