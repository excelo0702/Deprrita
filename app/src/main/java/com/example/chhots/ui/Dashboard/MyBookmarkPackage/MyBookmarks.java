package com.example.chhots.ui.Dashboard.MyBookmarkPackage;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.ExploreVideoAdapter;
import com.example.chhots.Models.ExploreVideoModel;
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
public class MyBookmarks extends Fragment {


    public MyBookmarks() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    ExploreVideoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    private DatabaseReference mDatabaseRef;
    private List<ExploreVideoModel> videolist;
    private static final String TAG = "Bookmarks";
    private LoadingDialog loadingDialog;
    FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_bookmarks, container, false);

        videolist = new ArrayList<>();
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        recyclerView = view.findViewById(R.id.recycler_my_bookmark_view);
        mAdapter = new ExploreVideoAdapter(videolist,getContext());

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Bookmarks");
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

        showVideos();


        return view;
    }


    private void showVideos() {
        videolist.clear();
        mDatabaseRef.child(user.getUid()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        mDatabaseRef.keepSynced(true);
    }




}
