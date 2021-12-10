package com.cyberia.attendencesystem.View.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.AddLectureActivity;
import com.cyberia.attendencesystem.View.AlertEmailActivity;
import com.cyberia.attendencesystem.View.EditProfileActivity;
import com.cyberia.attendencesystem.View.LoginActivity;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.cyberia.attendencesystem.View.ViewStatsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    TextView teacherName,email;
    int teacher_id;
    private Context mContext;
    private ProgressDialog progressDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mContext=container.getContext();
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setCancelable(false);

        getUserData();

        ConstraintLayout conEditProfile=view.findViewById(R.id.conEditProfile);
        ConstraintLayout conPrevLecture=view.findViewById(R.id.conViewPrev);
        ConstraintLayout conViewStats=view.findViewById(R.id.conViewStats);
        ConstraintLayout conAlertEmail=view.findViewById(R.id.conAlertEmail);
        ConstraintLayout conLogout=view.findViewById(R.id.conLogout);

        conPrevLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddLectureActivity.class);
                intent.putExtra("isPrev",true);
                startActivity(intent);
                Animatoo.animateSlideLeft(mContext);
            }
        });
        conEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                Animatoo.animateSlideLeft(mContext);
            }
        });

        conViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ViewStatsActivity.class);
                intent.putExtra("isPrev",true);
                startActivity(intent);
                Animatoo.animateSlideLeft(mContext);

            }
        });

        conAlertEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AlertEmailActivity.class);
                intent.putExtra("teacher_id",teacher_id);
                startActivity(intent);
                Animatoo.animateSlideLeft(mContext);
            }
        });



        conLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage(" Do you want to Logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPrefManager.logout();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.setTitle("Logout");
                alertDialog.show();


            }
        });
        teacherName=view.findViewById(R.id.textViewUserName);
        email=view.findViewById(R.id.textViewEmail);



        return view;
    }

    void getUserData(){

        progressDialog.setMessage("Loading ....");
        progressDialog.show();
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

                                teacherName.setText(jsonData.getString("name"));
                                email.setText(jsonData.getString("email"));
                                progressDialog.dismiss();
                                teacher_id=jsonData.getInt("id");

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
                        if (error instanceof NetworkError) {
                            Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {

                            Toast.makeText(mContext, "Server Error"+error, Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(mContext, "Session Timeout.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity().getApplicationContext(),LoginActivity.class));
                        } else if (error instanceof ParseError) {
                            Toast.makeText(mContext, "Parse Error", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(mContext, "No Connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(mContext, "Timeout", Toast.LENGTH_SHORT).show();

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
        RequestHandler.getInstance(mContext).addToRequestQueue(teacherRequest);
    }
}