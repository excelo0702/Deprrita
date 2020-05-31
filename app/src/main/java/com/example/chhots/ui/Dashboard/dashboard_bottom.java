package com.example.chhots.ui.Dashboard;


import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chhots.LeaderboardAdapter;
import com.example.chhots.LeaderboardModel;
import com.example.chhots.R;
import com.example.chhots.InstructorInfoModel;
import com.example.chhots.ui.Dashboard.HistoryPackage.history;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class dashboard_bottom extends Fragment {


    public dashboard_bottom() {
        // Required empty public constructor
    }

    private Button history,leaderboard;
    BottomNavigationView bottomNavigationView;
    private ImageView userImage,userBadge;
    TextView userName,userPoints,userPoint,userLeaderbardName;
    String cat="p";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    CircularProgressBar circularProgressBar,circularProgressBar2;
    String TAG ="dashboard";
    int points=0;


    RelativeLayout relativeLayout;
    PopupWindow mPopupWindow;
    String category = "Weekly";
    RecyclerView recyclerView;
    LeaderboardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    List<LeaderboardModel> list,list2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_bottom, container, false);


        Bundle bundle = this.getArguments();
        cat = bundle.getString("category");
        Log.d("main22222",cat);


        FirebaseOffline();
        init(view);
        int animationDuration = 2500; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(65, animationDuration);
        circularProgressBar2.setProgressWithAnimation(65, animationDuration);



        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new history();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.dashboard_layout,fragment);
                fragmentTransaction.commit();
            }
        });

        if(!cat.equals("MainActivity"))
        {
            history.setEnabled(false);
            history.setVisibility(GONE);
            leaderboard.setVisibility(GONE);
            leaderboard.setEnabled(false);
        }

        fetchUserInfo();
        fetchUserPoints();

        userPoints.setText(String.valueOf(points));
        if(points<200)
        {
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));
        }
        else if(points<300)
        {
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));

        }
        else{
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));
        }


        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaderboardUtil();
            }
        });





        return view;
    }



    private void showLeaderboardUtil() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.contest_leaderboard,null);


        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        ImageView close = customView.findViewById(R.id.close_leaderboard);
        TextView weekly = customView.findViewById(R.id.weekly);
        TextView overAll = customView.findViewById(R.id.Overall);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopupWindow.dismiss();
                }
                return false;
            }
        });

        final RecyclerView recyclerView = customView.findViewById(R.id.contest_leaderboard);
        final LeaderboardAdapter mAdapter;
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        final List<LeaderboardModel> list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mAdapter = new LeaderboardAdapter(list,getContext());
        recyclerView.setHasFixedSize(true);

        category="Weekly";
        showLeaderboard(category);
        showLeaderboardForUser(category);

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category="Weekly";
                showLeaderboard(category);
                showLeaderboardForUser(category);
            }
        });

        overAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category="OverAll";
                showLeaderboardForUser(category);
                showLeaderboard(category);
            }
        });

    }

    private void showLeaderboard(String category) {

        Query query = databaseReference.child("PointsInstructor").child(category).orderByChild("points").limitToLast(40);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    PointModel mode = ds.getValue(PointModel.class);
                    //TODO: points to int
                    LeaderboardModel model = new LeaderboardModel(userName.getText().toString(),String.valueOf(mode.getPoints()),mode.getId());
                    list.add(0,model);
                }
                mAdapter.setData(list);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    private void showLeaderboardForUser(String category) {

        Query query = databaseReference.child("PointsInstructor").child(category).orderByChild("points").limitToLast(40);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pos=0;
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    pos++;
                    PointModel mode = ds.getValue(PointModel.class);
                    if(mode.getId().equals(user.getUid()))
                    {
                        userLeaderbardName.setText(String.valueOf(pos)+". "+userName.getText().toString());
                        userPoint.setText(mode.getPoints());
                        break;
                    }
                    //TODO: points to int
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void init(View view) {
        relativeLayout = view.findViewById(R.id.rr6);

        circularProgressBar = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart);
        circularProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBar.setBackgroundColor(Color.GRAY);


        circularProgressBar2 = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart2);
        circularProgressBar2.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBar2.setBackgroundColor(Color.GRAY);
        userLeaderbardName = view.findViewById(R.id.userName_leaderboard_user);
        userPoint = view.findViewById(R.id.points_leaderboard_user);

        userPoints = view.findViewById(R.id.user_dashboard_points);
        userBadge = view.findViewById(R.id.user_dashboard_badge);
        history = view.findViewById(R.id.history);
        leaderboard = view.findViewById(R.id.learderboard);
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(GONE);

        userImage = view.findViewById(R.id.user_dashboard_profile);
        userName = view.findViewById(R.id.user_dashboard_name);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    private void FirebaseOffline() {
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

    }

    private void fetchUserInfo() {
        databaseReference.child("InstructorInfo").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                        Picasso.get().load(Uri.parse(model.getUserImageurl())).into(userImage);
                        userName.setText(model.getUserName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }


    private void fetchUserPoints() {
        databaseReference.child("PointsInstructor").child(user.getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
  //                          PointModel model = dataSnapshot.getValue(PointModel.class);
//                           points = model.getPoints();
                        }
                        else{
                            points = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }



}
