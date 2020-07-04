package com.example.chhots.ChatBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chhots.NotificationNumberModel;
import com.example.chhots.R;
import com.example.chhots.UserInfoModel;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.InstructorInfoModel;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleModel;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.view.View.GONE;

public class ChatWithInstructor extends AppCompatActivity {



    EditText message;

    ImageView send_message,peopleImage;
    TextView peopleName,text_video;
    private String instructor_id,routineId,category,peopleId;
    private String userId;
    private String TAG = "ChatWithInstructor12345";
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ImageView send_video;
    private List<MessageModel> list;
    private MessageAdapter adapter;

    String instructorImage,instructorName;
    String userImage,userName,localtime;

    FirebaseAuth auth;
    FirebaseUser user;

    private Uri videouri;


    ProgressBar progressBar;
    PopupWindow mPopupWindow;
    private RelativeLayout relativeLayout;
    RelativeLayout tt;
    PlayerView playerView;
    SimpleExoPlayer player;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;
    ImageView fullScreenButton;
    boolean fullScreen = false;
    Switch sw1;



    //Notification

    private RequestQueue mRequestQueue;
    private String URL="https://fcm.googleapis.com/fcm/send";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_instructor);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        send_message = findViewById(R.id.send_message);
        message = findViewById(R.id.message);
        databaseReference = FirebaseDatabase.getInstance().getReference("");
        recyclerView = findViewById(R.id.recycler_chat_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        send_video = findViewById(R.id.send_video_chat);

        progressBar = findViewById(R.id.progress_bar_chat_video);
        text_video = findViewById(R.id.text_video);
        relativeLayout = findViewById(R.id.chat_screen);
        adapter = new MessageAdapter(ChatWithInstructor.this, list, new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessageModel model) {
                Toast.makeText(getApplicationContext(),model.getTime(),Toast.LENGTH_SHORT).show();
            }
        });

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



        peopleImage = findViewById(R.id.instructor_profile_image);
        peopleName = findViewById(R.id.instructor_profile_name);



        routineId = intent.getStringExtra("routineId");
        peopleId = intent.getStringExtra("peopleId");


        mRequestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic(userId);

        fetchInstructorInfo();
        fetchPeopleInfo();
        setNotificationNumberUser();
        recyclerView.scrollToPosition(list.size()-1);

        showMessage();
        send_message.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        send_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVideo();
            }
        });
    }

    private void setNotificationNumberUser() {

        databaseReference.child("NotificationNumber").child(user.getUid()).child("dashboard").child("ApproveVideo").child("Routine").child(routineId).child(peopleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NotificationNumberModel model = dataSnapshot.getValue(NotificationNumberModel.class);

                        if(model!=null) {
                            Log.d("count_notify", dataSnapshot.getValue() + "");
                            model.setI(0);
                            databaseReference.child("NotificationNumber").child(user.getUid()).child("dashboard").child("ApproveVideo").child("Routine").child(routineId).child(peopleId).setValue(model);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    private void sendNotification(String message) {
  /*      //json object
        {
            "to": "topics/topic name"
                notification:   {
                    title: "some titlle"
                     body:  "some body"
                }
        }*/

        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to","/topics/"+peopleId);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",instructorName);
            notificationObj.put("body",message);

            JSONObject extraData = new JSONObject();
            extraData.put("category","Chat");
            extraData.put("peopleId",userId);
            extraData.put("routineId",routineId);


            mainObj.put("notification",notificationObj);
            mainObj.put("data",extraData);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAk4aOdMQ:APA91bGIGSc4-YEFm1lkWu5fiP9Cg8NRT0hC4Jwkg4yhn2GkGQH4uD-FNoDMkDW8Hl_pULwRfj7EFMLW--qnTIH6WUNG7_ZkH9_6Z-Mo6ATU30SErTZJNYe7K69AsljvTxhVavn1XW56");
                    return header;
                }
            };

            mRequestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void fetchPeopleInfo() {

        databaseReference.child("UserInfo").child(peopleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //TODO:maybe we have to create UserInfoModel

                UserInfoModel model = dataSnapshot.getValue(UserInfoModel.class);
                peopleName.setText(model.getUserName());
                Picasso.get().load(Uri.parse(model.getUserImageurl())).into(peopleImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("UserInfo").child(peopleId).keepSynced(true);
    }

    private void fetchInstructorInfo()
    {
        databaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                instructorName = model.getUserName();
                instructorImage = model.getUserImageurl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid()).keepSynced(true);
    }

    private void sendVideo() {
        chooseVideo();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            videouri = data.getData();
            showVideoUtil();

        }
    }

    private void showVideoUtil() {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.exo_player_popup_window,null);


        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
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
        ImageView send = customView.findViewById(R.id.send_video);
        ImageView close = customView.findViewById(R.id.close_video_screen);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });


        playerView = customView.findViewById(R.id.full_video_popup);
        fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPadding(5,0,5,0);
        sw1 = playerView.findViewById(R.id.mirror);
        sw1.setVisibility(GONE);
        fullScreenButton.setVisibility(GONE);
        initializePlayer();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_video.setEnabled(false);
       //         sendVideoFull();
                mPopupWindow.dismiss();
            }
        });


    }

    private String getfilterExt(Uri videoUri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri));
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void setNotificationNumber(){ }

    public void sendMessage()
    {
        String mess = message.getText().toString();
        String time = System.currentTimeMillis()+"";

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        localtime = date.format(currentLocalTime);
        MessageModel model = new MessageModel(mess,localtime,0,"");



            //instructor is sending a message
            model.setFlag(0);
            databaseReference.child("CHAT").child("Instructor").child(userId).child(routineId).child(peopleId).child(time).setValue(model);
            model.setFlag(1);
            databaseReference.child("CHAT").child("Users").child(peopleId).child(routineId).child(time).setValue(model);


            message.setText("");
            recyclerView.scrollToPosition(list.size()-1);

            sendNotification(mess);
    }

    public void showMessage()
    {
        Log.d(TAG,"show");

            databaseReference.child("CHAT").child("Instructor").child(userId).child(routineId).child(peopleId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                Log.d(TAG,ds.getValue().toString());
                                MessageModel model = ds.getValue(MessageModel.class);
                                list.add(model);
                            }
                            adapter.setData(list);
                            recyclerView.setAdapter(adapter);
                            recyclerView.smoothScrollToPosition(list.size()-1);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        databaseReference.child("CHAT").child("Instructor").child(userId).child(routineId).child(peopleId).keepSynced(true);

    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext());
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(videouri);
        player.setPlayWhenReady(playWhenReady);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        //  player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getApplicationContext(), "exoplayer-codelab");
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

}
