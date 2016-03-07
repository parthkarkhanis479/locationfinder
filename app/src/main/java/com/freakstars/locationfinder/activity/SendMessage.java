package com.freakstars.locationfinder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.app.Config;
import com.freakstars.locationfinder.app.EndPoints;
import com.freakstars.locationfinder.app.MyApplication;
import com.freakstars.locationfinder.gcm.NotificationUtils;
import com.freakstars.locationfinder.model.Message;

import java.util.HashMap;
import java.util.Map;

public class SendMessage extends AppCompatActivity {
EditText receiver,msg;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String TAG=SendMessage.class.getSimpleName();
    String m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        receiver=(EditText)findViewById(R.id.receiver);
        msg=(EditText)findViewById(R.id.msg);
        Button button=(Button)findViewById(R.id.messagesend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               m=msg.getText().toString();
                if (TextUtils.isEmpty(m)) {
                    Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                String id=receiver.getText().toString();
                String endPoint = EndPoints.SEND_SINGLE_USER.replace("_ID_",id);

                Log.e(TAG, "endpoint: " + endPoint);

                msg.setText("");

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        endPoint, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "response: " + response);

                        /*try {
                            JSONObject obj = new JSONObject(response);

                            // check for error
                            if (obj.getBoolean("error") == false) {
                                JSONObject commentObj = obj.getJSONObject("message");

                                String commentId = commentObj.getString("message_id");
                                String commentText = commentObj.getString("message");
                                String createdAt = commentObj.getString("created_at");

                                JSONObject userObj = obj.getJSONObject("user");
                                String userId = userObj.getString("user_id");
                                String userName = userObj.getString("name");
                                User user = new User(userId, userName, null);

                                Message message = new Message();
                                message.setId(commentId);
                                message.setMessage(commentText);
                                message.setCreatedAt(createdAt);
                                message.setUser(user);





                            } else {
                                Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/
                        Toast.makeText(getApplicationContext(),"Message sent successfully",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                        Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        //inputMessage.setText(message);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
                        params.put("message", m);

                        Log.e(TAG, "Params: " + params.toString());

                        return params;
                    };
                };


                // disabling retry policy so that it won't make
                // multiple http calls
                int socketTimeout = 0;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                strReq.setRetryPolicy(policy);

                //Adding request to request queue
                MyApplication.getInstance().addToRequestQueue(strReq);
            }
        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };
    }
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            Message message = (Message) intent.getSerializableExtra("message");
            Toast.makeText(getApplicationContext(), "New push: " + message.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
