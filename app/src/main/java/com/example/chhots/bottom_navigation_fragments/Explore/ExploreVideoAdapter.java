package com.example.chhots.bottom_navigation_fragments.Explore;

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
import com.example.chhots.Models.ExploreVideoModel;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.R;
import com.example.chhots.UserInfoModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExploreVideoAdapter extends RecyclerView.Adapter<ExploreVideoAdapter.ExploreVideoHolder>{

    private static final String TAG = "ExploreVideoAdapterTAG";
    private List<ExploreVideoModel> list;
    private Context context;

    public ExploreVideoAdapter(List<ExploreVideoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ExploreVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routine_item, parent,false);

        return new ExploreVideoHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ExploreVideoHolder holder, int i) {
        ExploreVideoModel current = list.get(i);
        holder.videoTitle.setText(current.getTitle());
        holder.numberOfViews.setText(current.getNumberOfViews()+"");
        Picasso.get().load(Uri.parse(current.getThumbnailLink())).into(holder.videoThumbnail);
        holder.videoUserId = current.getUser();
        holder.videoId = current.getVideoId();
        holder.fetchUserInfo();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<ExploreVideoModel> list){
        this.list = list;
    }

    public class ExploreVideoHolder extends RecyclerView.ViewHolder{

        public ImageView videoUserProfilePhoto,three_dot,videoThumbnail;
        public TextView videoUserName,videoTitle,numberOfViews;
        public String videoUserId,videoId;

        public ExploreVideoHolder(@NonNull View itemView) {
            super(itemView);
            init(itemView);

            videoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (videoUserId == null) {
                        Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);

                    } else {

                        Fragment fragment = new See_Video();
                        Bundle bundle = new Bundle();
                        bundle.putString(context.getString(R.string.videoId), videoId);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });

        }

        private void init(View itemView) {
            videoUserProfilePhoto = itemView.findViewById(R.id.user_explore_image);
            three_dot = itemView.findViewById(R.id.three_dot1_bookmarks);
            three_dot.setVisibility(View.GONE);
            videoUserName = itemView.findViewById(R.id.video_username);
            videoTitle = itemView.findViewById(R.id.video_adapter_title);
            videoThumbnail = itemView.findViewById(R.id.video_view_item);
            numberOfViews = itemView.findViewById(R.id.video_views);


        }

        private void fetchUserInfo(){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(context.getString(R.string.UserInfo)).child(videoUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfoModel model = dataSnapshot.getValue(UserInfoModel.class);
                    Log.d("VideoAdapter ll ",dataSnapshot.getValue()+"");
                    if(model!=null)
                    {
                        videoUserName.setText(model.getUserName());
                        Picasso.get().load(Uri.parse(model.getUserImageurl())).into(videoUserProfilePhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            databaseReference.child(context.getString(R.string.InstructorInfo)).child(videoUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                    Log.d("VideoAdapter ll ",dataSnapshot.getValue()+"");
                    if(model!=null)
                    {
                        videoUserName.setText(model.getUserName());
                        Picasso.get().load(Uri.parse(model.getUserImageurl())).into(videoUserProfilePhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        }

    }

}
