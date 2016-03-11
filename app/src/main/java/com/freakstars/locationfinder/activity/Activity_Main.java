package com.freakstars.locationfinder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.app.MyApplication;
import com.freakstars.locationfinder.helper.MyPreferenceManager;

public class Activity_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }
        setContentView(R.layout.activity_activity__main);
        Toast.makeText(getApplicationContext(),""+MyApplication.getInstance().getPrefManager().getUser().getName(),Toast.LENGTH_SHORT).show();

        Button logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApplication.getInstance().logoutfromapp();
            }
        });
    }
    private void launchLoginActivity() {
        Intent intent = new Intent(getApplicationContext(),Activity_Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
