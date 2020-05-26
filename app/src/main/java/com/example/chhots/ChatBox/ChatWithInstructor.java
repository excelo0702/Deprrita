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

import com.example.chhots.Notificatios.Client;
import com.example.chhots.Notificatios.Data;
import com.example.chhots.Notificatios.MyResponse;
import com.example.chhots.Notificatios.Sender;
import com.example.chhots.Notificatios.Token;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    APIService apiService;

    boolean notify = false;


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


        peopleImage = findViewById(R.id.people_profile_image);
        peopleName = findViewById(R.id.people_profile_name);

        apiService = Client.getClient("https:/fcm.googleapis.com/").create(APIService.class);


            routineId = intent.getStringExtra("routineId");
            peopleId = intent.getStringExtra("peopleId");

        fetchInstructorInfo();
        fetchPeopleInfo();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        updateToken(token);
                    }
                });

        showMessage();
        send_message.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
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

            sendNotification(peopleId,userName,mess);


            message.setText("");




    }

    private void sendNotification(String receiver, final String userName, final String message)
    {
        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Token token1 = ds.getValue(Token.class);
                    Data data = new Data(instructor_id,R.mipmap.ic_icon,userName+": "+message,"New Message",userId);

                    Sender sender = new Sender(data,token1.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200)
                                    {
                                        if(response.body().Success != 1)
                                        {
                                            Toast.makeText(ChatWithInstructor.this,"Failed Instructor",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    }


    private void updateToken(String token)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1);
    }


}
