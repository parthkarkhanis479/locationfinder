package com.freakstars.locationfinder.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.activity.Activity_Main;
import com.freakstars.locationfinder.app.Config;
import com.freakstars.locationfinder.app.EndPoints;
import com.freakstars.locationfinder.app.MyApplication;
import com.freakstars.locationfinder.gcm.NotificationUtils;
import com.freakstars.locationfinder.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Pending_Requests extends Fragment {


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public Activity_Main activity_main;
    private String TAG = Pending_Requests.class.getSimpleName();

    public Pending_Requests() {
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
                    handlePushNotification(intent);
                }
            }
        };
        fetchPendingRequests();
    }
    public void fetchPendingRequests()
    {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.PENDING_FRIEND_REQUESTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("error").compareTo("false")==0) {
                        JSONArray pendingUsersArray = obj.getJSONArray("to");
                        for (int i = 0; i < pendingUsersArray.length(); i++) {
                            JSONObject pendingUserObj = (JSONObject) pendingUsersArray.get(i);
                            User user=new User();
                            user.setId(pendingUserObj.getString("user_id"));
                            user.setName(pendingUserObj.getString("name"));
                            Toast.makeText(activity_main.getApplicationContext(),""+user.getName(),Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(activity_main.getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(activity_main.getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(activity_main.getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_id",MyApplication.getInstance().getPrefManager().getUser().getId());


                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            User message = (User)intent.getSerializableExtra("message");
            Toast.makeText(activity_main.getApplicationContext(),message.getName()+"has sent you friend request", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_pending__requests, container, false);
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
