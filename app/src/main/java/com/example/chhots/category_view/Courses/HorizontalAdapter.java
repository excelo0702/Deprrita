package com.example.chhots.category_view.Courses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.chhots.InstructorLogin;
import com.example.chhots.LoadingDialog;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.See_Video;
import com.example.chhots.SubscriptionModel;
import com.example.chhots.UserClass;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.category_view.routine.routine_view;
import com.example.chhots.ui.Dashboard.PointModel;
import com.example.chhots.ui.Subscription.subscription;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyView> {

    private List<CourseThumbnail> list;
    private Context context;
    private final String TAG = "HorizontalAdapter";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CourseThumbnail model;
    int a1=0,a2=0,a3=0;
    int points=0;
    int flag=0;

    public HorizontalAdapter(List<CourseThumbnail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HorizontalAdapter.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_courses_item,parent,false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapter.MyView holder, int position) {
        holder.CourseName.setText(list.get(position).getCourseName());
        holder.CourseCategory.setText(list.get(position).getCourseName());
        Picasso.get().load(Uri.parse(list.get(position).getCourseImage())).into(holder.image);
        holder.courseId = list.get(position).getCourseId();
        holder.thumbnail = list.get(position).getCourseImage();
        holder.instructorId = list.get(position).getInstructorId();
        Log.d(TAG,"fghjkk");

    }

    public void setData(List<CourseThumbnail> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {


        Log.d(TAG+" ppp ",list.size()+"");

        return list.size();
    }

    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView CourseName,CourseCategory;
        ImageView image;
        String courseId,instructorId,thumbnail;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;
        LoadingDialog loadingDialog;

        public MyView(View view)
        {
            super(view);
            image = view.findViewById(R.id.raw_image_course);
            CourseName =view.findViewById(R.id.raw_course_name);
            CourseCategory = view.findViewById(R.id.raw_dance_form);

            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            loadingDialog = new LoadingDialog(((AppCompatActivity) context));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                    coursePurchase();
                }
            });
        }

        public void coursePurchase()
        {
            Log.d("popop","popop111");
            Fragment fragment = new course_purchase_view();
            Bundle bundle = new Bundle();
            bundle.putString("instructorId", instructorId);
            bundle.putString("courseId", courseId);
            bundle.putString("thumbnail", thumbnail);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }





    }
}
