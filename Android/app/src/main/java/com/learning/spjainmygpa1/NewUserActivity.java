package com.learning.spjainmygpa1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oasac on 10/29/2016.
 */
public class NewUserActivity extends AppCompatActivity {

    List<List<Float>> weight_array;
    JSONArray courseJSONArray;
    JSONArray gradesJSONArray;
    ListView listViewItem;
    JSONAdapter jsonAdapter;
    List<String> grade_Array;

    RequestQueue requestQueue;
    String fetchcourselist = "http://spjmygpa.magicofflight.com/getCourseList.php";
    String fetchgradelist = "http://spjmygpa.magicofflight.com/getGrades.php";
    String saveuserdataurl = "http://spjmygpa.magicofflight.com/saveUserData.php";

    Activity cur_activity;
    Context cur_context;


    protected void onCreate(Bundle savedInstanceState) {
        cur_context = this;
        cur_activity = this;
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored. // modify
        grade_Array = new ArrayList<String>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser_main);

        listViewItem = (ListView) findViewById(R.id.courselistview);
        requestQueue = Volley.newRequestQueue(this);
        final int grade_cat = 1;//modify
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, fetchgradelist, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    gradesJSONArray = response.getJSONArray("gradelist");

                    grade_Array.clear();
                    JSONObject temp = new JSONObject();

                    weight_array = new ArrayList<>();

                    for (int i = 0; i < gradesJSONArray.length(); i++) {
                        temp = gradesJSONArray.getJSONObject(i);
                       /*  temp.put("spin_id", i);
                        grades.put(i, temp);
                        to remove but ref
*/
                        float temp_rwt = (float) temp.getDouble("rwt" + Integer.toString(grade_cat));
                        float temp_awt = (float) temp.getDouble("awt" + Integer.toString(grade_cat));

                        if (temp_awt != -99) {
                            List<Float> row_wt_array = new ArrayList<>(2);
                            row_wt_array.clear();
                            row_wt_array.add(temp_rwt);
                            row_wt_array.add(temp_awt);

                            weight_array.add(row_wt_array);

                            grade_Array.add(i, temp.getString("name"));
                        }
                    }




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


        final JSONObject params = new JSONObject();
        try {
            params.put("batch_id", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(fetchcourselist, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    courseJSONArray = response.getJSONArray("courselist");



                    jsonAdapter = new JSONAdapter(cur_activity, courseJSONArray, grade_Array,1);
                    listViewItem.setAdapter(jsonAdapter);


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


        requestQueue.add(jsonObjectRequest);

        // any edit or ad should be done prior









        //saving starts here


        Button submitCourse = (Button) cur_activity.findViewById(R.id.submit_crse);
        submitCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitCourse();
            }
        });
    }


    public void SubmitCourse() {


        final   JSONArray userCourse = new JSONArray();
        try {
        for(int i=0;i<courseJSONArray.length();i++){
            JSONObject params2 = new JSONObject();
            params2.put("userid", 1);
            params2.put("grade_pos", (jsonAdapter.getPosArray()).get(i));
            params2.put("course_id", courseJSONArray.getJSONObject(i).getInt("course_id"));
            userCourse.put(params2);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.POST, saveuserdataurl, userCourse, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


            }
        } ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.append(error.getMessage());

            }
        });

        requestQueue.add(jsonArrayRequest);



    }


}




