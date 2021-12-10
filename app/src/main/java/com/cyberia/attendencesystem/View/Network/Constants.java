package com.cyberia.attendencesystem.View.Network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cyberia.attendencesystem.View.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    private static final String API_ROOT_URL = "https://floating-reef-59337.herokuapp.com/api";



    //auth
    public static final String URL_LOGIN= API_ROOT_URL+"/login";
    public static final String URL_TEST_TOKEN=API_ROOT_URL+"/test";
    public static final String URL_GET_USER_DATA=API_ROOT_URL+"/auth/user";

    public static final String URL_MARK_ATTENDENCE=API_ROOT_URL+"/attendance/mark";
    public static final String URL_EDIT_ATTENDENCE=API_ROOT_URL+"/attendance/edit";

    public static final String URL_GET_ALLCLASSES=API_ROOT_URL+"/class/all";
    public static final String URL_GET_SUBJECT_BYCLASS=API_ROOT_URL+"/subject/get/class/";

    public static final String URL_ADD_LECTURE=API_ROOT_URL+"/schedule/extra";

    public static final String URL_UPDATE_PROFILE=API_ROOT_URL+"/auth/profile/edit";

    public static final String URL_GET_SUBJECT_BY_TEACHER=API_ROOT_URL+"/lecture/teacher/";

    public static final String URL_ALERT_EMAIL=API_ROOT_URL+"/teacher/alert";

    //Lecture
    public static final String URL_TODAY_LECTURE=API_ROOT_URL+"/schedule/today";
    public static final String URL_TIMETABLE=API_ROOT_URL+"/schedule/teacher";

    //Student
    public static final String URL_STUDENT_LIST=API_ROOT_URL+"/schedule/get/";
    public static final String URL_STUDENT_MARKED=API_ROOT_URL+"/attendance/get";

    public static final String URL_DOWNLOAD = API_ROOT_URL+"/attendance/list/export";

    public static final String URL_ViewSTATS= API_ROOT_URL+"/teacher/stats/subject/";


}
