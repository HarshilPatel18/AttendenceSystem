package com.cyberia.attendencesystem.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.cyberia.attendencesystem.View.Modals.Subject;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewStatsActivity extends AppCompatActivity {
    private int subjectID;
    Spinner spinner, spinnerSubject;
    RadioGroup radioGroup;
    private String className,subjectName,subCode;
    ProgressDialog progressDialog;
    List<String> subList;
    List<Subject> subjectList;
    ArrayAdapter<Subject> subAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

       if (!SharedPrefManager.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "Session Time Out !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ViewStatsActivity.this, LoginActivity.class));
        }

        TextView textViewTitle = findViewById(R.id.textViewTitleBar);
        textViewTitle.setText("View Report");

        //back button
        ImageView backButton = findViewById(R.id.imageBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });//back button

        progressDialog=new ProgressDialog(this);//??
        progressDialog.setCancelable(false);
        radioGroup=findViewById(R.id.radioGroup);

        CardView cardViewSaveButton=findViewById(R.id.cardviewSaveButton);
        cardViewSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(className=="No Class Found")
                {
                    Toast.makeText(getApplicationContext(),"Please, Select a Class !! ",Toast.LENGTH_SHORT).show();
                }
                else if(subjectName=="No Subjects Found"){
                    Toast.makeText(getApplicationContext(),"Please, Select a Subject !! ",Toast.LENGTH_SHORT).show();
                }
                else if(subjectID==0)
                {
                    Toast.makeText(getApplicationContext(),"Please, Select a Subject !!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent=new Intent(ViewStatsActivity.this,statsActivity.class);
                    intent.putExtra("code",subCode);
                    intent.putExtra("class",className);
                    intent.putExtra("subject",subjectName);
                    startActivity(intent);

                }

            }
        });

        //Spinner
        spinner=findViewById(R.id.spinnerClassName);
        spinnerSubject=findViewById(R.id.spinnerSubjectName);

        getClassList();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
                className=adapterView.getItemAtPosition(i).toString();
                getSubjects(className);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(spinnerSubject.getSelectedItem() instanceof String)
                {
                    subjectID=0;
                }
                else
                {
                    Subject subject=(Subject) spinnerSubject.getSelectedItem();
                    subjectID=subject.getId();
                    subCode=subject.getCode();
                    subjectName=subject.getName();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });//Spinner
    }



    public void getClassList()
    {
        progressDialog.setMessage("Fetching Classes...");
        progressDialog.show();
        final List<String> classList=new ArrayList<String>();
        StringRequest classRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_ALLCLASSES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {

                                JSONArray jsonArray=jsonObject.getJSONArray("data");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonClass = jsonArray.getJSONObject(i);

                                    classList.add(jsonClass.getString("name"));

                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, classList);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setPrompt("Select Class");

                                spinner.setAdapter(dataAdapter);

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        progressDialog.dismiss();
                        classList.add("No Class Found");
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, classList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);


                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
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

        classRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(classRequest);
    }

    public void getSubjects(String className){

        progressDialog.setMessage("Fetching Subjects...");
        progressDialog.show();
        subList=new ArrayList<String>();
        subjectList=new ArrayList<Subject>();
        StringRequest subRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_SUBJECT_BYCLASS+className.trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {

                                JSONArray jsonArray=jsonObject.getJSONArray("data");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonSub = jsonArray.getJSONObject(i);
                                    //Toast.makeText(getApplicationContext(),String.valueOf(jsonSub.getInt("id")),Toast.LENGTH_SHORT).show();
                                    subjectList.add(new Subject(jsonSub.getInt("id"),jsonSub.getString("name"),jsonSub.getString("code"),jsonSub.getString("class")));

                                }


                                subAdapter=new ArrayAdapter<Subject>(getApplicationContext(), android.R.layout.simple_spinner_item, subjectList);
                                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSubject.setPrompt("Select Subject");

                                spinnerSubject.setAdapter(subAdapter);

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        subList.add("No Subjects Found");
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, subList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(dataAdapter);

                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
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

        subRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(subRequest);
    }




}
