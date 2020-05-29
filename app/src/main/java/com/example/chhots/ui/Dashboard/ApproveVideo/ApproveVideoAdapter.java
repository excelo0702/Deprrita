package com.example.chhots.ui.Dashboard.ApproveVideo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
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

public class ApproveVideoAdapter extends RecyclerView.Adapter<ApproveVideoAdapter.ApproveVideoHolder>{


    public ApproveVideoAdapter() {
    }
    private List<RoutineThumbnailModel> list;
    private Context context;
    private final String TAG = "ApproveVideoAdapter1";

    public ApproveVideoAdapter(List<RoutineThumbnailModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ApproveVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_approve_video_item,parent,false);
        return new ApproveVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ApproveVideoHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("NotificationNumber").child(user.getUid()).child("dashboard").child("ApproveVideo").child("Routine");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = (int)dataSnapshot.getChildrenCount();
                if(k>0)
                {

                    holder.notify.setText(String.valueOf(k));
                    holder.notify.setTextColor(Color.GREEN);
                    holder.notify.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.notify.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.title.setText(list.get(position).getTitle());
        Picasso.get().load(Uri.parse(list.get(position).getRoutineThumbnail())).into(holder.thumbnail);
        holder.routineId = list.get(position).getRoutineId();
        holder.instructorId=list.get(position).getInstructorId();
    }

    public void setData(List<RoutineThumbnailModel> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ApproveVideoHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView title,notify;
        String routineId,instructorId;
        public ApproveVideoHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.routine_thumbnail);
            title = itemView.findViewById(R.id.routine_title);
            notify = itemView.findViewById(R.id.routine_title_notification);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new ChatPeopleList();
                    Bundle bundle = new Bundle();
                    bundle.putString("routineId",routineId);
                    bundle.putString("instructorId",instructorId);
                    Log.d(TAG,routineId+"  p "+instructorId);

                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.dashboard_layout,fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


        }
    }
}
