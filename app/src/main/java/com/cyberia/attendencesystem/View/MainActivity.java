package com.cyberia.attendencesystem.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Network.Constants;
import com.cyberia.attendencesystem.View.Network.RequestHandler;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.sentry.Sentry;

public class MainActivity extends AppCompatActivity {

    private long ms=0;
    private final long splashTime=100;
    private boolean splashActive=true, paused=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ConstraintLayout cl=findViewById(R.id.splashCL);
        ProgressBar progressBar=findViewById(R.id.progressBarSplash);
        progressBar.setIndeterminate(true);


        Thread thread = new Thread() {
            public void run() {
                try{
                    while (splashActive && ms<splashTime){
                        if(!paused) {
                            ms=ms+100;
                        }
                        sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(!isOnline())
                    {
                        Snackbar snackbar= Snackbar
                                .make(cl,"No Internet Access",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener(){

                                    @Override
                                    public void onClick(View view) {
                                        recreate();
                                    }
                                });
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.show();
                    }
                    else {

                        checkLogin();
                        //startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    }
                }
            }
        };
        thread.start();

    }

    public void checkLogin()
    {
        StringRequest refreshRequest=new StringRequest(
                Request.Method.GET,
                Constants.URL_TEST_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("foo"))
                            {
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));

                                finish();
                            }


                        } catch (JSONException e) {
                            //SharedPrefManager.getInstance(mContext).logout();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        SharedPrefManager.getInstance(MainActivity.this).logout();
                        if(error.getMessage()==null){
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
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
                String bearer = "Bearer "+SharedPrefManager.getInstance(MainActivity.this).getAccessToken();
                Map headers = new HashMap();
                headers.put("Authorization",bearer);
                return headers;
            }
        };

        refreshRequest.setRetryPolicy(new DefaultRetryPolicy(1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy
                        .DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(MainActivity.this).addToRequestQueue(refreshRequest);
    }

    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() !=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


}