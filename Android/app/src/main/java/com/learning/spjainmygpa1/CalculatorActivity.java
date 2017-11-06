package com.learning.spjainmygpa1;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oasac on 10/31/2016.
 */

public class CalculatorActivity extends AppCompatActivity  {
    public float totCredit;
    public float t_gpa;
    int totcrse;
    JSONArray studentDetailsArray;
    JSONArray gradesJSONArray;
    List<String> grade_Array;
    List<Float> cred_Array;
    List<Integer> posArray;
    final int grade_cat = 1; //read this from JOIN table of grades
    List<List<Float>> weight_array;
    List<Integer> change_flag;
    String fetchuserdata = "http://spjmygpa.magicofflight.com/getUserData.php";
    String updateuserdata = "http://spjmygpa.magicofflight.com/updateUserData.php";
    String fetchgradelist = "http://spjmygpa.magicofflight.com/getGrades.php";
    Activity cur_activity;
    Context cur_context;
    RequestQueue requestQueue;
    ListView listViewItem;
    JSONAdapter jsonAdapter;
    TextView gpa_text,cred_text;


    protected void onCreate(Bundle savedInstanceState) {
        cur_context = this;
        cur_activity = this;
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.
        grade_Array = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_main);
         gpa_text = (TextView) findViewById(R.id.gpa_text);
        cred_text = (TextView) findViewById(R.id.cred_text);
        requestQueue = Volley.newRequestQueue(this);
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

                    getUserData();



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

        Button revert= (Button)findViewById(R.id.Revert);
        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserData();
            }
        });

        Button sync = (Button)findViewById(R.id.Save);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateuserdata();
            }
        });

    }

    public void updateuserdata() {

JSONObject tempUpdateObject = new JSONObject();
        JSONArray updateUserData = new JSONArray();
        posArray=jsonAdapter.getPosArray();
        int changed;
        for(int i=0;i<totcrse;i++)
        {changed=0;
            try {
                                tempUpdateObject=studentDetailsArray.getJSONObject(i);
                changed=tempUpdateObject.getInt("change");
                if(changed==1) {
                    tempUpdateObject.put("grade_pos", posArray.get(i));
                    updateUserData.put(tempUpdateObject);
                    tempUpdateObject.put("change", 0);
                    studentDetailsArray.put(i, tempUpdateObject);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println("USER DATA "+updateUserData.toString());
        JsonArrayRequest jsonUpdateRequest= new JsonArrayRequest(Request.Method.POST, updateuserdata, updateUserData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


            }
        } ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        requestQueue.add(jsonUpdateRequest);
    }

    public void calculateGPA(int itemPosition, int curGradePosition, int prevGradePosition)
    {
if(totcrse>0) {
    float tempcred = 0;
    float totGPoint = 0;
    float tempGPoint = 0;
    float tempCurGrade = 0;
    float tempPrevGrade = 0;
    try {
        studentDetailsArray.put(itemPosition, studentDetailsArray.getJSONObject(itemPosition).put("change", 1));

    } catch (JSONException e) {
        e.printStackTrace();
    }


    tempCurGrade = weight_array.get(curGradePosition).get(0);
    tempPrevGrade = weight_array.get(prevGradePosition).get(0);
    tempcred = cred_Array.get(itemPosition);
    totGPoint = t_gpa * totCredit;
    tempGPoint = tempcred * tempPrevGrade;


    if (curGradePosition == 0) {
        totGPoint -= tempGPoint;

        totCredit -= tempcred;

    } else if (prevGradePosition == 0) {
        totCredit += tempcred;
        totGPoint = totGPoint + tempcred * tempCurGrade;


    } else {
        totGPoint = totGPoint - tempGPoint + tempcred * tempCurGrade;

    }


    t_gpa = totGPoint / totCredit;
    BigDecimal bd = new BigDecimal(t_gpa);
    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

    gpa_text.setText(bd.toString());
    cred_text.setText(Float.toString(totCredit));


}


    }
public void initialGPA()
{
    totCredit=0;
float tempGrade;
   if(totcrse>0)
   {float temp_gpa=0;
for(int i=0;i<totcrse;i++)
{
tempGrade=weight_array.get(posArray.get(i)).get(0);
    if(tempGrade!=-99) {
        temp_gpa += tempGrade * cred_Array.get(i);
        totCredit += cred_Array.get(i) ;
    }

}
       t_gpa=temp_gpa/totCredit;
       BigDecimal bd = new BigDecimal(t_gpa);
       bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

       gpa_text.setText(bd.toString());

       cred_text.setText(Float.toString(totCredit));


   }



}
    public void getUserData()

    {
        totcrse=0;
        final JSONObject params = new JSONObject();
        try {
            //to modify value
            params.put("userid", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(fetchuserdata, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    studentDetailsArray = response.getJSONArray("userdata");

                    JSONObject temp = new JSONObject();
                    totcrse=studentDetailsArray.length();
                    cred_Array=new ArrayList<>();
                    cred_Array = new ArrayList<>();
                    for (int i = 0; i <totcrse; i++) {


                        temp = studentDetailsArray.getJSONObject(i);
                            temp.put("change", 0);

                            studentDetailsArray.put(i, temp);
                        cred_Array.add((float) studentDetailsArray.getJSONObject(i).getDouble("credit"));

                        }


/* delete if not used
                        List row_studentDetails = new ArrayList<>(4);
                        row_studentDetails.add(temp_id);
                        row_studentDetails.add(i);
                        row_studentDetails.add(temp_courseid);
                        row_studentDetails.add(temp_coursename);
                        row_studentDetails.add(temp_gradepos);
*/





                    listViewItem = (ListView) findViewById(R.id.usercourselist);
                    jsonAdapter = new JSONAdapter(cur_activity, studentDetailsArray, grade_Array,0);
                    posArray=jsonAdapter.getPosArray();
                    listViewItem.setAdapter(jsonAdapter);
                    initialGPA();
                }

                 catch (JSONException e) {
                    System.out.println("ww2");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("weeha11");
                System.out.append(error.getMessage());

            }
        });

        requestQueue.add(jsonObjectRequest2);
    }

}


