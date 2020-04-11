package com.dvc.invitationscheduler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private TextView tv_dhruvit, tv_vaidik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tv_dhruvit = findViewById(R.id.tv_dhruvit);
        tv_vaidik = findViewById(R.id.tv_vaidik);

        tv_dhruvit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:maniyadhruvit@gmail.com");
                final Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(uri);
                startActivity(intent);
            }
        });
        tv_vaidik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:vaidikpanchal2@gmail.com");
                final Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(uri);
                startActivity(intent);
            }
        });

    }
}
