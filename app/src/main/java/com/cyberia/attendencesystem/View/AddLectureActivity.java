package com.cyberia.attendencesystem.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.cyberia.attendencesystem.View.Modals.Lecture;
import com.cyberia.attendencesystem.View.Modals.Student;
import com.cyberia.attendencesystem.View.Modals.Subject;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddLectureActivity extends AppCompatActivity {

    private int subjectID, mYear, mMonth, mDay;
    Spinner spinner, spinnerSubject;
    EditText textViewTime,textViewDate;
    RadioGroup radioGroup;
    private String startTime,endTime,day,className,subjectName,subCode,date;
    ProgressDialog progressDialog;
    List<String> subList;
    List<Subject> subjectList;
    ArrayAdapter<Subject> subAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecture);

        if(!SharedPrefManager.isLoggedIn())
        {

            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
        }
        TextView textViewTitle=findViewById(R.id.textViewTitleBar);
        TextView textViewSaveButton=findViewById(R.id.textViewSaveButton);
        ConstraintLayout constraintLayout=findViewById(R.id.consSetDate);

        if(getIntent().getBooleanExtra("isPrev",true)){
            textViewTitle.setText("Previous Lecture");
            textViewSaveButton.setText("Search");
        }
        else {
            textViewTitle.setText("Add Lecture");
            constraintLayout.setVisibility(View.GONE);
        }

        ImageView backButton = findViewById(R.id.imageBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        progressDialog=new ProgressDialog(this);
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
                    else if(startTime==null)
                    {
                        Toast.makeText(getApplicationContext(),"Please, Set Start Time !! ",Toast.LENGTH_SHORT).show();
                    }
                    else if(subjectID==0)
                    {
                        Toast.makeText(getApplicationContext(),"Please, Select a Subject !!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(getIntent().getBooleanExtra("isPrev",true))
                        {
                            prevLecture();
                        }
                        else
                        {
                            addLecture();
                        }


                    }

            }
        });

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
        });


        textViewTime=findViewById(R.id.edittextSetTime);
        textViewTime.setEnabled(false);
        Button setTime=findViewById(R.id.buttonSetTime);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog=new TimePickerDialog(AddLectureActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {


                        Calendar calendar=Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                        calendar.set(Calendar.MINUTE,timePicker.getMinute());
                        calendar.clear(Calendar.SECOND);
                        SimpleDateFormat frmTime=new SimpleDateFormat("hh:mm:ss");
                        startTime=frmTime.format(calendar.getTime());
                        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour()+1);
                        endTime=frmTime.format(calendar.getTime());

                        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
                        day=dayFormat.format(calendar.getTime());

                        textViewTime.setText(startTime);
                        textViewTime.setTextColor(Color.BLACK);
                        textViewTime.setTextSize(18);
                    }
                },11,00,false);

                timePickerDialog.show();
            }
        });

        textViewDate=findViewById(R.id.edittextSetDate);
        textViewDate.setEnabled(false);
        Button setDate=findViewById(R.id.buttonSetDate);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddLectureActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar calendar=Calendar.getInstance();
                                calendar.set(Calendar.DATE,dayOfMonth);
                                calendar.set(Calendar.MONTH,monthOfYear);
                                calendar.set(Calendar.YEAR,year);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                date = sdf.format(calendar.getTime());
                                textViewDate.setText(date);
                                textViewDate.setTextColor(Color.BLACK);
                                textViewDate.setTextSize(18);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
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
                            startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
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
                            startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
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

    public void addLecture() {
        final String group;
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonA)
            group = "A";
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonB)
            group = "B";
        else{
            group = "";
        }


        progressDialog.setMessage("Adding Lecture....");
        progressDialog.show();

        StringRequest lectureRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_LECTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                Toast.makeText(getApplicationContext(),"Lecture Added Successfully !! ",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddLectureActivity.this,HomeActivity.class));
                                finish();
                                /*JSONObject jsonData= jsonObject.getJSONObject("data");
                                JSONArray jsonArray=jsonData.getJSONArray("list");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonstudent = jsonArray.getJSONObject(i);


                                }*/
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
                        Toast.makeText(getApplicationContext(),"Can't add lecture !!",Toast.LENGTH_SHORT).show();

                        /*if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddLectureActivity.this,LoginActivity.class));
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();
                        }*/


                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("class",className);
                params.put("subject_id",String.valueOf(subjectID));
                params.put("day",day.toUpperCase());
                params.put("time_from",startTime);
                params.put("time_to",endTime);
                params.put("group",group);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = "Bearer "+ SharedPrefManager.getAccessToken();
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",bearer);
                return headers;
            }



        };

        lectureRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(lectureRequest);

    }

    public void prevLecture()
    {
        final String group;
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonA)
            group = "A";
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonB)
            group = "B";
        else{
            group = "";
        }


        progressDialog.setMessage("Fetching Lecture Data ....");
        progressDialog.show();

        StringRequest studentRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_STUDENT_MARKED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {

                                final String cName;
                                if(group==null || group=="null")
                                    cName=className;

                                else
                                    cName=className + " - Group " + group;

                                Intent intent=new Intent(AddLectureActivity.this,StudentsActivity.class);
                                intent.putExtra("is_marked",true);
                                intent.putExtra("subject_id",subjectID);
                                intent.putExtra("date",date);
                                intent.putExtra("time_from",startTime);
                                intent.putExtra("time_to",endTime);
                                intent.putExtra("subject",subjectName);
                                intent.putExtra("code",subCode);
                                intent.putExtra("class",className);
                                intent.putExtra("classname",cName);
                                intent.putExtra("group",group);
                                intent.putExtra("is_prev",true);

                                startActivity(intent);


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
                       Toast.makeText(getApplicationContext(),"Can't find lecture. Check Date or Time !!",Toast.LENGTH_SHORT).show();
                        /*if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();

                        }*/

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();

                params.put("class",className);
                params.put("subject_id",String.valueOf(subjectID));
                params.put("date",date);
                params.put("time_from",startTime);
                params.put("time_to",endTime);
                params.put("group",group);


                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = "Bearer "+ SharedPrefManager.getAccessToken();
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",bearer);
                return headers;
            }

        };

        studentRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(studentRequest);
    }


}