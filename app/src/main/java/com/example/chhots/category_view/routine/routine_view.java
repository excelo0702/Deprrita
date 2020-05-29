package com.example.chhots.category_view.routine;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.ChatBox.MessageModel;
import com.example.chhots.ChatBox.OnItemClickListener;
import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineModel;
import com.example.chhots.category_view.routine.RoutineViewAdapter;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Dashboard.HistoryPackage.HistoryModel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class routine_view extends Fragment implements onBackPressed {


    public routine_view() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<RoutineModel> list;
    private RoutineViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String routineId;
    private ImageView chatBtn;
    RelativeLayout rr2,fl;
    String category="";
    int flag;
    RelativeLayout tt;

    String htitle,hvideoName,hthumbnail;


    PlayerView playerView;
    SimpleExoPlayer player;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser user;
    String instructorId;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;
    private Uri videouri;
    TextView lala_land;
    FrameLayout lala_land1;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_view, container, false);

        lala_land = view.findViewById(R.id.lala_land);
        lala_land1 = view.findViewById(R.id.lala_land1);


        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.routines_recycler_view_section);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        playerView = view.findViewById(R.id.video_space_routine);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        player = ExoPlayerFactory.newSimpleInstance(getContext());
        mAdapter = new RoutineViewAdapter(list, getContext(), new OnItemClickListener() {

            @Override
            public void onItemClick(MessageModel model) {

            }

            @Override
            public void onItemClick(RoutineModel model) {

                videouri = Uri.parse(model.getVideoUrl());
                initializePlayer();

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                playerView.setPaddingRelative(0,0,0,0);
                playerView.setPadding(0,0,0,0);
                playerView.setBackgroundColor(Color.parseColor("#000000"));
            }
        });
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatBtn = view.findViewById(R.id.chat_with_instructor_routine);


        Bundle bundle = getArguments();
        category = bundle.getString("category");
        if(category.equals("Routine"))
        {

            routineId = bundle.getString("routineId");
            fetchRoutine();
        }
        else if(category.equals("Course"))
        {
            routineId = bundle.getString("courseId");
            fetchCourse();
        }
        Log.d("categoryyyy",category);


        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"FullScree",Toast.LENGTH_SHORT).show();
                Log.d("categoryyyy",category);
                FullScreen();
            }
        });


        return view;
    }

    private void fetchCourse() {
        list.clear();
        mDatabaseReference.child("Courses").child(routineId).orderByChild("sequenceNo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    RoutineModel model = ds.getValue(RoutineModel.class);
                    list.add(model);
                    instructorId=model.getInstructorId();
                    hvideoName = model.getTitle();
                    if(model.getThumbnail()!=null)
                        hthumbnail = model.getThumbnail();
                }
                mAdapter.setData(list);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void FullScreen() {
        if(fullScreen)
        {

            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_fullscreen_black_24dp));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int)( 300 * getContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();


            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(View.VISIBLE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.VISIBLE);
            playerView.setBackgroundColor(Color.parseColor("#000000"));

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(View.VISIBLE);

            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(View.VISIBLE);

            lala_land.setVisibility(View.VISIBLE);
            lala_land1.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.VISIBLE);
            fullScreen = false;
        }
        else
        {

            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_black_24dp));
            ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            ((AppCompatActivity)getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.height = params.MATCH_PARENT;
            params.width = (int)( (params.height*4)/3);
            playerView.setBackgroundColor(Color.parseColor("#000000"));
            playerView.setLayoutParams(params);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);


            //  player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(GONE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(GONE);

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(GONE);


            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(GONE);

            lala_land.setVisibility(GONE);
            lala_land1.setVisibility(GONE);
            recyclerView.setVisibility(GONE);
            chatBtn.setVisibility(GONE);


            fullScreen = true;
        }
    }

    private void pushHistory() {
        String time = System.currentTimeMillis()+"";
        HistoryModel model = new HistoryModel("Routine",hvideoName,hthumbnail,"date","Routine",routineId);
        mDatabaseReference.child("HISTORY").child(user.getUid()).child(time).setValue(model);
    }

    private void fetchRoutine() {
        mDatabaseReference.child("ROUTINEVIDEOS").child(routineId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            //TODO: Add thumbnail
                            RoutineModel model = ds.getValue(RoutineModel.class);
                            list.add(model);
                            instructorId=model.getInstructorId();
                            hvideoName = model.getTitle();
                            if(model.getThumbnail()!=null)
                                hthumbnail = model.getThumbnail();
                        }
                        mAdapter.setData(list);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        pushHistory();

    }





    private void initializePlayer() {

        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(videouri);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.prepare(mediaSource, false, false);
    }


    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    @Override
    public void onBackPressed() {
        FullScreen();
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);
        releasePlayer();

    }




}
