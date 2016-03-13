package com.freakstars.locationfinder.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.activity.Activity_Main;
import com.freakstars.locationfinder.app.Config;
import com.freakstars.locationfinder.gcm.NotificationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public Activity_Main activity_main;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_main=(Activity_Main)getActivity();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(activity_main.getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();
                    activity_main.subscribeToGlobalTopic();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(activity_main.getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(activity_main.getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();

                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Inflate the layout for this fragment
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(activity_main.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(activity_main.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        LocalBroadcastManager.getInstance(activity_main.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SENT_TOKEN_TO_SERVER));
        NotificationUtils.clearNotifications();
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(activity_main.getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
