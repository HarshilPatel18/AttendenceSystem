package com.cyberia.attendencesystem.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.cyberia.attendencesystem.View.Modals.Student;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    private int scheduleID, sid;
    ArrayList<Student> studentArrayList;
    ProgressBar mProgressBar;
    ProgressDialog progressDialog;
    Spinner spinnerDownload;
    String[] download = { "DOWNLOAD","EXCEL", "CSV", "PDF", "HTML", "TSV"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //Code for each activity
        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
        }
        TextView textViewTitle=findViewById(R.id.textViewTitleBar);
        textViewTitle.setText(getIntent().getStringExtra("code")+" - "+getIntent().getStringExtra("subject"));
        ImageView backButton = findViewById(R.id.imageBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView textViewClass=findViewById(R.id.textViewStats);
        textViewClass.setText(getIntent().getStringExtra("classname"));
        recyclerView=findViewById(R.id.recyclerStats);
        scheduleID=getIntent().getIntExtra("schedule_id",0);
        mProgressBar=findViewById(R.id.progressBarStats);
        spinnerDownload=findViewById(R.id.spinnerDownload);



        if(getIntent().getBooleanExtra("is_prev",true) || getIntent().getBooleanExtra("is_marked",true))
        {
            spinnerDownload.setVisibility(View.VISIBLE);
            final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,download);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDownload.setAdapter(arrayAdapter);

            spinnerDownload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getApplicationContext(),download[i],Toast.LENGTH_SHORT).show();
                    if(i!=0)
                    {
                        downloadAttendence(download[i].toLowerCase());
                    }
                    spinnerDownload.setSelection(0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else
            spinnerDownload.setVisibility(View.GONE);

        studentArrayList=new ArrayList<>();

        if(getIntent().getBooleanExtra("is_marked",true))
            getStudents();
        else
            setupStudents();

        Chip presentAllChip=findViewById(R.id.chipPresentAll);
        presentAllChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<studentArrayList.size();i++)
                {
                    studentArrayList.get(i).setPresent(true);
                }
                Collections.sort(studentArrayList,Student.StuRollno);
                mAdapter = new StudentListAdapter(getApplicationContext(),studentArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(mAdapter);

            }
        });

        Chip absentAllchip=findViewById(R.id.chipAbsentAll);
        absentAllchip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<studentArrayList.size();i++)
                {
                    studentArrayList.get(i).setPresent(false);
                }
                Collections.sort(studentArrayList,Student.StuRollno);
                mAdapter = new StudentListAdapter(getApplicationContext(),studentArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(mAdapter);

            }
        });

        Chip buttonSave=findViewById(R.id.chipSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(StudentsActivity.this);

                if(mAdapter!=null)
                {
                    final ArrayList<Student> finalList=StudentListAdapter.getStudentList();
                    Boolean flagEmpty=false;
                    for(int i=0;i<finalList.size();i++)
                    {
                        if(finalList.get(i).getPresent()==null)
                        {
                            flagEmpty=true;
                        }
                    }

                    if(flagEmpty)
                    {
                        builder.setMessage("You havn't taken every student's attendence ! Pressing Confirm will mark their attendence as Absent.");
                    }
                    else
                    {
                        builder.setMessage("Do you want to save the attendence ?");
                    }

                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if(getIntent().getBooleanExtra("is_marked",true))
                                editAttendence(finalList);
                            else
                                markAttendence(finalList);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });


                    AlertDialog alert = builder.create();
                    alert.setTitle("Attendence of "+getIntent().getStringExtra("subject"));
                    alert.show();
                }
                else{
                    builder.setMessage("Please Set attendence first");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.setTitle("Attendence of "+getIntent().getStringExtra("subject"));
                    alertDialog.show();


                }
            }
        });



    }

    @Override
    protected void onRestart() {

        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
        }
        super.onRestart();
    }


    public void setupStudents() {

        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest studentRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_STUDENT_LIST+String.valueOf(scheduleID),
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

                                    Student mStudent= new Student(String.valueOf(jsonstudent.getInt("roll_no")),jsonstudent.getString("name"),jsonstudent.getString("prn"));
                                    studentArrayList.add(mStudent);

                                }

                                Collections.sort(studentArrayList,Student.StuRollno);
                                mProgressBar.setVisibility(View.GONE);
                                mAdapter = new StudentListAdapter(getApplicationContext(),studentArrayList);
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
                            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
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

    public void markAttendence(ArrayList<Student> mStudentList)
    {

        int size=mStudentList.size();
        String students[]=new String[100];
        int i,j=0;

        for(i=0;i<size;i++)
        {
            if(mStudentList.get(i).getPresent()!=null)
            {
                if(mStudentList.get(i).getPresent())
                {
                    students[j]=mStudentList.get(i).getRollNo();
                    j++;
                }
            }

        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        final Map<String, String> postData = new HashMap<String, String>();
        postData.put("schedule_id",String.valueOf(getIntent().getIntExtra("schedule_id",0)));
        postData.put("lecture_id",String.valueOf(getIntent().getIntExtra("lecture_id",0)));
        postData.put("date",date);


        for(i = 0; i< j; i++)
        {
            postData.put("students["+i+"]",students[i]);
        }



        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Saving Attendence ......");

        progressDialog.setCancelable(false);
        progressDialog.show();



        StringRequest attendenceRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_MARK_ATTENDENCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                Toast.makeText(getApplicationContext()," Attendence Marked Successfully !!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(StudentsActivity.this,HomeActivity.class));
                                finishAffinity();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error Not working",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        if (error instanceof NetworkError) {
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

                        }

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return postData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = "Bearer "+ SharedPrefManager.getAccessToken();
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",bearer);
                return headers;
            }



        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(attendenceRequest);
    }



    public void getStudents() {



        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest studentRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_STUDENT_MARKED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                JSONObject jsonData= jsonObject.getJSONObject("data");
                                sid=jsonData.getInt("schedule_id");
                                JSONArray jsonArray=jsonData.getJSONArray("list");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonstudent = jsonArray.getJSONObject(i);

                                    Boolean present;
                                    if(jsonstudent.getInt("isPresent")==1)
                                        present=true;
                                    else
                                        present=false;

                                    Student mStudent= new Student(String.valueOf(jsonstudent.getInt("roll_no")),jsonstudent.getString("name"),present);
                                    studentArrayList.add(mStudent);

                                }

                                Collections.sort(studentArrayList,Student.StuRollno);

                                mProgressBar.setVisibility(View.GONE);
                                mAdapter = new StudentListAdapter(getApplicationContext(),studentArrayList);
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

                            Toast.makeText(getApplicationContext(), "Server Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                Map<String, String> params = new HashMap<String, String>();
                params.put("class",getIntent().getStringExtra("class"));
                params.put("subject_id",String.valueOf(getIntent().getIntExtra("subject_id",1)));
                if(getIntent().getStringExtra("date")!=null)
                {
                    params.put("date",getIntent().getStringExtra("date"));
                }
                else
                {
                    params.put("date",date);
                }

                params.put("time_from",getIntent().getStringExtra("time_from"));
                params.put("time_to",getIntent().getStringExtra("time_to"));

                if(getIntent().getStringExtra("group")!=null)
                {
                    params.put("group",getIntent().getStringExtra("group"));
                }
                else
                {
                    params.put("group"," ");
                }

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

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }

        };

        studentRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(studentRequest);

    }



    public void editAttendence(ArrayList<Student> mStudentList)
    {

        int size=mStudentList.size();
        String students[]=new String[100];
        int i,j=0;

        for(i=0;i<size;i++)
        {
            if(mStudentList.get(i).getPresent()!=null)
            {
                if(mStudentList.get(i).getPresent())
                {
                    students[j]=mStudentList.get(i).getRollNo();
                    j++;
                }
            }

        }


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        final Map<String, String> postData = new HashMap<String, String>();

        if(getIntent().getBooleanExtra("is_prev",true))
        {
            postData.put("schedule_id",String.valueOf(sid));
            postData.put("date",getIntent().getStringExtra("date"));
            postData.put("class",getIntent().getStringExtra("class"));
        }
        else
        {
            postData.put("schedule_id",String.valueOf(getIntent().getIntExtra("schedule_id",0)));
            postData.put("date",date);
            postData.put("class",getIntent().getStringExtra("class"));
        }




        for(i = 0; i< j; i++)
        {
            postData.put("students["+i+"]",students[i]);
        }



        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Saving Attendence ......");


        progressDialog.setCancelable(false);
        progressDialog.show();



        StringRequest attendenceRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_EDIT_ATTENDENCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                Toast.makeText(getApplicationContext()," Attendence Edited Successfully !!",Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(StudentsActivity.this,HomeActivity.class));
//                                finishAffinity();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error Not working",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        if (error instanceof NetworkError) {
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

                        }

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return postData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = "Bearer "+ SharedPrefManager.getAccessToken();
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization",bearer);
                return headers;
            }



        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(attendenceRequest);
    }

    public void downloadAttendence(String fileType) {


        progressDialog.setMessage("Downloading "+fileType+"....");
        progressDialog.show();


        StringRequest studentRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_DOWNLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(getApplicationContext(),"Downloading",Toast.LENGTH_SHORT).show();

                        try {
                            //File path = Environment.getExternalStorageDirectory();
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                            String filename;
                            if(fileType.equals("excel"))
                            {
                                filename = getIntent().getStringExtra("class") + getIntent().getStringExtra("code")+ ".xlsx";
                            }
                            else
                            {
                                filename =  getIntent().getStringExtra("class") + getIntent().getStringExtra("code")+ "."+fileType;
                            }

                            File exportFile= new File(path,filename);

                            FileWriter fw = new FileWriter(exportFile.getAbsoluteFile());
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write(response);

                            bw.close();

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"File saved to downloads/ " + filename,Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(getApplicationContext(), "Server Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentsActivity.this,LoginActivity.class));
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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                Map<String, String> params = new HashMap<String, String>();
                params.put("class",getIntent().getStringExtra("class"));
                params.put("subject_id",String.valueOf(getIntent().getIntExtra("subject_id",1)));
                if(getIntent().getStringExtra("date")!=null)
                {
                    params.put("date",getIntent().getStringExtra("date"));
                }
                else
                {
                    params.put("date",date);
                }

                params.put("time_from",getIntent().getStringExtra("time_from"));
                params.put("time_to",getIntent().getStringExtra("time_to"));

                if(getIntent().getStringExtra("group")!=null)
                {
                    params.put("group",getIntent().getStringExtra("group"));
                }
                else
                {
                    params.put("group"," ");
                }
                params.put("type",fileType);
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