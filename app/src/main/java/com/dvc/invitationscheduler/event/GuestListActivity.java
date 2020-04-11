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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dvc.invitationscheduler.AboutActivity;
import com.dvc.invitationscheduler.R;
import com.dvc.invitationscheduler.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GuestListActivity extends AppCompatActivity {

    private ImageButton fbtn_add;
    private ArrayAdapter<String> listAdapter;
    private ListView guest_list;
    private ArrayList<String> guest_name = new ArrayList<String>();
    private ArrayList<String> keys = new ArrayList<String>();
    private DatabaseReference myGuestDetailRef= FirebaseDatabase.getInstance().getReference("Guest Detail").child(KeyDetails.getEventKey());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);
        this.getSupportActionBar().setTitle("Guest List");
        guest_list = findViewById(R.id.guest_list);
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
                                Intent intent = new Intent(GuestListActivity.this, LoginActivity.class);
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
                guest_name);
        guest_list.setAdapter(listAdapter);
        guest_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        guest_list.setClickable(false);
        guest_list.setEmptyView(findViewById(R.id.empty_view));
        guest_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        KeyDetails.setGuestKey(keys.get(position));
                        startGuestEdit();
                    }
                });
    }

    private void setListeners(){
        myGuestDetailRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GuestDetail guestDetail = (GuestDetail) dataSnapshot.getValue(GuestDetail.class);
                listAdapter.add(guestDetail.name);
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
                startGuestDetail();
            }
        });
    }

    private void startGuestEdit() {
        Intent intent = new Intent(GuestListActivity.this, GuestViewActivity.class);
        startActivity(intent);
        finish();
    }

    private void startGuestDetail(){
        Intent intent = new Intent(GuestListActivity.this, GuestDetailActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(GuestListActivity.this, EventCountdownActivity.class);
        startActivity(intent);
        finish();
    }
}