package com.example.chhots.ui.Dashboard.ApproveVideo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chhots.InstructorInfoModel;
import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApproveVideo extends Fragment {


    public ApproveVideo() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ApproveVideoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    private DatabaseReference mDatabaseReference;
    private List<RoutineThumbnailModel> list;

    private FirebaseUser user;
    private InstructorInfoModel User_model;
    private String TAG="ApproveVideo";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_approve_video, container, false);
        recyclerView = view.findViewById(R.id.routine_videos);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        mAdapter = new ApproveVideoAdapter(list,getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        fetchUserInfo();

        fetchRoutine();

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


        return view;
    }

    private void fetchUserInfo() {
        mDatabaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User_model = dataSnapshot.getValue(InstructorInfoModel.class);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void fetchRoutine() {
        mDatabaseReference.child("ROUTINE_THUMBNAIL")
           .addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot ds: dataSnapshot.getChildren())
                   {
                       RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                       if(model.getInstructorId().equals(user.getUid()))
                       {
                           list.add(0,model);
                       }
                   }
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
