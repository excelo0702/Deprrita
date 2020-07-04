package com.example.chhots.ChatBox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder>{


    private Context context;
    private List<MessageModel> list;
    private String TAG = "MessageAdapter";
    private OnItemClickListener listener;
    FirebaseUser user;

    public MessageAdapter(Context context, List<MessageModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
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
        holder.bind(list.get(position),listener);
        MessageModel model = list.get(position);
        Log.d(TAG,"vbn");
        if(model.getFlag()==0)
        {

            if(model.getVideo().equals("video"))
            {
                holder.playerView.getLayoutParams().height=700;
                holder.playerView.getLayoutParams().width = 550;
                holder.videouri = Uri.parse(model.getMessage());
                holder.message.setText("");
                holder.time.setText(model.getTime());
                holder.url = model.getMessage();
                holder.initializePlayer();
            }
            else
            {
                holder.message.setText(model.getMessage());
                holder.time.setText(model.getTime());
                holder.playerView.getLayoutParams().height=0;
                holder.playerView.getLayoutParams().width = 0;
            }
        }

        else
        {
            if(model.getVideo().equals("video"))
            {
                holder.playerView.getLayoutParams().height=700;
                holder.playerView.getLayoutParams().width = 550;
                holder.videouri = Uri.parse(model.getMessage());
                holder.message_left.setText("");
                holder.time_left.setText(model.getTime());
                holder.url = model.getMessage();
                holder.initializePlayer();
            }
            else
            {
                holder.message_left.setText(model.getMessage());
                holder.time_left.setText(model.getTime());
                holder.playerView.getLayoutParams().height=0;
                holder.playerView.getLayoutParams().width = 0;
            }

        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public TextView message_left,message,time_left,time;
        public PlayerView playerView;
        SimpleExoPlayer player;
        private boolean playWhenReady = false;
        Uri videouri;
        String url;

        boolean fullScreen = false;
        private int currentWindow = 0;
        private long playbackPosition = 0;
        ImageView fullScreenButton;
        Switch sw1;



        public MyHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"fghjkk");
            message = itemView.findViewById(R.id.show_message);
            message_left = itemView.findViewById(R.id.show_message_left);
            playerView = itemView.findViewById(R.id.video_view_chat);
            time = itemView.findViewById(R.id.show_message_time);
            time_left = itemView.findViewById(R.id.show_message_left_date);

            playerView = itemView.findViewById(R.id.video_view_chat);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPadding(0,0,0,0);
            playerView.setBackgroundColor(Color.parseColor("#000000"));
            fullScreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

            fullScreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,exoPlayerFullScreen.class);
                    intent.putExtra("videoUrl",url);
                    context.startActivity(intent);
                }
            });

        }


        public void bind(final MessageModel model,final OnItemClickListener listener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
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


        private void initializePlayer() {

            player = ExoPlayerFactory.newSimpleInstance(context);
            playerView.setPlayer(player);

            MediaSource mediaSource = buildMediaSource(videouri);

            player.setPlayWhenReady(playWhenReady);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            //  player.seekTo(currentWindow, playbackPosition);
            player.prepare(mediaSource, false, false);
        }

        private MediaSource buildMediaSource(Uri uri) {
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(context, "exoplayer-codelab");
            return new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
        }

        @SuppressLint("InlinedApi")
        private void hideSystemUi() {
            playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
    public interface OnItemClickListener{
        void onItemClick(MessageModel model);
    }

}
