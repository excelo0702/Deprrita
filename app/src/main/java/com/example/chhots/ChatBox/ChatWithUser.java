package com.example.chhots.ChatBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatWithUser extends AppCompatActivity {




    EditText message;
    ImageView send_message,peopleImage;
    TextView peopleName;
    private String people_id,routineId;
    private String userId;
    private String TAG = "ChatWithInstructor12345";
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ImageView send_video;
    private List<MessageModel> list;
    private MessageAdapter adapter;

    String instructorImage,instructorName;
    String userImage,userName;

    FirebaseAuth auth;
    FirebaseUser user;


    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_user);


        Intent intent = getIntent();
        people_id = intent.getStringExtra("peopleId");
        routineId = intent.getStringExtra("routineId");
        send_message = findViewById(R.id.send_message1);
        message = findViewById(R.id.message1);
        databaseReference = FirebaseDatabase.getInstance().getReference("");
        recyclerView = findViewById(R.id.recycler_chat_view1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        send_video = findViewById(R.id.send_video_chat1);
        adapter = new MessageAdapter(ChatWithUser.this,list);

        apiService = Client.getClient("https:/fcm.googleapis.com/").create(APIService.class);


        showMessage();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        updateToken(token);
                    }
                });

        send_message.setOnClickListener(new View.OnClickListener() {
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

    private void showMessage() {
        databaseReference.child(getString(R.string.CHAT)).child("Instructor").child(userId).child(routineId).child(people_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        Log.d(TAG+" qq ",dataSnapshot.getValue()+"");

                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            Log.d(TAG+" pp ",ds.getValue()+"");
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


    private void sendVideo() {
    }

    public void sendMessage()
    {
        String mess = message.getText().toString();
        String time = System.currentTimeMillis()+"";
        MessageModel model = new MessageModel(mess,time,0,"");

            //instructor is sending a message
            databaseReference.child(getString(R.string.CHAT)).child("Instructor").child(userId).child(routineId).child(people_id).child(time).setValue(model);
            model.setFlag(1);
            databaseReference.child("CHAT").child("Users").child(people_id).child(routineId).child(time).setValue(model);

        sendNotification(people_id,userName,mess);


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
                    Data data = new Data(people_id,R.mipmap.ic_icon,userName+": "+message,"New Message",userId);

                    Sender sender = new Sender(data,token1.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200)
                                    {
                                        if(response.body().Success != 1)
                                        {
                                            Toast.makeText(ChatWithUser.this,"Failed User",Toast.LENGTH_SHORT).show();
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




    private void updateToken(String token)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1);
    }

}
