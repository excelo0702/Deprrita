package com.example.chhots.bottom_navigation_fragments.Explore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.UserInfoModel;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.InstructorInfoModel;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.ui.Dashboard.dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private List<ExploreVideoModel> localVideoTracks;
    private Context context;
    private String activity;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public VideoAdapter(List<ExploreVideoModel> localVideoTracks, Context context, String activity) {
        this.localVideoTracks = localVideoTracks;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routine_item, parent,false);

        return new VideoViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
       /* ExploreVideoModel current = localVideoTracks.get(position);
        holder.title.setText(current.getTitle());
        holder.upvote.setText(String.valueOf(current.getLike()));
        holder.views.setText(String.valueOf(current.getView()));
        holder.value = current.getVideoId();
        holder.sub_category = current.getSub_category();
        holder.thumbnail = current.getThumbnail();
        holder.userId = current.getUser();
        Picasso.get().load(Uri.parse(current.getThumbnail())).into(holder.videoview);
        Log.d(TAG,current.getTitle()+"  "+current.getUrl());
        if(current.getPrice().equals("1"))
        {
            holder.instructorInfo();
        }
        else
        {
            holder.fetchUserInfo();
        }
        holder.videoview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });*/
    }

    public void setData(List<ExploreVideoModel> localVideoTracks)
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
    public class VideoViewHolder extends RecyclerView.ViewHolder{

        public TextView title,upvote,comments,share,views,userNameName;
        public ImageView upvote_icon,comment_icon,share_icon;
        public ImageView videoview,userImagePhoto,three_dot;
        String value,isSusbscribed,isPurchased,sub_category,thumbnail,userId,userName,userImage;

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
            userNameName = view.findViewById(R.id.video_username);
            userImagePhoto = view.findViewById(R.id.user_explore_image);
            three_dot = itemView.findViewById(R.id.three_dot1_bookmarks);



            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

            if(activity.equals("MyBookmarks")|| activity.equals("MyExplore"))
            {
                three_dot.setVisibility(View.VISIBLE);
            }
            else
            {
                three_dot.setVisibility(View.GONE);
            }

            three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(three_dot);
                }
            });



            userNameName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new dashboard();
                    Bundle bundle = new Bundle();
                    bundle.putString("category","UserView");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            userImagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new dashboard();
                    Bundle bundle = new Bundle();
                    bundle.putString("category","UserView");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


            videoview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (user == null) {
                        Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);

                    } else {

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


        private void instructorInfo()
        {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(context.getResources().getString(R.string.InstructorInfo)).child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                    if(model!=null)
                    {
                        userName = model.getUserName();
                        userImage = model.getUserImageurl();
                        userNameName.setText(userName);
                        Picasso.get().load(Uri.parse(userImage)).into(userImagePhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }


        private void fetchUserInfo()
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("UserInfo").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfoModel model = dataSnapshot.getValue(UserInfoModel.class);
                    Log.d("VideoAdapter ll ",dataSnapshot.getValue()+"");
                    if(model!=null)
                    {
                        userName = model.getUserName();
                        userImage = model.getUserImageurl();
                        userNameName.setText(userName);
                        Picasso.get().load(Uri.parse(userImage)).into(userImagePhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        private void showPopupMenu(View view)
        {
            PopupMenu popup = new PopupMenu(context, view, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();

            inflater.inflate(R.menu.delete_menu, popup.getMenu());

            MenuItem item = popup.getMenu().findItem(R.id.chat_with_user);
            item.setVisible(false);

            //set menu item click listener here
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(getAdapterPosition()));
            popup.show();
        }

        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
            int position;

            /**
             * @param position
             */
            MyMenuItemClickListener(int position) {

                this.position = position;
            }


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.delete:

                        if(activity.equals("MyBookmarks")) {


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Bookmarks").child(user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            localVideoTracks.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, localVideoTracks.size());
                                            Toast.makeText(context, "Deleted ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else
                        {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("VIDEOS").child(value).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            localVideoTracks.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, localVideoTracks.size());
                                            Toast.makeText(context, "Deleted ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        return true;

                }
                return false;
            }

        }

    }


}

