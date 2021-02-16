package com.example.chhots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.LetsGetStarted.lets_get_started;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarnava.textwriter.TextWriter;

public class SplashScreen_Activity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;
    FirebaseUser usesr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        usesr = FirebaseAuth.getInstance().getCurrentUser();


        if(getIntent().hasCategory("Chat"))
        {
            Intent intent = new Intent(SplashScreen_Activity.this, ChatWithInstructor.class);
            intent.putExtra("category","Instructor");
            intent.putExtra("routineId",getIntent().getStringExtra("routineId"));
            intent.putExtra("peopleId",getIntent().getStringExtra("peopleId"));
            startActivity(intent);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(usesr==null){
                    Intent mainIntent = new Intent(SplashScreen_Activity.this, lets_get_started.class);
                    startActivity(mainIntent);
                    SplashScreen_Activity.this.finish();

                }
                else
                {
                    Intent mainIntent = new Intent(SplashScreen_Activity.this,MainActivity.class);
                    startActivity(mainIntent);
                    SplashScreen_Activity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
