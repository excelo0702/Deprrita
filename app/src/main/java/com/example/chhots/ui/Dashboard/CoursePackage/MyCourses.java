package com.example.chhots.ui.Dashboard.CoursePackage;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chhots.R;
import com.example.chhots.category_view.Courses.AllCourseAdapter;
import com.example.chhots.category_view.Courses.CourseThumbnail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourses extends Fragment {


    public MyCourses() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private AllCourseAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<CourseThumbnail> list;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "MyCourse";
    private DatabaseReference mDatabaseRef;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_courses, container, false);
        recyclerView = view.findViewById(R.id.recycler_my_course_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        progressBar= view.findViewById(R.id.progress_bar_my_course);
        user = auth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAdapter = new AllCourseAdapter(list, getContext(),"MyCourses");


        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("disconnectmessage");
        presenceRef.onDisconnect().setValue("I disconnected!");
        presenceRef.onDisconnect().removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference reference) {
                if (error != null) {
                    Log.d(TAG, "could not establish onDisconnect event:" + error.getMessage());
                }
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "connected");
                } else {
                    Log.d(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

        mDatabaseRef.child("CoursesThumbnail").keepSynced(true);


        showAllCourse();


        return view;
    }


    private void showAllCourse() {

        mDatabaseRef.child("CoursesThumbnail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    if(model.getInstructorId().equals(user.getUid()))
                        list.add(0, model);
                }

                //    Collections.reverse(videolist);
                mAdapter.setData(list);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }






}
