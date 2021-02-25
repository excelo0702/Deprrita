package com.example.chhots.category_view.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chhots.R;

public class addRoutine extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);

        getSupportFragmentManager().beginTransaction().add(R.id.routine_add,new Step1AddRoutine()).addToBackStack(null).commit();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
