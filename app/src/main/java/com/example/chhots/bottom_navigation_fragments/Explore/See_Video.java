package com.example.chhots.bottom_navigation_fragments.Explore;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.LoadingDialog;
import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.example.chhots.UserInfoModel;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.InstructorInfoModel;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.Dashboard.HistoryPackage.HistoryModel;
import com.example.chhots.ui.Dashboard.PointModel;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class See_Video extends Fragment implements onBackPressed {


    public TextView title,upvote,downvote,comments,share,views,chat,addToBookmark;
    public ImageView favorite_icon,share_icon,user_photo;
    public ImageView bookmark ;
    public ImageView upvote_icon,downvote_icon;
    private ImageView send_comment,camera_comment;
    private MediaController mediaController;
    FirebaseAuth auth;
    private DatabaseReference mDatabaseRef,databaseReference;
    private static final String TAG = "See_Video";
    private FloatingActionButton chatBtn;
    private String instructorId;
    VideoModel current;
    EditText comment_message;
    private Uri videouri;
    CommentAdapter adapter;

    RecyclerView recyclerView;
    private String videoId;
    FirebaseUser user;

    List<CommentModel> list;
    String htitle,hvideoName,hthumbnail;
    InstructorInfoModel usermodel;
    int k=0,points=0;
    String s="";



    //exoplayer implementation
    RelativeLayout tt;
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;
    Switch sw1;

    PointModel modelWeekly,modelOverAll;
    String userVideoId="";
    LoadingDialog loadingDialog;


    public See_Video() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see__video, container, false);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        title = view.findViewById(R.id.video_adapter_title_see);
        upvote = view.findViewById(R.id.video_likess_see);
        views = view.findViewById(R.id.video_views_see);
        upvote_icon = view.findViewById(R.id.video_likes_see);
        downvote_icon = view.findViewById(R.id.video_downvote_see);
        share_icon = view.findViewById(R.id.video_share_see);
        bookmark = view.findViewById(R.id.bookmark_icon);
        auth = FirebaseAuth.getInstance();
        addToBookmark = view.findViewById(R.id.AddtoBookmars);
        user_photo = view.findViewById(R.id.instructor_see_video_photo);
        send_comment = view.findViewById(R.id.send_comment);
        camera_comment = view.findViewById(R.id.select_image_content);
        comment_message = view.findViewById(R.id.comment_message);

        user = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));





        playerView = view.findViewById(R.id.video_view_item_see);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(5,0,5,0);
        sw1 = playerView.findViewById(R.id.mirror);
        sw1.setVisibility(GONE);


        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FullScreen();
                //      Intent intent = new Intent(getContext(), FloatingWidgetService.class);
                //    intent.putExtra("videoUri",videouri.toString());
                //  getActivity().startService(intent);

            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("");
        databaseReference = FirebaseDatabase.getInstance().getReference("");
        mDatabaseRef.child(getResources().getString(R.string.InstructorInfo)).child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usermodel = dataSnapshot.getValue(InstructorInfoModel.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        Bundle bundle = this.getArguments();
        videoId = bundle.getString("videoId");


        upvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseRef.child(getResources().getString(R.string.VIDEOS)).child(videoId).child(getResources().getString(R.string.like)).setValue(String.valueOf(Integer.parseInt(upvote.getText().toString())+1));
                current.setLike(String.valueOf(Integer.parseInt(current.getLike())+1));

                increasePoints(5);

                //enable downvote and disable upvote
                upvote.setText(String.valueOf(Integer.parseInt(upvote.getText().toString())+1));
                upvote_icon.setEnabled(false);
                upvote_icon.setVisibility(GONE);
                downvote_icon.setVisibility(VISIBLE);
                downvote_icon.setEnabled(true);
                downvote_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumbs_up_afterlike,null));
                mDatabaseRef.child("FAVORITE").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mDatabaseRef.child(getResources().getString(R.string.FAVORITE)).child(auth.getCurrentUser().getUid()).child(videoId).setValue(current);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        downvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseRef.child(getResources().getString(R.string.VIDEOS)).child(videoId).child(getResources().getString(R.string.like)).setValue(String.valueOf(Integer.parseInt(upvote.getText().toString())-1));
                current.setLike(String.valueOf(Integer.parseInt(current.getLike())-1));
                //enable downvote and disable upvote

                increasePoints(-5);
                upvote_icon.setEnabled(true);
                downvote_icon.setEnabled(false);
                upvote_icon.setVisibility(VISIBLE);
                downvote_icon.setVisibility(GONE);
                downvote_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up,null));
                upvote_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up,null));
                upvote.setText(String.valueOf(Integer.parseInt(upvote.getText().toString())-1));

                mDatabaseRef.child(getResources().getString(R.string.FAVORITE)).child(user.getUid()).child(videoId).removeValue();
                Toast.makeText(getContext(),"Remove From Liked Videos",Toast.LENGTH_SHORT).show();
            }
        });

        addToBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBookmark();
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBookmark();
            }
        });

        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });
        showVideo();
        checkBookmark();
        return view;
    }

    public void checkBookmark(){
        mDatabaseRef.child(getResources().getString(R.string.Bookmarks)).child(user.getUid()).child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0){

                }
                else
                {
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_afterbookmark,null));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void AddBookmark() {
        mDatabaseRef.child(getResources().getString(R.string.Bookmarks)).child(user.getUid()).child(videoId).setValue(current);
        Toast.makeText(getContext(),"Added to My Bookmarks",Toast.LENGTH_SHORT).show();
        bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_afterbookmark,null));

    }

    private void increasePoints(int n)
    {
        if(modelOverAll!=null) {
            modelWeekly.setPoints(modelWeekly.getPoints() + n);
            modelOverAll.setPoints(modelOverAll.getPoints() + n);
            databaseReference.child(getResources().getString(R.string.UsersPoint)).child(getResources().getString(R.string.Weekly)).child(userVideoId).setValue(modelWeekly);
            databaseReference.child(getResources().getString(R.string.UsersPoint)).child(getResources().getString(R.string.OverAll)).child(userVideoId).setValue(modelOverAll);
        }
    }

    private void checkVideoLike()
    {
        mDatabaseRef.child(getResources().getString(R.string.FAVORITE)).child(user.getUid()).child(videoId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0)
                        {
                            downvote_icon.setVisibility(GONE);
                            upvote_icon.setVisibility(VISIBLE);
                            upvote_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up,null));
                            k++;
                            if(k==2)
                            {
                                loadingDialog.DismissDialog();
                            }
                        }
                        else
                        {
                            k++;
                            if(k==2)
                            {
                                loadingDialog.DismissDialog();
                            }
                            upvote_icon.setVisibility(GONE);
                            downvote_icon.setVisibility(VISIBLE);
                            downvote_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumbs_up_afterlike,null));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkVideoHistory()
    {
        mDatabaseRef.child("VideoHistory").child(user.getUid()).child(videoId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0)
                        {
                            PointModel popo = new PointModel(userVideoId,"",1);
                            mDatabaseRef.child(getResources().getString(R.string.VideoHistory)).child(user.getUid()).child(videoId).setValue(popo);
                            increasePoints(10);
                            current.setView(String.valueOf(Integer.parseInt(current.getView())+1));
                            mDatabaseRef.child(getResources().getString(R.string.VIDEOS)).child(videoId).child(getResources().getString(R.string.view)).setValue(current.getView());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){ }
                });
    }

    private void fetchUserPoints() {
        if(userVideoId!=null) {
            databaseReference.child(getResources().getString(R.string.UsersPoint)).child(getResources().getString(R.string.Weekly)).child(userVideoId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            modelWeekly = dataSnapshot.getValue(PointModel.class);
                            if (modelWeekly != null) {
                                points = modelWeekly.getPoints();
                            }
                            databaseReference.child(getResources().getString(R.string.UsersPoint)).child(getResources().getString(R.string.OverAll)).child(userVideoId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            modelOverAll = dataSnapshot.getValue(PointModel.class);
                                            if (modelOverAll != null) {
                                                points = modelOverAll.getPoints();
                                            }
                                            checkVideoHistory();
                                            checkVideoLike();
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

    private void userInfo()
    {
        if(userVideoId!=null)
        {
            mDatabaseRef.child(getResources().getString(R.string.UserInfo)).child(userVideoId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfoModel model = dataSnapshot.getValue(UserInfoModel.class);
                    Picasso.get().load(Uri.parse(model.getUserImageurl())).into(user_photo);
                    k++;
                    if(k==2)
                    {
                        loadingDialog.DismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void instructorInfo()
    {
        if(userVideoId!=null)
        {
            mDatabaseRef.child(getResources().getString(R.string.InstructorInfo)).child(userVideoId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                    Picasso.get().load(Uri.parse(model.getUserImageurl())).into(user_photo);
                    k++;
                    if(k==2)
                    {
                        loadingDialog.DismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void pushHistory() {
        String time = System.currentTimeMillis()+"";
        Log.d(TAG,htitle+hvideoName);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        HistoryModel model = new HistoryModel(htitle,hvideoName,hthumbnail,date,"Explore",videoId);
        mDatabaseRef.child(getResources().getString(R.string.HISTORY)).child(user.getUid()).child(time).setValue(model);
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
            BottomnavBar.setVisibility(VISIBLE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(VISIBLE);
            playerView.setBackgroundColor(Color.parseColor("#000000"));

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(VISIBLE);

            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(VISIBLE);


            title.setVisibility(VISIBLE);
            upvote.setVisibility(VISIBLE);
            upvote_icon.setVisibility(VISIBLE);
            downvote_icon.setVisibility(VISIBLE);
            share_icon.setVisibility(VISIBLE);
            favorite_icon.setVisibility(VISIBLE);
            send_comment.setVisibility(VISIBLE);
            camera_comment.setVisibility(VISIBLE);
            comment_message.setVisibility(VISIBLE);
            fullScreen = false;
        }
        else{
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
            params.width = params.MATCH_PARENT;
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
            title.setVisibility(GONE);
            upvote.setVisibility(GONE);
            upvote_icon.setVisibility(GONE);
            downvote_icon.setVisibility(GONE);
            share_icon.setVisibility(GONE);
            favorite_icon.setVisibility(GONE);
            send_comment.setVisibility(GONE);
            camera_comment.setVisibility(GONE);
            comment_message.setVisibility(GONE);

            fullScreen = true;
        }
    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(videouri);
        player.setPlayWhenReady(playWhenReady);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        //  player.seekTo(currentWindow, playbackPosition);
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
            if(player!=null){
                player.setPlayWhenReady(false);
            }        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            if(player!=null){
                player.setPlayWhenReady(false);
            }
            //   releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT <= 23) {

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

    private void showVideo() {
        mDatabaseRef.child("VIDEOS").child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0)
                    return;
                else {
                    current = dataSnapshot.getValue(VideoModel.class);
                    instructorId = current.getUser();
                    htitle = current.getTitle();
                    hvideoName = current.getTitle();
                    userVideoId = current.getUser();
                    title.setText(current.getTitle());
                    upvote.setText(String.valueOf(current.getLike()));
                    views.setText(String.valueOf(current.getView()));
                    videouri = (Uri.parse(current.getUrl()));
                    hthumbnail = current.getThumbnail();
                    views.setText(current.getView());
                    Log.d("pop pop id ", userVideoId);
                    if(current.getPrice().equals("1"))
                    {
                        instructorInfo();
                    }
                    else
                    {
                        userInfo();
                    }
                    pushHistory();
                    initializePlayer();
                    fetchUserPoints();

                    showComments();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showComments() {
        Query query =mDatabaseRef.child("COMMENTS").child(videoId).orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG,ds.getValue().toString());
                    CommentModel model = ds.getValue(CommentModel.class);
                    list.add(model);
                }
                Collections.reverse(list);
                adapter = new CommentAdapter(getContext(),list,userVideoId,videoId);
                recyclerView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendComment()
    {        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String time = System.currentTimeMillis()+"";
        CommentModel model = new CommentModel(comment_message.getText().toString(),date,user.getUid(),usermodel.getUserImageurl(),usermodel.getUserName(),time);
        mDatabaseRef.child("COMMENTS").child(videoId).child(time).setValue(model);
        comment_message.setText("");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null) {
            final MenuItem activitySearchMenu = menu.findItem(R.id.action_search);
            activitySearchMenu.setVisible(false);
        }
    }


    @Override
    public void onBackPressed() {

        if(fullScreen)
        {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();


            View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
            BottomnavBar.setVisibility(VISIBLE);

            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(VISIBLE);
            playerView.setBackgroundColor(Color.parseColor("#000000"));

            tt = getActivity().findViewById(R.id.tt);
            tt.setVisibility(VISIBLE);

            View NavBar = getActivity().findViewById(R.id.nav_view);
            NavBar.setVisibility(VISIBLE);
            fullScreen = false;

        }
        else
        {
            releasePlayer();
        }
    }

}