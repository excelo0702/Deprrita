package com.example.chhots.category_view.routine;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.chhots.Models.PreviewModel;
import com.example.chhots.R;
import com.example.chhots.UserClass;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class routine_purchase extends Fragment implements onBackPressed {


    public routine_purchase() {
        // Required empty public constructor
    }

    private TextView title,description,category;
    private ImageView routineThumbnail,userImage;
    private Button buy_now;
    String routineId,userId,instructorId,thumbnail;
    private RoutineThumbnailModel mode;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private int pos;
    //exoplayer implementation
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private long currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;
    private Uri videouri;
    Switch sw1;
    LoadingDialog loadingDialog;
    private Bundle savedState;
    int flag=1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null)
        {
            routineId = savedInstanceState.getString("routineId");
            instructorId = savedInstanceState.getString("userId");
            playbackPosition = savedInstanceState.getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_purchase, container, false);

        playerView = view.findViewById(R.id.routine_preview_video_purchase);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        description = view.findViewById(R.id.routine_description_purchase);
        category = view.findViewById(R.id.category_purchase);

        Bundle bundle = this.getArguments();
        routineId = bundle.getString("routineId");
        instructorId = bundle.getString("userId");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        fetchRoutine();
        fetchRoutinePreview();
        buy_now = view.findViewById(R.id.routine_buy_now);
        sw1 = playerView.findViewById(R.id.mirror);
        sw1.setVisibility(View.GONE);
        fullScreenButton.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(getActivity());

        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
                buy_now.setEnabled(false);
                checkSubscription();
            }
        });
        return view;
    }

    private void fetchRoutinePreview() {
        mDatabaseReference.child("ROUTINE_PREVIEWS").child(routineId)
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

    private void fetchRoutine() {
        mDatabaseReference.child(getString(R.string.RoutineThumbnail)).child(routineId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mode = dataSnapshot.getValue(RoutineThumbnailModel.class);
                description.setText(mode.getRoutineDescription());
                thumbnail = mode.getRoutineThumbnail();
                category.setText(mode.getLearn());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public long getCurrentPosition() {
        if(player!=null)
        {
            return player.getCurrentPosition();
        }
        else
        {
            return 0;
        }
    }

    private void initializePlayer() {

        if(getContext()!=null) {


            player = ExoPlayerFactory.newSimpleInstance(getContext());

            playerView.setPlayer(player);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);


            MediaSource mediaSource = buildMediaSource(videouri);

            player.setPlayWhenReady(playWhenReady);

            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            player.seekTo((int) currentWindow, playbackPosition);
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

    public void subscription()
    {
        releasePlayer();
        loadingDialog.DismissDialog();
        Fragment fragment = new subscription();
        Bundle bundl = new Bundle();
        bundl.putString("category","Routine");
        bundl.putString("Id",routineId);
        bundl.putString("thumbnail",thumbnail);
        fragment.setArguments(bundl);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void routineView()
    {
        releasePlayer();
        loadingDialog.DismissDialog();
        Log.d("popop","popop12");
        Fragment fragment = new routine_view();
        Bundle bundle = new Bundle();
        bundle.putString("thumbnail",thumbnail);
        bundle.putString("category", "Routine");
        bundle.putString("routineId", routineId);
        bundle.putString("cat","Routine");
        bundle.putString("planplan","1month");
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

        mDatabaseReference.child("FreeRoutine").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        if(model!=null)
                        {
                            int k = dateDifference(date,model.getDate());
                            if(k<=3)
                            {
                                routineView();
                                flag=1;
                                return;
                            }
                            else
                            {
                                mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                UserClass model = dataSnapshot.getValue(UserClass.class);

                                                if (model != null) {
                                                    int k = dateDifference(date, model.getDate());
                                                    if (model.getCategory().equals("3day")) {
                                                        if (k > 3) {
                                                            flag=0;
                                                        } else {
                                                            //go to routine view
                                                            Log.d("pop pop ",k+" date ");
                                                            routineView();
                                                            return;
                                                        }
                                                    }
                                                    else if (model.getCategory().equals("1month")) {
                                                        if (k > 30) {
                                                            //expired
                                                            //      subscription();
                                                            flag=0;

                                                        } else {
                                                            //go to routine view
                                                            routineView();
                                                            return;
                                                        }
                                                    } else if (model.getCategory().equals("6month")) {
                                                        if (k > 180) {
                                                            flag=0;
                                                            //  subscription();

                                                        } else {
                                                            routineView();
                                                            return;
                                                        }
                                                    } else if (model.getCategory().equals("1year")) {
                                                        if (k > 365) {
                                                            flag=0;
                                                            //subscription();


                                                        } else {
                                                            routineView();
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
                                                                    if(model.getId().equals(routineId))
                                                                    {
                                                                        int k = dateDifference(date,model.getDate());
                                                                        if(k>5)
                                                                        {
                                                                            flag=0;
                                                                            //   subscription();

                                                                        }
                                                                        else
                                                                        {
                                                                            routineView();
                                                                            return;
                                                                        }

                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    subscription();
                                                                    return;
                                                                }
                                                                if(flag==0)
                                                                {
                                                                    subscription();
                                                                    return;
                                                                }

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
                        else
                        {
                            mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Routine")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserClass model = dataSnapshot.getValue(UserClass.class);

                                            //find category
                                            if (model != null) {
                                                int k = dateDifference(date, model.getDate());
                                                if (model.getCategory().equals("1month")) {

                                                    if (k > 30) {
                                                        //expired
                                                        flag=0;
                                                        //    subscription();

                                                    } else {
                                                        //go to routine view
                                                        routineView();
                                                        flag=1;
                                                        return;
                                                    }
                                                } else if (model.getCategory().equals("6month")) {
                                                    if (k > 180) {
                                                        flag=0;
                                                        //     subscription();

                                                    } else {
                                                        routineView();
                                                        return;
                                                    }
                                                } else if (model.getCategory().equals("1year")) {
                                                    if (k > 365) {
                                                        flag=0;
                                                        //   subscription();

                                                    } else {
                                                        routineView();
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

                                                                if (model != null) {
                                                                    int k = dateDifference(date, model.getDate());
                                                                    if (model.getCategory().equals("3day")) {
                                                                        if (k > 3) {
                                                                            flag=0;
                                                                        } else {
                                                                            //go to routine view
                                                                            Log.d("pop pop ",k+" date ");
                                                                            routineView();
                                                                            return;
                                                                        }
                                                                    }
                                                                    else if (model.getCategory().equals("1month")) {
                                                                        if (k > 30) {
                                                                            //expired
                                                                            //      subscription();
                                                                            flag=0;

                                                                        } else {
                                                                            //go to routine view
                                                                            routineView();
                                                                            return;
                                                                        }
                                                                    } else if (model.getCategory().equals("6month")) {
                                                                        if (k > 180) {
                                                                            flag=0;
                                                                            //  subscription();

                                                                        } else {
                                                                            routineView();
                                                                            return;
                                                                        }
                                                                    } else if (model.getCategory().equals("1year")) {
                                                                        if (k > 365) {
                                                                            flag=0;
                                                                            //subscription();


                                                                        } else {
                                                                            routineView();
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
                                                                                    if(model.getId().equals(routineId))
                                                                                    {
                                                                                        int k = dateDifference(date,model.getDate());
                                                                                        if(k>5)
                                                                                        {
                                                                                            flag=0;
                                                                                            //   subscription();

                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            routineView();
                                                                                            return;
                                                                                        }

                                                                                    }
                                                                                }
                                                                                else
                                                                                {
                                                                                    subscription();
                                                                                    return;
                                                                                }
                                                                                if(flag==0)
                                                                                {
                                                                                    subscription();
                                                                                    return;
                                                                                }

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

    private int dateDifference(String date, String date1) {

        int k=0;

        String sy1 = date.substring(0,4);
        String sy2 = date1.substring(0,4);

        int y = 365*(Integer.parseInt(sy1)-Integer.parseInt(sy2));


        String sm1 = date.substring(5,7);
        String sm2 = date1.substring(5,7);
        int m = 30*(Integer.parseInt(sm1)-Integer.parseInt(sm2));

        String sd1 = date.substring(8,10);
        String sd2 =  date1.substring(8,10);
        int d = Integer.parseInt(sd1)-Integer.parseInt(sd2);
        k = y+m+d+1;
        return k;        }

    @Override
    public void onBackPressed()
    {
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putLong("position",getCurrentPosition());
        outState.putString("routineId",routineId);
        outState.putString("userId",instructorId);

    }

}

