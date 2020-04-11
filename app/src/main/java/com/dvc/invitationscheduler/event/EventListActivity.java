package com.dvc.invitationscheduler.event;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private ImageButton fbtn_add;
    private ArrayAdapter<String> listAdapter;
    private ListView event_list;
    private ArrayList<String> event_name = new ArrayList<String>();
    private ArrayList<String> keys = new ArrayList<String>();
    private DatabaseReference myEventDetailRef= FirebaseDatabase.getInstance().getReference("Event Detail").child(KeyDetails.getUserKey());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        this.getSupportActionBar().setTitle("Event List");
        event_list = findViewById(R.id.event_list);
        fbtn_add = findViewById(R.id.fbtn_add);
        setListeners();
        initViews();
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
                Intent intent = new Intent(EventListActivity.this, AboutActivity.class);
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
                                Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
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
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                event_name);
        event_list.setAdapter(listAdapter);
        event_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        event_list.setClickable(false);
        event_list.setEmptyView(findViewById(R.id.empty_view));
        event_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        KeyDetails.setEventKey(keys.get(position));
                        startEventCountdown();
                    }
                });
    }

    private void setListeners(){
        myEventDetailRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventDetail detail = dataSnapshot.getValue(EventDetail.class);
                listAdapter.add(detail.event_name);
                keys.add(dataSnapshot.getKey());
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
        fbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyDetails.setEventEditable(false);
                startEventDetail();
            }
        });
    }

    private void startEventCountdown() {
        Intent intent = new Intent(EventListActivity.this, EventCountdownActivity.class);
        startActivity(intent);
        finish();
    }

    private void startEventDetail(){
        Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
        startActivity(intent);
        finish();
    }
}
