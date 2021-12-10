package com.cyberia.attendencesystem.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.cyberia.attendencesystem.View.Adapter.LectureAdapter;
import com.cyberia.attendencesystem.View.AddLectureActivity;
import com.cyberia.attendencesystem.View.HomeActivity;
import com.cyberia.attendencesystem.View.Modals.Lecture;
import com.cyberia.attendencesystem.View.Modals.Subject;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LectureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LectureFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    TextView errorTextview;
    ArrayList<Lecture> lectureList;
    private Context mContext;
    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton fab;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LectureFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LectureFragment newInstance(String param1, String param2) {
        LectureFragment fragment = new LectureFragment();
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
        View view= inflater.inflate(R.layout.fragment_lecture, container, false);
        mContext=container.getContext();

        recyclerView=view.findViewById(R.id.recyclerLectures);
        errorTextview=view.findViewById(R.id.textViewErrorLecture);
        errorTextview.setVisibility(View.GONE);
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        });

        fab =view.findViewById(R.id.extFabAddLecture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddLectureActivity.class);
                intent.putExtra("isPrev",false);
                startActivity(intent);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.extend();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isExtended())
                    fab.shrink();
            }
        });

        swipeRefreshLayout= view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupLectures();
            }
        });


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        setupLectures();
                                    }
                                }
        );



        return view;
    }

    public void setupLectures() {


        StringRequest LectureRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_TODAY_LECTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        lectureList=new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Toast.makeText(mContext,response,Toast.LENGTH_SHORT).show();

                            if(jsonObject.getBoolean("success"))
                            {
                                JSONArray jsonArray= jsonObject.getJSONArray("data");

                                for (int i=0; i< jsonArray.length();i++)
                                {
                                    JSONObject jsonLecture = jsonArray.getJSONObject(i);

                                    Subject mSubject=new Subject(jsonLecture.getJSONObject("subject").getInt("id"),
                                            jsonLecture.getJSONObject("subject").getString("name"),
                                            jsonLecture.getJSONObject("subject").getString("code"),
                                            jsonLecture.getJSONObject("subject").getString("class"));
                                    Lecture mLecture= new Lecture(jsonLecture.getInt("teacher_id"),
                                            jsonLecture.getInt("subject_id"),
                                            jsonLecture.getInt("schedule_id"),
                                            jsonLecture.getInt("id"),
                                            jsonLecture.getInt("lecture_id"),
                                            jsonLecture.getString("day"),
                                            jsonLecture.getString("time_from"),
                                            jsonLecture.getString("time_to"),
                                            jsonLecture.getInt("type"),
                                            jsonLecture.getString("group"),
                                            jsonLecture.getBoolean("is_marked"),
                                            mSubject);




                                    lectureList.add(mLecture);

                                }
                                

                                mAdapter = new LectureAdapter(mContext,lectureList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                recyclerView.setAdapter(mAdapter);
                                errorTextview.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            swipeRefreshLayout.setRefreshing(false);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        errorTextview.setVisibility(View.VISIBLE);
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

        LectureRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(mContext).addToRequestQueue(LectureRequest);

    }


}