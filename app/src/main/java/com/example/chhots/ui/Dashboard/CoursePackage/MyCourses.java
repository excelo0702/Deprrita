package com.example.chhots.ui.Dashboard.CoursePackage;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.example.chhots.category_view.Courses.AllCourseAdapter;
import com.example.chhots.category_view.Courses.CourseThumbnail;
import com.example.chhots.ui.Category.category;
import com.example.chhots.ui.Subscription.SUbscriptionViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCourses extends Fragment {


    public MyCourses() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private MyCourseAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<MyCourseModel> list;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final String TAG = "MyCourse";
    private DatabaseReference mDatabaseRef;
    private LoadingDialog loadingDialog;
    double viewIPAYL,viewIR,viewRC1,viewRC6,viewRC1Y,viewF1,viewF6,viewF1Y,viewPAYL,views;
    double viewTIR,viewTIC,viewTPAYL,viewTR1M,viewTR6M,viewTR1Y,viewTF1M,viewTF6M,viewTF1Y,viewTC1M,viewTC6M,viewTC1Y;




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
        mAdapter = new MyCourseAdapter(list, getContext());
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();


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
        fetchTotalUsers();


        return view;
    }


    private void showAllCourse() {

        mDatabaseRef.child("CoursesThumbnail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");

                if(dataSnapshot.getChildrenCount()==0)
                {
                    loadingDialog.DismissDialog();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    final CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    if(model!=null && model.getInstructorId().equals(user.getUid()))
                    {
                        String id = model.getCourseId();
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SubscriptionCourseViews").child(id);
                        databaseReference.child("Individual")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds: dataSnapshot.getChildren())
                                        {
                                            Log.d("uuuutt",ds.getValue()+"");
                                            SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                            if(model.getCategory().equals("Course"))
                                            {
                                                viewIR=model.getViews();
                                            }
                                            else if(model.getCategory().equals("PAYL"))
                                            {
                                                viewIPAYL=model.getViews();
                                            }
                                        }
                                        databaseReference.child("Course")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            Log.d("uuuurr",ds.getValue()+"");

                                                            SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                                            if (model.getCategory().equals("1month")) {
                                                                viewRC1 = model.getViews();
                                                            } else if (model.getCategory().equals("6month")) {
                                                                viewRC6 = model.getViews();

                                                            } else if (model.getCategory().equals("1year")) {
                                                                viewRC1Y = model.getViews();
                                                            }

                                                        }
                                                        Log.d("uuuuuu", viewRC1 + " " + viewRC6 + " " + viewRC1Y);

                                                        databaseReference.child("Full")
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for(DataSnapshot ds: dataSnapshot.getChildren())
                                                                        {
                                                                            Log.d("uuuuee",ds.getValue()+"");

                                                                            SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                                                            if(model.getCategory().equals("1month"))
                                                                            {
                                                                                viewF1=model.getViews();
                                                                            }
                                                                            else if(model.getCategory().equals("6month"))
                                                                            {
                                                                                viewF6=model.getViews();
                                                                            }
                                                                            else if(model.getCategory().equals("1year"))
                                                                            {
                                                                                viewF1Y=model.getViews();
                                                                            }
                                                                            Log.d("uuuuuu",viewF1+" "+viewF6+" "+viewF1Y);
                                                                        }

                                                                        double money = ((30*viewIR)/max(1.0,viewTIR) + (viewRC1*120)/max(1.0,viewTR1M) + (viewRC6*112)/max(1.0,viewTR6M) + (viewRC1Y*107)/max(1.0,viewTR1Y) + (viewF1*450)/max(1.0,viewTF1M)
                                                                                +(viewF6*442)/max(1.0,viewTF6M) + (viewF1Y*437)/max(1.0,viewTF1Y))*0.7;

                                                                        Log.d("?????",money+"   "+viewRC1+" "+viewTR1M);
                                                                        DecimalFormat dec = new DecimalFormat("#0.00");

                                                                        MyCourseModel mode = new MyCourseModel(model.getCourseName(),model.getCourseId(),model.getCourseImage(), dec.format(money));
                                                                        list.add(0, mode);
                                                                        mAdapter.setData(list);
                                                                        recyclerView.setLayoutManager(mLayoutManager);
                                                                        recyclerView.setAdapter(mAdapter);
                                                                        loadingDialog.DismissDialog();

                                                                    }
                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }


                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(list.size()==0)
                        {
                            loadingDialog.DismissDialog();
                        }
                    }
                },1000);


                //    Collections.reverse(videolist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    public void fetchTotalUsers()
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TotalUsersView");
        databaseReference.child("Routine").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuoo",ds.getValue()+"");
                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTR1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTR6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTR1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuupp",ds.getValue()+"");

                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTC1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTC6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTC1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Individual").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuqq",ds.getValue()+"");

                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("Routine"))
                    {
                        viewTIR = model.getViews();
                    }
                    else if(model.getCategory().equals("Course"))
                    {
                        viewTIC = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Full").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuww",ds.getValue()+"");
                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTF1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTF6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTF1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






}
