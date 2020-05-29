package com.example.chhots.ui.Dashboard;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chhots.R;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Dashboard.ApproveVideo.ApproveVideo;
import com.example.chhots.ui.Dashboard.Favorite.favorite;
import com.example.chhots.ui.home.HomeFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class dashboard extends Fragment implements onBackPressed {


    public dashboard() {
        // Required empty public constructor
    }
    
    private Button history,leaderboard;
    BottomNavigationView bottomNavigationView;
    String cat="p";

    private FirebaseUser user;
    private DatabaseReference databaseReference;

    private BadgeDrawable badge_approve;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);



        Bundle bundle = getArguments();
        cat = bundle.getString("category");
        Log.d("main222p",cat);




        Fragment fragment = new dashboard_bottom();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        Bundle bundle1 = new Bundle();
        bundle1.putString("category",cat);
        fragment.setArguments(bundle1);
        Log.d("main222o","fragment");
        fragmentTransaction.commit();

        bottomNavigationView = view.findViewById(R.id.bottom_navigation_dashboard);
        if(!cat.equals("MainActivity"))
        {
            bottomNavigationView.setEnabled(false);
            bottomNavigationView.setVisibility(GONE);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("NotificationNumber").child(user.getUid());
        databaseReference.child("dashboard").child("ApproveVideo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k = (int)dataSnapshot.getChildrenCount();
                        if(k>0) {
                            badge_approve = bottomNavigationView.getOrCreateBadge(R.id.approve_videos_dashboard);
                            badge_approve.setBackgroundColor(Color.BLUE);
                            badge_approve.setBadgeTextColor(Color.RED);
                            badge_approve.setMaxCharacterCount(3);
                            badge_approve.setNumber(k);
                            badge_approve.setVisible(true);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.dashboard_dashboard:

                        Fragment fragment = new dashboard_bottom();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.dashboard_layout,fragment);
                        fragmentTransaction.addToBackStack(null);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("category",cat);
                        fragment.setArguments(bundle1);
                        Log.d("main222o","fragment");
                        fragmentTransaction.commit();

                       // setFragment(new dashboard_bottom());
                        break;

                    case R.id.favorite_dashboard:
                        setFragment(new favorite());
                        break;

                    case R.id.community_dashboard:
                        setFragment(new favorite());
                        break;

                    case R.id.certificates_dashboard:
                        setFragment(new favorite());
                        break;

                    case R.id.approve_videos_dashboard:
                        setFragment(new ApproveVideo());
                        break;
                }
                return true;
            }
        });
        return view;
    }


    private void setFragment(Fragment fragment) {

        
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,fragment);
        fragmentTransaction.commit();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(GONE);
        setFragment(new HomeFragment());
    }
}
