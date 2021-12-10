package com.cyberia.attendencesystem.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Modals.Student;
import com.cyberia.attendencesystem.View.Modals.Subject;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertEmailActivity extends AppCompatActivity {

    private ListView listView;
    private CardView buttonAlert;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_email);


        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AlertEmailActivity.this,LoginActivity.class));
        }
        TextView textViewTitle=findViewById(R.id.textViewTitleBar);
        textViewTitle.setText("Alert Email");
        ImageView backButton = findViewById(R.id.imageBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView=findViewById(R.id.listViewSub);
        buttonAlert=findViewById(R.id.cardViewAlertButton);
        mProgressBar=findViewById(R.id.progressBarAlert);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();

            }
        });


        initializeListview();

        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertEmail();
            }
        });
    }

    private void alertEmail() {

        SparseBooleanArray sp = listView.getCheckedItemPositions();
        String subCode[]=new String[10];

        int k=0;
        for(int i=0;i<sp.size();i++)
        {
            if(sp.valueAt(i))
            {
                Subject subject= (Subject) listView.getItemAtPosition(i);
                subCode[k]=subject.getCode();
                k++;
                //Toast.makeText(getApplicationContext(),subCode[i],Toast.LENGTH_SHORT).show();
            }
        }



        final Map<String, String> postData = new HashMap<String, String>();
        for(int i=0;i<k;i++)
        {
            postData.put("names["+i+"]",subCode[i]);
        }



        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Mailing Students");
        progressDialog.setMessage("This may take long time. Hold on....");

        progressDialog.setCancelable(false);
        progressDialog.show();



        StringRequest attendenceRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_ALERT_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                Toast.makeText(getApplicationContext()," Mail sent Successfully.",Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error Sending Mail !!",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AlertEmailActivity.this,LoginActivity.class));
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

        attendenceRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(attendenceRequest);
    }

    private void initializeListview() {


        StringRequest teacherRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_SUBJECT_BY_TEACHER+String.valueOf(getIntent().getIntExtra("teacher_id",0)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success"))
                            {
                                mProgressBar.setVisibility(View.GONE);
                                JSONObject jsonData= jsonObject.getJSONObject("data");
                                JSONArray jsonArray=jsonData.getJSONArray("lectures");


                                List<Subject> subjectList=new ArrayList<>();
                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonLec = jsonArray.getJSONObject(i);
                                    JSONObject jsonSub = jsonLec.getJSONObject("subject");

                                    Subject subject=new Subject(jsonSub.getInt("id"),jsonSub.getString("name"),
                                            jsonSub.getString("code"),jsonSub.getString("class"));

                                   subjectList.add(subject);

                                }

                                ArrayAdapter<Subject> arrayAdapter = new ArrayAdapter<Subject>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, subjectList);


                                listView.setAdapter(arrayAdapter);
                                buttonAlert.setVisibility(View.VISIBLE);

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
                            startActivity(new Intent(getApplicationContext().getApplicationContext(),LoginActivity.class));
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

        teacherRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(teacherRequest);
    }


}