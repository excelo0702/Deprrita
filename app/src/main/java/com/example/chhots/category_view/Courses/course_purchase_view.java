package com.example.chhots.category_view.Courses;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chhots.LoadingDialog;
import com.example.chhots.PaymentListener;
import com.example.chhots.R;
import com.example.chhots.UserClass;
import com.example.chhots.Models.PreviewModel;
import com.example.chhots.category_view.routine.routine_view;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Subscription.subscription;
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
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */

public class course_purchase_view extends Fragment implements onBackPressed {


    public course_purchase_view() {
        // Required empty public constructor
    }

    private PaymentListener listener;


    private TextView learn,description;
    private Button seeCourse;

    String courseId="p",userId,instructorId,thumbnail;
    private CourseThumbnail mode;

    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;


    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;
    private Uri videouri;
    LoadingDialog loadingDialog;
    Switch sw1;
    int flag=1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_course_purchase_view, container, false);
        init(view);
        Bundle bundle = getArguments();
        courseId = bundle.getString("courseId");
        thumbnail = bundle.getString("thumbnail");
        instructorId = bundle.getString("instructorId");
        loadingDialog = new LoadingDialog(getActivity());

        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fetchCourse();
        fetchCoursePreview();

        seeCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
                seeCourse.setEnabled(false);
                checkSubscription();
            }
        });

        return view;
    }

    private void fetchCoursePreview() {
        mDatabaseReference.child(getResources().getString(R.string.CoursePreview)).child(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        PreviewModel model = dataSnapshot.getValue(PreviewModel.class);
                        videouri = Uri.parse(model.getUrl());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initializePlayer();
                            }
                        },1000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void fetchCourse() {
        mDatabaseReference.child(getResources().getString(R.string.CoursesThumbnail)).child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mode = dataSnapshot.getValue(CourseThumbnail.class);
                description.setText(mode.getDescription());
                learn.setText(mode.getLearn());
                thumbnail = mode.getCourseImage();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void routine(String cat,String planplan){

        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        mDatabaseReference.child("CoursesThumbnail").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mode = dataSnapshot.getValue(CourseThumbnail.class);
                CourseThumbnail model = new CourseThumbnail(mode.getCourseName(),mode.getCourseId(),mode.getCourseImage(),mode.getInstructorId(),mode.getViews(),mode.getRating(),mode.getTrending(),mode.getDate(),mode.getCategory(),mode.getLearn(),mode.getDescription(),System.currentTimeMillis()+"");
                mode.setViews(mode.getViews()+1);
                int k = dataDifference(date,mode.getDate());
                mode.setTrending((double)((1.0*mode.getViews())/k));
                mDatabaseReference.child("CourseHistory").child(user.getUid()).child(courseId).setValue(model);
                mDatabaseReference.child("CoursesThumbnail").child(courseId).setValue(mode);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("popop","popop112");
        Fragment fragment = new routine_view();
        Bundle bundle = new Bundle();
        bundle.putString("category", "Course");
        bundle.putString("routineId", courseId);
        bundle.putString("thumbnail",thumbnail);
        loadingDialog.DismissDialog();
        bundle.putString("cat",cat);
        bundle.putString("planplan",planplan);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void coursePurchase()
    {
        Log.d("popop","popop111");
        loadingDialog.DismissDialog();
        Fragment fragment = new subscription();
        Bundle bundle = new Bundle();
        bundle.putString("category", "Course");
        bundle.putString("Id", courseId);
        bundle.putString("thumbnail", thumbnail);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public int checkSubscription()
    {
        //kon sa subscription h uske pas

        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //      mDatabaseReference.child("USER_PURCHASED_ROUTINES").child(userId).child("Individual").child()

        mDatabaseReference.child("FreeCourse").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        if(model!=null)
                        {
                            int k = dataDifference(date,model.getDate());
                            if(k<=3)
                            {
                                routine("Course","1month");
                                return;
                            }
                            else
                            {
                                mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                UserClass model = dataSnapshot.getValue(UserClass.class);

                                                //find category
                                                if (model != null) {
                                                    int k = dataDifference(date, model.getDate());
                                                    if (model.getCategory().equals("1month")) {
                                                        if (k > 30) {
                                                            //expired
                                                            flag=0;
                                                        } else {
                                                            //go to routine view
                                                            routine("Course","1month");
                                                            return;
                                                        }
                                                    } else if (model.getCategory().equals("6month")) {
                                                        if (k > 180) {
                                                            flag=0;
                                                        } else {
                                                            routine("Course","6month");
                                                            return;
                                                        }
                                                    } else if (model.getCategory().equals("1year")) {
                                                        if (k > 365) {
                                                            flag=0;
                                                        } else {
                                                            routine("Course","1year");
                                                            return;
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    //check for full subscription
                                                    mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    UserClass model = dataSnapshot.getValue(UserClass.class);
                                                                    Log.d("popop",dataSnapshot.getValue()+"");
                                                                    //find category
                                                                    if (model != null) {
                                                                        int k = dataDifference(date, model.getDate());
                                                                        if(model.getCategory().equals("3day"))
                                                                        {
                                                                            if (k > 3) {
                                                                                flag=0;
                                                                                return;
                                                                            } else {
                                                                                routine("Full","3day");
                                                                                return;
                                                                            }
                                                                        }
                                                                        else if (model.getCategory().equals("1month")) {
                                                                            if (k > 30) {
                                                                                flag=0;
                                                                                return;
                                                                            } else {
                                                                                routine("Full","1month");
                                                                                return;
                                                                            }
                                                                        } else if (model.getCategory().equals("6month")) {
                                                                            if (k > 180) {
                                                                                flag=0;
                                                                            } else {
                                                                                routine("Full","6month");

                                                                                return;
                                                                            }
                                                                        } else if (model.getCategory().equals("1year")) {
                                                                            if (k > 365) {
                                                                                flag=0;
                                                                            } else {
                                                                                routine("Full","1year");
                                                                                return;
                                                                            }
                                                                        }
                                                                    }

                                                                    //check individual subscription
                                                                    mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Individual")
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    UserClass model = dataSnapshot.getValue(UserClass.class);
                                                                                    if(model!=null)
                                                                                    {
                                                                                        if(model.getId().equals(courseId))
                                                                                        {
                                                                                            int k = dataDifference(date,model.getDate());
                                                                                            if(k>5)
                                                                                            {
                                                                                                flag=0;
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                routine("Individual","Course");
                                                                                                return;
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        coursePurchase();
                                                                                        return;
                                                                                    }
                                                                                    if (flag==0)
                                                                                    {
                                                                                        coursePurchase();
                                                                                        return;
                                                                                    }
                                                                                }
                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                                            });
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                            });
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                                        });

                            }
                        }
                        else
                        {
                            mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserClass model = dataSnapshot.getValue(UserClass.class);

                                            //find category
                                            if (model != null) {
                                                int k = dataDifference(date, model.getDate());
                                                if (model.getCategory().equals("1month")) {
                                                    if (k > 30) {
                                                        //expired
                                                        flag=0;
                                                    } else {
                                                        //go to routine view
                                                        routine("Course","1month");
                                                        return;
                                                    }
                                                } else if (model.getCategory().equals("6month")) {
                                                    if (k > 180) {
                                                        flag=0;
                                                    } else {
                                                        routine("Course","6month");
                                                        return;
                                                    }
                                                } else if (model.getCategory().equals("1year")) {
                                                    if (k > 365) {
                                                        flag=0;
                                                    } else {
                                                        routine("Course","1year");
                                                        return;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                //check for full subscription
                                                mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                UserClass model = dataSnapshot.getValue(UserClass.class);
                                                                Log.d("popop",dataSnapshot.getValue()+"");
                                                                //find category
                                                                if (model != null) {
                                                                    int k = dataDifference(date, model.getDate());
                                                                    if(model.getCategory().equals("3day"))
                                                                    {
                                                                        if (k > 3) {
                                                                            flag=0;
                                                                            return;
                                                                        } else {
                                                                            routine("Full","3day");
                                                                            return;
                                                                        }
                                                                    }
                                                                    else if (model.getCategory().equals("1month")) {
                                                                        if (k > 30) {
                                                                            flag=0;
                                                                            return;
                                                                        } else {
                                                                            routine("Full","1month");
                                                                            return;
                                                                        }
                                                                    } else if (model.getCategory().equals("6month")) {
                                                                        if (k > 180) {
                                                                            flag=0;
                                                                        } else {
                                                                            routine("Full","6month");

                                                                            return;
                                                                        }
                                                                    } else if (model.getCategory().equals("1year")) {
                                                                        if (k > 365) {
                                                                            flag=0;
                                                                        } else {
                                                                            routine("Full","1year");
                                                                            return;
                                                                        }
                                                                    }
                                                                }

                                                                //check individual subscription
                                                                mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Individual")
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                UserClass model = dataSnapshot.getValue(UserClass.class);
                                                                                if(model!=null)
                                                                                {
                                                                                    if(model.getId().equals(courseId))
                                                                                    {
                                                                                        int k = dataDifference(date,model.getDate());
                                                                                        if(k>5)
                                                                                        {
                                                                                            flag=0;
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            routine("Individual","Course");
                                                                                            return;
                                                                                        }

                                                                                    }
                                                                                }
                                                                                else
                                                                                {
                                                                                    coursePurchase();
                                                                                    return;
                                                                                }
                                                                                if (flag==0)
                                                                                {
                                                                                    coursePurchase();
                                                                                    return;
                                                                                }
                                                                            }
                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                                        });
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                        });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        return 0;
    }


    public int dataDifference(String date1,String date2)
    {
        int k=0;

        String sy1 = date1.substring(0,4);
        String sy2 = date2.substring(0,4);

        int y = 365*(Integer.parseInt(sy1)-Integer.parseInt(sy2));


        String sm1 = date1.substring(5,7);
        String sm2 = date2.substring(5,7);
        int m = 30*(Integer.parseInt(sm1)-Integer.parseInt(sm2));

        String sd1 = date1.substring(8,10);
        String sd2 =  date2.substring(8,10);
        int d = Integer.parseInt(sd1)-Integer.parseInt(sd2);
        k = y+m+d+1;
        return k;
    }

    private void initializePlayer() {

        if (getContext()!=null) {

            player = ExoPlayerFactory.newSimpleInstance(getContext());

            playerView.setPlayer(player);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);


            MediaSource mediaSource = buildMediaSource(videouri);

            player.setPlayWhenReady(playWhenReady);

            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            //  player.seekTo(currentWindow, playbackPosition);
            player.prepare(mediaSource, false, false);
        }

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

    private void init(View view) {

        learn = view.findViewById(R.id.what);
        description = view.findViewById(R.id.course_description_purchase);
        seeCourse = view.findViewById(R.id.see_course_preview);


        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        playerView = view.findViewById(R.id.course_preview_video_purchase);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        fullScreenButton.setVisibility(GONE);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        sw1 = playerView.findViewById(R.id.mirror);
        sw1.setVisibility(GONE);
    }


    @Override
    public void onBackPressed() {

    }
}