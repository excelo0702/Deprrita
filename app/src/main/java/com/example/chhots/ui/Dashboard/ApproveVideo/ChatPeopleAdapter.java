package com.example.chhots.ui.Dashboard.ApproveVideo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.NotificationBadgeModel;
import com.example.chhots.R;

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
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        NotificationBadgeModel notif = new NotificationBadgeModel();
        if(notif!=null) {
            HashMap<String, Integer> mm = notif.getNotification_chatlist();

            if(mm!=null) {
                Log.d("Notification Chat", mm.size() + "");

                if (mm.size() > 0 && mm.get(list.get(position).getUserId()) > 0) {
                    holder.chatlist_notification.setText(mm.get(list.get(position).getUserId()));
                } else {
                    holder.chatlist_notification.setVisibility(GONE);
                }
            }
        }
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
