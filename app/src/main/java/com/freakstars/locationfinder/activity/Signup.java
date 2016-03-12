package com.freakstars.locationfinder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freakstars.locationfinder.R;
import com.freakstars.locationfinder.app.EndPoints;
import com.freakstars.locationfinder.app.MyApplication;
import com.freakstars.locationfinder.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private String TAG = Signup.class.getSimpleName();
    private EditText inputName,inputPassword,inputEmail,inputPhoneNo;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inputName=(EditText)findViewById(R.id.input_name);
        inputEmail=(EditText)findViewById(R.id.input_email);
        inputPhoneNo=(EditText)findViewById(R.id.input_phoneno);
        inputPassword=(EditText)findViewById(R.id.input_password);
        Button button=(Button)findViewById(R.id.signup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }
    public void signup()
    {
        final String name=inputName.getText().toString();
        final String email=inputEmail.getText().toString();
        final String phoneno=inputPhoneNo.getText().toString();
        final String password=inputPassword.getText().toString();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.SIGNUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag

                    if (obj.getString("error").compareTo("false")==0) {
                       Toast.makeText(getApplicationContext(),"You have registered successfully",Toast.LENGTH_SHORT).show();

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name",name);
                params.put("phone_no",phoneno);
                params.put("email", email);
                params.put("password", password);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
