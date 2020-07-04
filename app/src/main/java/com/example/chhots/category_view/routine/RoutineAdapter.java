package com.example.chhots.category_view.routine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.chhots.InstructorLogin;
import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.example.chhots.SubscriptionModel;
import com.example.chhots.UserClass;
import com.example.chhots.bottom_navigation_fragments.Explore.See_Video;
import com.example.chhots.ui.Dashboard.PointModel;
import com.example.chhots.ui.Subscription.subscription;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineView>{


    public RoutineAdapter() {
    }

    private List<RoutineThumbnailModel> list;
    private Context context;
    private String activity;
    private final String TAG = "RoutineAdapter1";
    int selected=-1;
    int points=0;
    int flag=0;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public RoutineAdapter(List<RoutineThumbnailModel> list, Context context, String activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RoutineView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routine_new_item,parent,false);
        return new RoutineView(view);
    }

    public void setData(List<RoutineThumbnailModel> list)
    {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineView holder, int position) {

        holder.title.setText(list.get(position).getTitle());
        holder.instructor_name.setText(list.get(position).getInstructor_name());
        holder.level.setText(list.get(position).getRoutine_level());
        holder.routine_views.setText(list.get(position).getRoutine_views());
        holder.routineId = list.get(position).getRoutineId();
        Picasso.get().load(Uri.parse(list.get(position).routineThumbnail)).placeholder(R.drawable.smurfoo_dp).into(holder.routine_view_image);
        holder.thumbnail = list.get(position).getRoutineThumbnail();
        holder.instructorId = list.get(position).getInstructorId();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class RoutineView extends RecyclerView.ViewHolder{

        TextView title,instructor_name,routine_views,level;
        ImageView routine_view_image;
        String routineId,thumbnail,userId,instructorId;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;
        LoadingDialog loadingDialog;

        public RoutineView(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.routine_view_title);
            instructor_name = itemView.findViewById(R.id.routine_instructor_name);
            routine_view_image = itemView.findViewById(R.id.routine_view_item);
            routine_views = itemView.findViewById(R.id.routine_views);
            level = itemView.findViewById(R.id.routine_level);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            userId = user.getUid();
            loadingDialog = new LoadingDialog(((AppCompatActivity) context));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, InstructorLogin.class);
                        context.startActivity(intent);
                    }
                    else
                        routinePurchase();

                }
            });


        }


        public void routinePurchase(){

            Fragment fragment = new routine_purchase();
            Bundle bundle = new Bundle();
            bundle.putString("routineId",routineId);
            bundle.putString("userId",instructorId);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }

}
