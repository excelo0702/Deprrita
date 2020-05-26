package com.example.chhots.ChatBox;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder>{


    private Context context;
    private List<MessageModel> list;
    private String TAG = "MessageAdapter";
    FirebaseUser user;

    public MessageAdapter(Context context, List<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType == 0)
        {
            view = LayoutInflater.from(context).inflate(R.layout.raw_chat_item_right,parent,false);

        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.raw_chat_item_left,parent,false);

        }
        return new MyHolder(view);
    }

    public void setData(List<MessageModel>list)
    {
        this.list=list;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MessageModel model = list.get(position);
        Log.d(TAG,"vbn");
        if(model.getFlag()==0)
        {
            holder.message.setText(model.getMessage());
            holder.message_date.setText(model.getTime());
            holder.playerView.getLayoutParams().height=0;
            holder.playerView.getLayoutParams().width = 0;
        }
        else
        {
            holder.message_left.setText(model.getMessage());
            holder.message_left_date.setText(model.getTime());

            holder.playerView.getLayoutParams().height=0;
            holder.playerView.getLayoutParams().width = 0;

        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public TextView message_left,message,message_left_date,message_date;
        public PlayerView playerView;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"fghjkk");
            message = itemView.findViewById(R.id.show_message);
            message_left = itemView.findViewById(R.id.show_message_left);
            message_left_date = itemView.findViewById(R.id.show_message__left_date);
            message_date = itemView.findViewById(R.id.show_message_date);
            playerView = itemView.findViewById(R.id.video_view_chat);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getFlag()==0)
        {
            //sender
            return 0;
        }
        else
        {
            //reciever
            return 1;
        }

    }
}
