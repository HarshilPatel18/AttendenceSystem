package com.cyberia.attendencesystem.View.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.cyberia.attendencesystem.View.LoginActivity;
import com.cyberia.attendencesystem.View.Modals.Lecture;
import com.cyberia.attendencesystem.View.Modals.Subject;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends Fragment {

    private Context mContext;
    private ProgressDialog progressDialog;
    TimetableView timetableView;
    ArrayList<Schedule> schedules;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeTableFragment newInstance(String param1, String param2) {
        TimeTableFragment fragment = new TimeTableFragment();
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
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        mContext=container.getContext();
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        timetableView=view.findViewById(R.id.timetable);
        progressDialog=new ProgressDialog(mContext);
        loadTimeTable();

        return view;
    }

    public String initcap(String s){
        String str;
        if (s.length() == 6)
            return s;
        else{
            str =s.charAt(0)+ "";
            for (int i = 1; i < s.length() - 1; i++)
                if (s.charAt(i) == ' ')
                    str+=s.charAt(i+1);

            return str;
        }
    }


    public void loadTimeTable()
    {
        progressDialog.setMessage("Loading ....");
        progressDialog.show();
        StringRequest teacherRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_TIMETABLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getBoolean("success"))
                            {
                                schedules = new ArrayList<Schedule>();
                                JSONArray jsonArray= jsonObject.getJSONArray("data");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonLecture = jsonArray.getJSONObject(i);


                                    Subject mSubject=new Subject(jsonLecture.getJSONObject("subject").getInt("id"),
                                            jsonLecture.getJSONObject("subject").getString("name"),
                                            jsonLecture.getJSONObject("subject").getString("code"),
                                            jsonLecture.getJSONObject("subject").getString("class"));
                                    Lecture mLecture= new Lecture(0,
                                           0,
                                            0,
                                            jsonLecture.getInt("id"),
                                            jsonLecture.getInt("lecture_id"),
                                            jsonLecture.getString("day"),
                                            jsonLecture.getString("time_from"),
                                            jsonLecture.getString("time_to"),
                                            jsonLecture.getInt("type"),
                                            jsonLecture.getString("group"),
                                            false,
                                            mSubject);

                                    Schedule schedule = new Schedule();
                                    schedule.setClassTitle(initcap(mLecture.getSubject().getName()));
                                    schedule.setClassPlace(mLecture.getSubject().getclassName());

                                    String day=mLecture.getDay();
                                    if(day.equals("MON"))
                                        schedule.setDay(0);
                                    else if(day.equals("TUE"))
                                        schedule.setDay(1);
                                    else if(day.equals("WED"))
                                        schedule.setDay(2);
                                    else if(day.equals("THU"))
                                        schedule.setDay(3);
                                    else if(day.equals("FRI"))
                                        schedule.setDay(4);
                                    else if (day.equals("SAT"))
                                        schedule.setDay(5);
                                    else
                                        schedule.setDay(6);



                                    Time startTime=new Time(Integer.parseInt(mLecture.getTimeFrom().trim().substring(0,2)),Integer.parseInt(mLecture.getTimeFrom().trim().substring(3,5)));


                                    if(startTime.getHour()>=1 && startTime.getHour()<7)
                                    {
                                        startTime.setHour(startTime.getHour()+12);
                                    }
                                    schedule.setStartTime(startTime);


                                    Time endTime=new Time(Integer.parseInt(mLecture.getTimeTo().trim().substring(0,2)),Integer.parseInt(mLecture.getTimeTo().trim().substring(3,5)));

                                    if(endTime.getHour()>=1 && endTime.getHour()<7)
                                    {
                                        endTime.setHour(endTime.getHour()+12);
                                    }
                                   /* if(mLecture.getSubject().getName().toLowerCase().trim().contains("lab"))
                                        endTime.setHour(startTime.getHour()+2);
                                    else
                                        endTime.setHour(startTime.getHour()+1);*/

                                    
                                    schedule.setEndTime(endTime);



                                    schedules.add(schedule);
                                }



                                timetableView.add(schedules);

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
                            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
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