package com.example.chhots.ui.Dashboard.ApproveVideo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.example.chhots.category_view.routine.routine_view;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.View.GONE;

public class ApproveVideoAdapter extends RecyclerView.Adapter<ApproveVideoAdapter.ApproveVideoHolder>{


    public ApproveVideoAdapter() {
    }
    private List<MyRoutineModel> list;
    private Context context;
    private final String TAG = "ApproveVideoAdapter1";

    public ApproveVideoAdapter(List<MyRoutineModel> list, Context context) {
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
        holder.userId = user.getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("NotificationNumber").child(user.getUid()).child("dashboard").child("ApproveVideo").child("Routine");
        db.child(list.get(position).getRoutineId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = (int)dataSnapshot.getChildrenCount();
                if(k>0)
                {
                    Log.d(">>>>",k+"");
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
        holder.title.setText(list.get(position).getRoutineName());
        Picasso.get().load(Uri.parse(list.get(position).getRoutineThumbnail())).into(holder.thumbnail);
        holder.routineId = list.get(position).getRoutineId();
        holder.imageURL = list.get(position).getRoutineThumbnail();
        holder.money.setText(list.get(position).getMoney());
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

    }

    public void setData(List<MyRoutineModel> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ApproveVideoHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail,three_dot;
        TextView title,notify,routineView,money;
        String routineId,imageURL,userId;
        RelativeLayout relativeLayout;
        public ApproveVideoHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.routine_image);
            title = itemView.findViewById(R.id.raw_routine_name);
            notify = itemView.findViewById(R.id.raw_routine_notification);
            relativeLayout = itemView.findViewById(R.id.routine_raw_view);
            routineView = itemView.findViewById(R.id.raw_routine_view);
            money = itemView.findViewById(R.id.raw_routine_earn_money);
            three_dot = itemView.findViewById(R.id.three_dot);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        }
            });

            three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(three_dot);
                }
            });



            routineView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new routine_view();
                    Bundle bundle = new Bundle();
                    bundle.putString("category", "Routine");
                    bundle.putString("routineId", routineId);
                    bundle.putString("cat","Routine");
                    bundle.putString("planplan","1month");
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
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
                        databaseReference.child("ROUTINE_THUMBNAIL").child(routineId).removeValue()
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
                        Fragment fragment = new ChatPeopleList();
                        Bundle bundle = new Bundle();
                        bundle.putString("routineId",routineId);
                        bundle.putString("instructorId",userId);
                        Log.d(TAG,routineId+"  p "+userId);
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
