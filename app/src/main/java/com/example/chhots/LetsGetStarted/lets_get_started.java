package com.example.chhots.LetsGetStarted;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chhots.InstructorLogin;
import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.example.chhots.Signup;
import com.example.chhots.ui.home.Adapter;
import com.example.chhots.ui.home.Model;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class lets_get_started extends AppCompatActivity {


    ViewPager viewPager;
    LGS_Adapter adapter;
    List<LetsGetStartedModel> models;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    private TextView letsGetstart,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_get_started);
        letsGetstart = findViewById(R.id.lets_get_started);
        login = findViewById(R.id.login);

        TabLayout tabLayout = findViewById(R.id.tab_layout);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(lets_get_started.this, InstructorLogin.class));
            }
        });
        letsGetstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(lets_get_started.this, Signup.class));
            }
        });

        models = new ArrayList<>();
        models.add(new LetsGetStartedModel("Welcome to Smurfo House","Smurfo House is an E-Learning platform which facalitating and helping people",R.drawable.w1));
        models.add(new LetsGetStartedModel("Welcome to Smurfo House","ReadMore",R.drawable.w1));
        models.add(new LetsGetStartedModel("Welcome to Smurfo House","ReadMore",R.drawable.w1));

        adapter  = new LGS_Adapter(models, getApplicationContext());
        viewPager = findViewById(R.id.viewPager_lgs);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0,0,0,0);

        tabLayout.setupWithViewPager(viewPager,true);


    }
}