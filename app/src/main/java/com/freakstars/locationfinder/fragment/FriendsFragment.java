package com.freakstars.locationfinder.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.activity.Activity_Main;
import com.freakstars.locationfinder.app.Config;
import com.freakstars.locationfinder.gcm.NotificationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public Activity_Main activity_main;
    private final static int CONTACT_PICKER = 1;
    public FriendsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        Button contact_pick=(Button)rootView.findViewById(R.id.contact_pick);
        contact_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent =
                        new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER);

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == activity_main.RESULT_OK) {
            // Check for the request code, we might be using multiple startActivityForReslut
            switch (requestCode) {
                case CONTACT_PICKER:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    private void contactPicked(Intent data) {
        ContentResolver cr = activity_main.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        cur.moveToFirst();
        try {
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cur = activity_main.getContentResolver().query(uri, null, null, null, null);
            cur.moveToFirst();
            // column index of the contact ID
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            // column index of the contact name
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Toast.makeText(activity_main.getApplicationContext(),name,Toast.LENGTH_SHORT).show();     //print name
            // column index of the phone number
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            if(pCur.moveToFirst()) {
                String phone = pCur.getString(
                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Toast.makeText(activity_main.getApplicationContext(),phone,Toast.LENGTH_SHORT).show();          //print no
            }
            pCur.close();
            // column index of the email
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                String email = emailCur.getString(
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Toast.makeText(activity_main.getApplicationContext(),email,Toast.LENGTH_SHORT).show();         //print data
            }
            emailCur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
