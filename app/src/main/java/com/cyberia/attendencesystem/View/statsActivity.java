package com.cyberia.attendencesystem.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Adapter.StudentListAdapter;
import com.cyberia.attendencesystem.View.Adapter.statsAdapter;
import com.cyberia.attendencesystem.View.Modals.Student;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class statsActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    ArrayList<Student> studentArrayList;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(statsActivity.this,LoginActivity.class));
        }

        TextView textViewTitle=findViewById(R.id.textViewTitleBar);
        textViewTitle.setText(getIntent().getStringExtra("code")+" - "+getIntent().getStringExtra("subject"));


        recyclerView=findViewById(R.id.recyclerStats);
        mProgressBar=findViewById(R.id.progressBarStats);
        studentArrayList=new ArrayList<>();

        loadStats();


    }



    public void loadStats() {

        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest studentRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_ViewSTATS + String.valueOf(getIntent().getStringExtra("code")),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                JSONObject jsonData= jsonObject.getJSONObject("data");
                                JSONArray jsonArray=jsonData.getJSONArray("students");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonstudent = jsonArray.getJSONObject(i);

                                    Student mStudent= new Student(String.valueOf(jsonstudent.getInt("roll_no")),jsonstudent.getString("name"),jsonstudent.getInt("total_present"),
                                            jsonstudent.getInt("total_lectures"));
                                    studentArrayList.add(mStudent);

                                }

                                Collections.sort(studentArrayList,Student.StuRollno);
                                mProgressBar.setVisibility(View.GONE);
                                mAdapter = new statsAdapter(getApplicationContext(),studentArrayList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerView.setAdapter(mAdapter);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        mProgressBar.setVisibility(View.GONE);
                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(statsActivity.this,LoginActivity.class));
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = "Bearer "+ SharedPrefManager.getAccessToken();
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",bearer);
                return headers;
            }
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }




        };

        studentRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(studentRequest);

    }
}