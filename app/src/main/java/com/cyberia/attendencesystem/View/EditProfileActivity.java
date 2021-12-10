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
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ProgressBar progressBar;
    CardView mcardViewEP;
    EditText editTextTeacherName,editTextEmail,editTextOldPWD,editTextNewPWD,editTextCNFPWD;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this,LoginActivity.class));
        }

        TextView textViewTitle=findViewById(R.id.textViewTitleBar);
        textViewTitle.setText("Edit Profile");
        ImageView backButton = findViewById(R.id.imageBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mcardViewEP=findViewById(R.id.cardviewEP);
        mcardViewEP.setVisibility(View.GONE);
        CardView mCardviewUpdateButton=findViewById(R.id.cardviewUpdateButton);
        checkBox=findViewById(R.id.checkBoxPWD);


        progressBar=findViewById(R.id.progressBarEP);

        editTextTeacherName=findViewById(R.id.editTextEPName);
        editTextEmail=findViewById(R.id.editTextEPEmailAddress);
        editTextOldPWD=findViewById(R.id.editTextEPOldPassword);
        editTextNewPWD=findViewById(R.id.editTextEPNewPassword);
        editTextCNFPWD=findViewById(R.id.editTextEPNewCNFPassword);
        editTextOldPWD.setVisibility(View.GONE);
        editTextNewPWD.setVisibility(View.GONE);
        editTextCNFPWD.setVisibility(View.GONE);

        getUserData();

        mCardviewUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTeacherName.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Name can't be empty !",Toast.LENGTH_SHORT).show();
                }
                else if(editTextEmail.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Email can't be empty !",Toast.LENGTH_SHORT).show();
                }
                else{

                    if(checkBox.isChecked())
                    {
                        if(editTextOldPWD.getText().toString().equals("") || editTextNewPWD.getText().toString().equals("") || editTextCNFPWD.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Please, Fill all Password Fields !",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(editTextNewPWD.getText().toString().equals(editTextCNFPWD.getText().toString())){
                                UpdateProfile();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Password and Confirm Password should match !",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        UpdateProfile();
                    }



                }

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    editTextOldPWD.setVisibility(View.VISIBLE);
                    editTextNewPWD.setVisibility(View.VISIBLE);
                    editTextCNFPWD.setVisibility(View.VISIBLE);
                }
                else
                {
                    editTextOldPWD.setVisibility(View.GONE);
                    editTextNewPWD.setVisibility(View.GONE);
                    editTextCNFPWD.setVisibility(View.GONE);
                }
            }
        });

    }

    void UpdateProfile(){

        StringRequest teacherRequest=new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success"))
                            {
                                Toast.makeText(getApplicationContext(),"Updated Successfully !!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditProfileActivity.this,HomeActivity.class));

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Can't Update profile !!",Toast.LENGTH_SHORT).show();
                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfileActivity.this,LoginActivity.class));
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
                params.put("name",editTextTeacherName.getText().toString());
                params.put("email",editTextEmail.getText().toString());

                if(checkBox.isChecked())
                {
                    params.put("password",editTextOldPWD.getText().toString());
                    params.put("new_password",editTextNewPWD.getText().toString());
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



        };

        teacherRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(teacherRequest);

    }


    void getUserData(){


        StringRequest teacherRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_USER_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success"))
                            {
                                JSONObject jsonData= jsonObject.getJSONObject("data");

                                editTextTeacherName.setText(jsonData.getString("name"));
                                editTextEmail.setText(jsonData.getString("email"));
                                progressBar.setVisibility(View.GONE);
                                mcardViewEP.setVisibility(View.VISIBLE);

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                        if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfileActivity.this,LoginActivity.class));
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