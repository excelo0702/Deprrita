package com.example.chhots.ChatBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chhots.NotificationBadgeModel;
import com.example.chhots.R;
import com.example.chhots.InstructorInfoModel;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
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

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.GONE;

public class ChatWithInstructor extends AppCompatActivity {



    EditText message;

    ImageView send_message,peopleImage;
    TextView peopleName;
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
        adapter = new MessageAdapter(ChatWithInstructor.this,list);

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

        NotificationBadgeModel notif = new NotificationBadgeModel();
        if(notif!=null) {
            HashMap<String, Integer> mm = notif.getNotification_chatlist();

            if(mm!=null) {
                Log.d("Notification Chat", mm.size() + "");
                mm.put(peopleId,0);
            }
        }

        peopleImage = findViewById(R.id.people_profile_image);
        peopleName = findViewById(R.id.people_profile_name);



            routineId = intent.getStringExtra("routineId");
            peopleId = intent.getStringExtra("peopleId");


        mRequestQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic(userId);

        fetchInstructorInfo();
        fetchPeopleInfo();


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
                    Toast.makeText(getApplicationContext(),"sent",Toast.LENGTH_SHORT).show();

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

                InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
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
    }

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
            sendNotification(mess);


            message.setText("");




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
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        databaseReference.child("CHAT").child("Instructor").child(userId).child(routineId).child(peopleId).keepSynced(true);

    }




}
