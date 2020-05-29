package com.example.chhots.ui.Dashboard.ApproveVideo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.MyFirebaseMessagingService;
import com.example.chhots.NotificationBadgeModel;
import com.example.chhots.NotificationNumberModel;
import com.example.chhots.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class ChatPeopleAdapter extends RecyclerView.Adapter<ChatPeopleAdapter.MyView>{


    public ChatPeopleAdapter() {
    }

    private List<ChatPeopleModel> list;
    private Context context;
    private String routineId;
    private DatabaseReference db;
    private FirebaseUser user;
    int flag=1;

    public ChatPeopleAdapter(List<ChatPeopleModel> list, Context context,String routineId) {
        this.list = list;
        this.context = context;
        this.routineId = routineId;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_chat_people_list,parent,false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {

        holder.chatlist_notification.setVisibility(GONE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("NotificationNumber").child(user.getUid());
        db.child("dashboard").child("ApproveVideo").child("Routine").child(routineId).child(list.get(position).getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NotificationNumberModel model = dataSnapshot.getValue(NotificationNumberModel.class);

                            if(model!=null) {
                                Log.d("count_notify", dataSnapshot.getValue() + "");
                                holder.chatlist_notification.setVisibility(View.VISIBLE);
                                holder.chatlist_notification.setText(String.valueOf(model.getI()));
                                dataSnapshot.getRef().removeValue();
                            }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });


        holder.name.setText(list.get(position).getUserName());
   //     Picasso.get().load(Uri.parse(list.get(position).getuserImageurl())).into(holder.image);
        holder.peopleId = list.get(position).getUserId();
    }

    public void setData(List<ChatPeopleModel> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView name,chatlist_notification;
        String peopleId;

        public MyView(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.people_profile);
            name = itemView.findViewById(R.id.people_name);
            chatlist_notification = itemView.findViewById(R.id.chatlist_notification);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatWithInstructor.class);
                    intent.putExtra("category","INSTRUCTOR");
                    intent.putExtra("routineId",routineId);
                    intent.putExtra("peopleId",peopleId);

                    chatlist_notification.setVisibility(GONE);
                    context.startActivity(intent);
                }
            });


        }
    }
}
