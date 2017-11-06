package com.learning.spjainmygpa1;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by oasac on 10/27/2016.
 */
public class JSONAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;

    private final JSONArray jsonArray;
    List<String> gradeArray;
    List<Integer> posSpin;
    int new_user;

    public JSONAdapter(Activity activity, JSONArray jsonArray, List<String> gradeArray,int new_user) {
        assert activity != null;
        assert jsonArray != null;
        this.gradeArray = gradeArray;
        this.jsonArray = jsonArray;
        this.activity = activity;
        this.new_user=new_user;
        //read from sql later
        posSpin = new ArrayList<>();

        if(new_user==1) {


            for (int i = 0; i < jsonArray.length(); i++) {
                posSpin.add(i, 0);

            }
        }


        {for (int i = 0; i < jsonArray.length(); i++) {

            try {
                posSpin.add(jsonArray.getJSONObject(i).getInt("grade_pos"));
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        }
    }


    @Override
    public int getCount() {

        return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {

        return jsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);

        return jsonObject.optLong("id");
    }

    public List<Integer> getPosArray() {
        return posSpin;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolderItem();
            convertView = activity.getLayoutInflater().inflate(R.layout.listview_each_item, null);

            viewHolder.textViewSubject = (TextView) convertView.findViewById(R.id.subj);
            viewHolder.textViewCred = (TextView) convertView.findViewById(R.id.cred);
            viewHolder.spinGrade = (Spinner) convertView.findViewById(R.id.spin_grade);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        JSONObject jsonObject = getItem(position); //tocheck later

        String schemaSubject = null;
        int schemaCredit = 1;
        try {
            schemaSubject = jsonObject.getString("name");
            schemaCredit = jsonObject.getInt("credit");


        } catch (JSONException e) {
            e.printStackTrace();
        }


//

        try {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, gradeArray);
            //set the view for the Drop down list
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //set the ArrayAdapter to the spinner
            viewHolder.spinGrade.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        //


        viewHolder.textViewSubject.setText(schemaSubject);
        viewHolder.textViewCred.setText(Integer.toString(schemaCredit));

        viewHolder.spinGrade.setSelection(posSpin.get(position), false);

        viewHolder.spinGrade.setOnItemSelectedListener(new spinnerListView(position));

        return convertView;
    }

    static class ViewHolderItem {
        TextView textViewSubject;
        TextView textViewCred;
        Spinner spinGrade;

    }

    private class spinnerListView implements AdapterView.OnItemSelectedListener {

        private int mSpinnerPosition;

        public spinnerListView(int spinnerPosition) {

            mSpinnerPosition = spinnerPosition;

        }

        @Override

        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            // your code here
            if(parentView.getId()==R.id.spin_grade){
            if (posSpin.get(mSpinnerPosition)!=position) {
                if(new_user==0) {

                    ((CalculatorActivity) activity).calculateGPA(mSpinnerPosition, position, posSpin.get(mSpinnerPosition));
                }
                posSpin.set(mSpinnerPosition, position);



            }}

        }


        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }
}


