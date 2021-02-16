package com.example.chhots.ui.Dashboard.CoursePackage;

import android.app.VoiceInteractor;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.routine_view;
import com.example.chhots.ui.Dashboard.ApproveVideo.ApproveVideoAdapter;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleList;
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

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder>{

    private List<MyCourseModel> list;
    private Context context;

    public MyCourseAdapter(List<MyCourseModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_mycourse_item,parent,false);
        return new MyCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyCourseViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.userId = user.getUid();
        holder.Notification.setVisibility(View.GONE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("NotificationNumber").child(user.getUid()).child("dashboard").child("ApproveVideo").child("Routine");
        db.child(list.get(position).getCourseId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = (int)dataSnapshot.getChildrenCount();
                if(k>0)
                {
                    holder.Notification.setText(k+"");
                    holder.Notification.setTextColor(Color.GREEN);
                    holder.Notification.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.Notification.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.course_name.setText(list.get(position).getCourseName());
        Picasso.get().load(Uri.parse(list.get(position).getCourseThumbnail())).into(holder.img);
        holder.course_earned_money.setText(list.get(position).getMoney());
        holder.courseId = list.get(position).getCourseId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<MyCourseModel> list)
    {
        this.list = list;
    }

    public class MyCourseViewHolder extends RecyclerView.ViewHolder{

        ImageView img,three_dot;
        TextView course_name,course_earned_money,watchCourse;
        TextView Notification;
        String courseId,userId;

        public MyCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.course_image);
            course_name = itemView.findViewById(R.id.course_name);
            course_earned_money = itemView.findViewById(R.id.raw_course_earn_money);
            Notification = itemView.findViewById(R.id.raw_course_notification);
            three_dot = itemView.findViewById(R.id.three_dot1_course);

            watchCourse = itemView.findViewById(R.id.watch_course);

            three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(three_dot);
                }
            });

            watchCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("popop","popop112");
                    Fragment fragment = new routine_view();
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "Course");
                    bundle.putString("routineId", courseId);
                    bundle.putString("cat","Course");
                    bundle.putString("planplan","1month");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        private void showPopupMenu(View view)
        {
            PopupMenu popup = new PopupMenu(context, view, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();

            inflater.inflate(R.menu.delete_menu, popup.getMenu());

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

            /**
             * Click listener for popup menu items
             */

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("CoursesThumbnail").child(courseId).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,list.size());
                                        Toast.makeText(context,"Deleted ",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Failed ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    case R.id.chat_with_user:
                        Fragment fragment = new CourseChatList();
                        Bundle bundle = new Bundle();
                        bundle.putString("courseId",courseId);
                        bundle.putString("userId",userId);

                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.dashboard_layout,fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();



                }
                return false;
            }
        }



    }
}
