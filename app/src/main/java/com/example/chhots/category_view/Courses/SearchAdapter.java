package com.example.chhots.category_view.Courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.chhots.category_view.routine.routine_view;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    public SearchAdapter() {
    }

    private List<CourseThumbnail> list;
    private Context context;
    private final String TAG = "CoursesSearchAdapter";

    public SearchAdapter(List<CourseThumbnail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search_item,parent,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.courseName.setText(list.get(position).getCourseName());
        holder.courseId = list.get(position).getCourseId();
        holder.thumbnail = list.get(position).getCourseImage();
        holder.instructorId = list.get(position).getInstructorId();
    }


    public void setData(List<CourseThumbnail> list)
    {
        this.list = list;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        int p = 1;

        // Text View
        TextView courseName;
        String courseId,instructorId,thumbnail;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;
        LoadingDialog loadingDialog;


        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);


            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            loadingDialog = new LoadingDialog(((AppCompatActivity) context));

            courseName = itemView.findViewById(R.id.search_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                    else
                    {
                        //        p = checkSubscription();
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
            fragmentTransaction.commit();
        }


    }

}