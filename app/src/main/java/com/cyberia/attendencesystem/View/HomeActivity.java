package com.cyberia.attendencesystem.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.cyberia.attendencesystem.R;
import com.cyberia.attendencesystem.View.Fragments.LectureFragment;
import com.cyberia.attendencesystem.View.Fragments.TimeTableFragment;
import com.cyberia.attendencesystem.View.Fragments.UserFragment;
import com.cyberia.attendencesystem.View.Network.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(!SharedPrefManager.isLoggedIn())
        {
            Toast.makeText(getApplicationContext(),"Session Time Out !",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }


        Fragment mfragment = new LectureFragment();
        loadFragment(mfragment);
        SmoothBottomBar navigation = (SmoothBottomBar) findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int menuItem) {
                Fragment fragment;
                switch (menuItem) {
                    case 0:
                        fragment = new LectureFragment();
                        loadFragment(fragment);
                        return true;
                    case 1:
                        fragment = new TimeTableFragment();
                        loadFragment(fragment);
                        return true;
                    case 2:
                        fragment = new UserFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}