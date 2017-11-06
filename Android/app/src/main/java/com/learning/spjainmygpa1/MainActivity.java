package com.learning.spjainmygpa1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.SignInButton;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.OptionalPendingResult;

import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Status;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    JSONArray cohortJSONArray;
    JSONArray batchJSONArray;
    Spinner cohort_spin;

    Spinner batch_spin;
    List<String> cohort_array, batch_array;
    Activity cur_activity;
    Context cur_context;
    RequestQueue requestQueue;
    Button submitOnce;
    Button signIn;
    studentInfo std1;
    String fetchcohort = "http://spjmygpa.magicofflight.com/getCohort.php";
    String fetchbatch = "http://spjmygpa.magicofflight.com/getBatch.php";
    String cohort_sel;
    private GoogleApiClient mGoogleApiClient;


    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestEmail()

                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        cur_context = this;
        cur_activity = this;
        cohort_array = new ArrayList<String>();
        batch_array = new ArrayList<String>();
        cohort_spin = (Spinner) findViewById(R.id.spin_cohort);
        submitOnce = (Button) findViewById(R.id.submit_once);
        signIn = (Button) findViewById(R.id.exist_user);
        batch_spin = (Spinner) findViewById(R.id.spin_batch);
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fetchcohort, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    cohortJSONArray = response.getJSONArray("cohort");


                    cohort_array.clear();
                    for (int i = 0; i < cohortJSONArray.length(); i++) {
                        JSONObject student = cohortJSONArray.getJSONObject(i);

                        String cohort = student.getString("name");
                        int validity = student.getInt("valid");
                        if (validity == 1)
                            cohort_array.add(cohort);

                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(cur_context, android.R.layout.simple_spinner_item, cohort_array);
                    //set the view for the Drop down list
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //set the ArrayAdapter to the spinner
                    cohort_spin.setAdapter(dataAdapter);
                    //attach the listener to the spinner
                    cohort_spin.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final JSONObject params = new JSONObject();
                            try {
                                params.put("cohort_id", Integer.toString(position + 1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(fetchbatch, params, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        batchJSONArray = response.getJSONArray("batchlist");

                                        batch_array.clear();
                                        for (int i = 0; i < batchJSONArray.length(); i++) {


                                            JSONObject student = batchJSONArray.getJSONObject(i);

                                            String batch = student.getString("name");
                                            int validity = student.getInt("valid");
                                            if (validity == 1)
                                                batch_array.add(batch);

                                        }

                                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(cur_context, android.R.layout.simple_spinner_item, batch_array);
                                        //set the view for the Drop down list
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        //set the ArrayAdapter to the spinner
                                        batch_spin.setAdapter(dataAdapter);

                                    } catch (JSONException e) {
                                        System.out.println("ww2");
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("weeha");
                                    System.out.append(error.getMessage());

                                }
                            });
                            requestQueue.add(jsonObjectRequest1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } catch (
                        JSONException e
                        )

                {

                    e.printStackTrace();
                }

            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.append(error.getMessage());

            }
        }

        );
        requestQueue.add(jsonObjectRequest);

        submitOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, NewUserActivity.class);
                myIntent.putExtra("key", "1"); //Optional parameters //modify pass user info, grade_cat
                MainActivity.this.startActivity(myIntent);

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CalculatorActivity.class);
                myIntent.putExtra("key", "1"); //Optional parameters
                MainActivity.this.startActivity(myIntent);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {

        EditText mStatusTextView = (EditText) findViewById(R.id.name_accnt);
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(acct.getDisplayName());

            updateUI(true);
        } else {

            updateUI(false);
        }
    }
    private void updateUI(boolean signedIn) {// lot more to copy for this

        if (signedIn) {

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);



        } else {





            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);



        }

    }


}
