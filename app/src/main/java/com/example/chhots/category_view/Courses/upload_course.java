package com.example.chhots.category_view.Courses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.chhots.R;

public class upload_course extends AppCompatActivity {



    //pass courseId

    private static final String TAG = "Upload_Course";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course);

        getSupportFragmentManager().beginTransaction().add(R.id.course_vvv,new Step1()).addToBackStack(null).commit();
        setFragment(new Step1());
    }


    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      //  FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.course_vvv,fragment);
        fragmentTransaction.commit();
    }






}
