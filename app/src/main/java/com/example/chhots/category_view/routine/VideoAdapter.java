package com.example.chhots.category_view.routine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.See_Video;
import com.example.chhots.SubscriptionModel;
import com.example.chhots.UserClass;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


/*
 * RecyclerView adapter that renders local video tracks to a VideoView and TextView.
 */


public class VideoAdapter extends
        RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private static final String TAG = "VideoViewRecAdapter";
    private  OnItemClickListener mListener;

    private List<VideoModel> localVideoTracks;
    private Context context;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public VideoAdapter(List<VideoModel> localVideoTracks, Context context) {
        this.localVideoTracks = localVideoTracks;
        this.context = context;
    }

    @Override
    public void onViewAttachedToWindow(VideoViewHolder holder) {
        Log.d(TAG, "onViewAttachedToWindow");
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VideoViewHolder holder) {
        Log.d(TAG, "onViewDetachedFromWindow");
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(VideoViewHolder holder) {
        Log.d(TAG, "onViewRecycled");
        super.onViewRecycled(holder);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(context)
                .inflate(R.layout.raw_routine_item, null);

        return new VideoViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        VideoModel current = localVideoTracks.get(position);
        holder.title.setText(current.getTitle());
        holder.upvote.setText(String.valueOf(current.getLike()));
        holder.views.setText(String.valueOf(current.getView()));
        holder.value = current.getVideoId();
        holder.sub_category = current.getSub_category();
        holder.thumbnail = current.getThumbnail();
        holder.userId = current.getUser();
        Picasso.get().load(Uri.parse(current.getThumbnail())).into(holder.videoview);
        Log.d(TAG,current.getTitle()+"  "+current.getUrl());
    }


    public String getLastItemVideoId(){
        return localVideoTracks.get(localVideoTracks.size()-1).getVideoId();
    }

    public void setData(List<VideoModel> localVideoTracks)
    {
        this.localVideoTracks =localVideoTracks;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return localVideoTracks.size();
    }
    /*
     * View holder that hosts the video view proxy and a text view
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {

        public TextView title,upvote,comments,share,views;
        public ImageView upvote_icon,comment_icon,share_icon;
        public ImageView videoview;
        String value,isSusbscribed,isPurchased,sub_category,thumbnail,userId;

        FirebaseUser user;
        DatabaseReference mDatabaseReference;


        VideoViewHolder(View view, final OnItemClickListener listener) {
            super(view);

            title = view.findViewById(R.id.video_adapter_title);
            upvote = view.findViewById(R.id.video_likess);
            share = view.findViewById(R.id.video_sharee);
            comments = view.findViewById(R.id.video_commentt);
            views = view.findViewById(R.id.video_views);
            upvote_icon = view.findViewById(R.id.video_likes);
            share_icon = view.findViewById(R.id.video_share);
            comment_icon = view.findViewById(R.id.video_comment);
            videoview = view.findViewById(R.id.video_view_item);


            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            videoview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);

                    }
                    else {

                        int p = 1;
                        if (sub_category.equals("ROUTINE")) {
                            // TODO: check subscription of user
                            p = checkSubscription();
                            Log.d(TAG, p + " p ");
                            if (p == 0) {
                                p = checkPurchased();
                                Log.d(TAG, p + " q ");
                            }
                        }
                        if (p == 1) {
                            Fragment fragment = new See_Video();
                            Bundle bundle = new Bundle();
                            bundle.putString("videoId", value);
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.drawer_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        } else {
                            //Create Pop up to Buy this Video
                            //Redirect to routine_purchase
                            Fragment fragment = new routine_purchase();
                            Bundle bundle = new Bundle();
                            bundle.putString("videoId", value);
                            bundle.putString("thumbnail", thumbnail);
                            bundle.putString("instructorId", userId);
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.drawer_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        }
                    }

                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });

        }

        public int checkSubscription()
        {
            Log.d(TAG," pqq ");
            final int[] flag = new int[1];
            mDatabaseReference.child("SUBSCRIPTION").child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                Log.d(TAG,ds.getValue()+"");

                                SubscriptionModel model = ds.getValue(SubscriptionModel.class);
                                if(model.getVideoId().equals(value))
                                {
                                    Log.d(TAG," pqq ");
                                    flag[0] =1;
                                    return;
                                }

                            }
                            if(flag[0]==1)
                            {
                                Fragment fragment = new See_Video();
                                Bundle bundle = new Bundle();
                                bundle.putString("videoId", value);
                                fragment.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.drawer_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            if(flag[0]==1)
                return 1;


            return 0;
        }

        public int checkPurchased()
        {
            Log.d(TAG," pqq ");
            final int[] flag = new int[1];
            mDatabaseReference.child("USERS").child(user.getUid()).child("videos")
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG,dataSnapshot.getValue()+"");

                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                Log.d(TAG,ds.getValue()+"");

                                UserClass model = ds.getValue(UserClass.class);
                                if(model.getVideoId().equals(value))
                                {
                                    Log.d(TAG," peee ");
                                    flag[0] =1;
                                    Log.d(TAG,flag[0]+" oo ");
                                }
                            }
                            if(flag[0]==1) {
                                Fragment fragment = new See_Video();
                                Bundle bundle = new Bundle();
                                bundle.putString("videoId", value);
                                fragment.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.drawer_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            //TODO: handler for wait

            return 0;
        }


    }
}
