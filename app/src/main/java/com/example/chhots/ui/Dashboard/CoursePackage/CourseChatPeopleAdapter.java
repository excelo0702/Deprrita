package com.example.chhots.ui.Dashboard.CoursePackage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.NotificationNumberModel;
import com.example.chhots.R;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleAdapter;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.View.GONE;

public class CourseChatPeopleAdapter extends RecyclerView.Adapter<CourseChatPeopleAdapter.MyView>{


    private List<ChatPeopleModel> list;
    private Context context;
    private String courseId;
    private DatabaseReference db;
    private FirebaseUser user;
    int flag=1;


    public CourseChatPeopleAdapter(List<ChatPeopleModel> list, Context context, String courseId) {
        this.list = list;
        this.context = context;
        this.courseId = courseId;
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
        db.child("dashboard").child("ApproveVideo").child("Routine").child(courseId).child(list.get(position).getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NotificationNumberModel model = dataSnapshot.getValue(NotificationNumberModel.class);
                        if(model!=null && model.getI()!=0) {
                            Log.d("count_notify", dataSnapshot.getValue() + "");
                            holder.chatlist_notification.setVisibility(View.VISIBLE);
                            holder.chatlist_notification.setText(String.valueOf(model.getI()));
                        }
                        else if(model!=null && model.getI()==0)
                        {
                            holder.chatlist_notification.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        holder.name.setText(list.get(position).getUserName());
//        Picasso.get().load(Uri.parse(list.get(position).getuserImageurl())).into(holder.image);
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
                    intent.putExtra("routineId",courseId);
                    intent.putExtra("peopleId",peopleId);

                    chatlist_notification.setVisibility(GONE);
                    context.startActivity(intent);
                }
            });



        }
    }


}
