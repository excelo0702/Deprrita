package com.example.chhots.category_view.routine;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.ChatBox.MessageModel;
import com.example.chhots.ChatBox.OnItemClickListener;
import com.example.chhots.ChatBox.exoPlayerFullScreen;
import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.example.chhots.UserClass;
import com.example.chhots.category_view.Courses.CourseThumbnail;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Category.category;
import com.example.chhots.ui.Dashboard.CoursePackage.MyCourseModel;
import com.example.chhots.ui.Dashboard.HistoryPackage.HistoryModel;
import com.example.chhots.ui.Subscription.SUbscriptionViewModel;
import com.example.chhots.ui.Subscription.SubscriptionModel;
import com.example.chhots.ui.notifications.NotificationModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class routine_view extends Fragment implements onBackPressed{


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

    String htitle,hvideoName,hthumbnail,thumbnail;

    StringBuffer no_of_lectures;
    PlayerView playerView;
    SimpleExoPlayer player;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser user;
    String instructorId;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    ImageButton mirror;
    boolean fullScreen = false,rotation=false;
    private Uri videouri;
    TextView lala_land;
    FrameLayout lala_land1;
    Switch sw1;
    MyCourseModel courseModel;
    String cat,planplan,catViews,categoryP;
    double viewIPAYL,viewIR,viewRC1,viewRC6,viewRC1Y,viewF1,viewF6,viewF1Y,viewPAYL,views;
    double viewTIR,viewTIC,viewTPAYL,viewTR1M,viewTR6M,viewTR1Y,viewTF1M,viewTF6M,viewTF1Y,viewTC1M,viewTC6M,viewTC1Y;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null)
        {
            playbackPosition = savedInstanceState.getLong("pos");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
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

        sw1 = playerView.findViewById(R.id.mirror);

        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw1.isChecked())
                {
                    RelativeLayout rr = playerView.findViewById(R.id.exoplayback);
                    rr.setRotationY(180);
                    playerView.setRotationY(180);
                }
                else
                {
                    RelativeLayout rr = playerView.findViewById(R.id.exoplayback);
                    rr.setRotationY(0);
                    playerView.setRotationY(0);

                }
            }
        });



        mAdapter = new RoutineViewAdapter(list, getContext(), new OnItemClickListener() {

            @Override
            public void onItemClick(MessageModel model) { }

            @Override
            public void onItemClick(RoutineModel model,int position) {

                if(videouri==null ||!model.getVideoUrl().equals(videouri.toString())) {
                    Log.d("pop qoq ", model.getVideoUrl());
                    videouri = Uri.parse(model.getVideoUrl());
                    initializePlayer();
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    playerView.setPaddingRelative(0, 0, 0, 0);
                    playerView.setPadding(0, 0, 0, 0);
                    playerView.setBackgroundColor(Color.parseColor("#000000"));

                }
            }
            @Override
            public void onItemClick(SubscriptionModel model, String plan, String price, int pos) { }
        });
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatBtn = view.findViewById(R.id.chat_with_instructor_routine);
        chatBtn.setVisibility(GONE);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                Intent intent = new Intent(getContext(), ChatWithInstructor.class);
                intent.putExtra("category","category");
                intent.putExtra("instructorId",instructorId);
                intent.putExtra("routineId",routineId);
                getActivity().startActivity(intent);
            }
        });

        Bundle bundle = getArguments();
        category = bundle.getString("category");
        if(category.equals("Routine"))
        {
            thumbnail = bundle.getString("thumbnail");
            routineId = bundle.getString("routineId");
            cat = bundle.getString("cat");
            planplan = bundle.getString("planplan");
            catViews = "routine_views";
            categoryP="ROUTINE_THUMBNAIL";
            fetchRoutine();
        }
        else if(category.equals("Course"))
        {
            thumbnail = bundle.getString("thumbnail");
            routineId = bundle.getString("routineId");
            cat = bundle.getString("cat");
            planplan = bundle.getString("planplan");
            catViews = "views";
            categoryP="CoursesThumbnail";

            fetchCourse();
        }
        Log.d("categoryyyy",category);

        fetchTotalUsers();
        fetchViews(category,routineId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { checkView();
            }
        },3000);


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

    private void checkView()
    {
        mDatabaseReference.child("SubscriptionHistory").child(user.getUid()).child(routineId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0)
                        {
                            mDatabaseReference.child("SubscriptionHistory").child(user.getUid()).child(routineId).child(routineId).setValue(routineId);
                            increaseCount();
                            increaseInstructorPoints();
                            if(category.equals("Routine"))
                                increaseViewRoutine();
                            else
                                increaseViewCourse();
                            //increase no. of counts of Course and Routine
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void increaseInstructorPoints() {
    }

    private void increaseViewCourse() {
        mDatabaseReference.child("CoursesThumbnail").child(routineId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CourseThumbnail model = dataSnapshot.getValue(CourseThumbnail.class);
                        model.setViews(model.getViews()+1);
                        mDatabaseReference.child("CoursesThumbnail").child(routineId).setValue(model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void increaseViewRoutine() {
        mDatabaseReference.child("ROUTINE_THUMBNAIL").child(routineId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RoutineThumbnailModel model = dataSnapshot.getValue(RoutineThumbnailModel.class);
                        model.setRoutine_views(String.valueOf(Integer.parseInt(model.getRoutine_views())+1));
                        mDatabaseReference.child("ROUTINE_THUMBNAIL").child(routineId).setValue(model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void fetchCourse() {
        list.clear();
        mDatabaseReference.child("Course").child(routineId).orderByChild("sequenceNo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    RoutineModel model = ds.getValue(RoutineModel.class);
                    list.add(model);
                    instructorId=model.getInstructorId();
                    hvideoName = model.getTitle();
                    hthumbnail=thumbnail;

                }
                pushHistory();
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

            fullScreen = true;
        }
    }


    private void pushHistory() {

        mDatabaseReference.child("ROUTINE_THUMBNAIL").child(routineId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RoutineThumbnailModel model = dataSnapshot.getValue(RoutineThumbnailModel.class);
                mDatabaseReference.child("MyRoutines").child(user.getUid()).child(routineId).setValue(model);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String time = System.currentTimeMillis()+"";
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        HistoryModel model = new HistoryModel(hvideoName,instructorId,hthumbnail,date,category,routineId);
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
                            Log.d("pop pop pop",ds.getValue()+" q o q oq ");
                            list.add(model);
                            instructorId=model.getInstructorId();
                            hvideoName = model.getTitle();
                            hthumbnail = thumbnail;
                        }
                        pushHistory();
                        mAdapter.setData(list);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getContext());

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

    public void increaseCount()
    {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //TODO if he select individual

        //notification for instructor
        NotificationModel notify = new NotificationModel(date,user.getUid()+" Routine Purchase",user.getUid(),"description",thumbnail);
        Log.d("InstructorNotify",notify.getDate());

        //add views to instructor
        //if it is routine
        if(category.equals("Routine"))
        {
            if(cat.equals("Individual"))
            {
                if(planplan.equals("Routine"))
                {
                    views = viewIR;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("Routine",viewTIR+1);
                    mDatabaseReference.child("TotalUsersView").child("Individual").child("Routine").setValue(mode);



                }
                else if(planplan.equals("PAYL"))
                {
                    views = viewIPAYL;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("PAYL",viewTPAYL+1);
                    mDatabaseReference.child("TotalUsersView").child("Individual").child("PAYL").setValue(mode);

                }
            }
            else if(cat.equals("Routine"))
            {
                if(planplan.equals("1month"))
                {
                    views = viewRC1;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1month",viewTR1M+1);
                    mDatabaseReference.child("TotalUsersView").child("Routine").child("1month").setValue(mode);


                }
                else if(planplan.equals("6month"))
                {
                    views = viewRC6;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("6month",viewTR6M+1);
                    mDatabaseReference.child("TotalUsersView").child("Routine").child("6month").setValue(mode);



                }
                else if(planplan.equals("1year"))
                {
                    views = viewRC1Y;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1year",viewTR1Y+1);
                    mDatabaseReference.child("TotalUsersView").child("Routine").child("1year").setValue(mode);


                }
            }
            else if(cat.equals("Full"))
            {
                if(planplan.equals("1month"))
                {
                    views = viewF1;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1month",viewTF1M+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("1month").setValue(mode);


                }
                else if(planplan.equals("6month"))
                {
                    views = viewF6;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("6month",viewTF6M+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("6month").setValue(mode);


                }
                else if(planplan.equals("1year"))
                {
                    views = viewF1Y;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1year",viewTF1Y+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("1year").setValue(mode);


                }
            }
            //TODO: Incrase the points of instructor,number of views of routine

            SUbscriptionViewModel mode = new SUbscriptionViewModel(planplan,views+1);
            Log.d("uuuuii",mode.getViews()+" p p p ");
            mDatabaseReference.child("SubscriptionRoutineViews").child(routineId).child(cat).child(planplan).setValue(mode);

        }
        else if(category.equals("Course"))
        {
            if(cat.equals("Individual"))
            {
                if(planplan.equals("Course"))
                {
                    views = viewIR;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("Course",viewTIR+1);
                    mDatabaseReference.child("TotalUsersView").child("Individual").child("Course").setValue(mode);



                }
                else if(planplan.equals("PAYL"))
                {
                    views = viewIPAYL;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("PAYL",viewTPAYL+1);
                    mDatabaseReference.child("TotalUsersView").child("Individual").child("PAYL").setValue(mode);

                }
            }
            else if(cat.equals("Course"))
            {
                if(planplan.equals("1month"))
                {
                    views = viewRC1;
                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1month",viewTC1M+1);
                    mDatabaseReference.child("TotalUsersView").child("Course").child("1month").setValue(mode);


                }
                else if(planplan.equals("6month"))
                {
                    views = viewRC6;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("6month",viewTC6M+1);
                    mDatabaseReference.child("TotalUsersView").child("Course").child("6month").setValue(mode);



                }
                else if(planplan.equals("1year"))
                {
                    views = viewRC1Y;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1year",viewTC1Y+1);
                    mDatabaseReference.child("TotalUsersView").child("Routine").child("1year").setValue(mode);

                }
            }
            else if(cat.equals("Full"))
            {
                if(planplan.equals("1month"))
                {
                    views = viewF1;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1month",viewTF1M+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("1month").setValue(mode);


                }
                else if(planplan.equals("6month"))
                {
                    views = viewF6;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("6month",viewTF6M+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("6month").setValue(mode);


                }
                else if(planplan.equals("1year"))
                {
                    views = viewF1Y;

                    SUbscriptionViewModel mode = new SUbscriptionViewModel("1year",viewTF1Y+1);
                    mDatabaseReference.child("TotalUsersView").child("Full").child("1year").setValue(mode);


                }
            }
            //TODO: Incrase the points of instructor,number of views of routine

            SUbscriptionViewModel mode = new SUbscriptionViewModel(planplan,views+1);
            mDatabaseReference.child("SubscriptionCourseViews").child(routineId).child(cat).child(planplan).setValue(mode);

        }



    }

    public void fetchViews(String category,String id)
    {
        Log.d("uuuuuup", category+"  "+id);
        if(category.equals("Routine"))
        {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SubscriptionRoutineViews").child(id);
            databaseReference.child("Individual")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                if(model.getCategory().equals("Routine"))
                                {
                                    viewIR=model.getViews();
                                }
                                else if(model.getCategory().equals("PAYL"))
                                {
                                    viewIPAYL=model.getViews();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            databaseReference.child("Routine")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.d("uuuuuuq",dataSnapshot.getValue()+"");
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Log.d("uuuuuuq",ds.getValue()+"");
                                SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                if (model.getCategory().equals("1month")) {
                                    viewRC1 = model.getViews();
                                } else if (model.getCategory().equals("6month")) {
                                    viewRC6 = model.getViews();

                                } else if (model.getCategory().equals("1year")) {
                                    viewRC1Y = model.getViews();
                                }

                            }
                            Log.d("uuuuuuq", viewRC1 + " " + viewRC6 + " " + viewRC1Y);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            databaseReference.child("Full")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("uuuuuuw",dataSnapshot.getValue()+"");

                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {

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
                            }
                            Log.d("uuuuuuw",viewF1+" "+viewF6+" "+viewF1Y);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        else if(category.equals("Course"))
        {

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SubscriptionCourseViews").child(id);
            databaseReference.child("Individual")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                if(model.getCategory().equals("Routine"))
                                {
                                    viewIR=model.getViews();
                                }
                                else if(model.getCategory().equals("PAYL"))
                                {
                                    viewIPAYL=model.getViews();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            databaseReference.child("Course")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

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

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            databaseReference.child("Full")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {

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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


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

    @Override
    public void onBackPressed() {

        if(fullScreen)
        {
            FullScreen();
        }

        ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        View NavBar = getActivity().findViewById(R.id.nav_view);
        NavBar.setVisibility(View.VISIBLE);

        tt = getActivity().findViewById(R.id.tt);
        tt.setVisibility(View.VISIBLE);

        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        releasePlayer();
    }

    public long getCurrentPosition() {
        if (player!=null) {


            return player.getCurrentPosition();
        }
        else{
            return 0;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("pos",getCurrentPosition());
    }


}
