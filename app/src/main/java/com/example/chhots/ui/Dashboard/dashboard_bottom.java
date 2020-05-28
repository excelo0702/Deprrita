package com.example.chhots.ui.Dashboard;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chhots.R;
import com.example.chhots.InstructorInfoModel;
import com.example.chhots.ui.Dashboard.HistoryPackage.history;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

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
    private ImageView userImage;
    TextView userName;
    String cat="p";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    CircularProgressBar circularProgressBar;
    String TAG ="dashboard";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_bottom, container, false);


        Bundle bundle = this.getArguments();
        cat = bundle.getString("category");
        Log.d("main22222",cat);


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


        circularProgressBar = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart);
        circularProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBar.setBackgroundColor(Color.GRAY);
        int animationDuration = 2500; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(65, animationDuration);

        history = view.findViewById(R.id.history);
        leaderboard = view.findViewById(R.id.learderboard);
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(GONE);

        userImage = view.findViewById(R.id.user_dashboard_profile);
        userName = view.findViewById(R.id.user_dashboard_name);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("InstructorInfo").child(auth.getCurrentUser().getUid())
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

        return view;
    }

}
