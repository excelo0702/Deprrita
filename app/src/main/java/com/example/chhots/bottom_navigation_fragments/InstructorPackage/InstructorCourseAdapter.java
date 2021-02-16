package com.example.chhots.bottom_navigation_fragments.InstructorPackage;

import android.content.Context;
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
import com.example.chhots.category_view.Courses.CourseThumbnail;
import com.example.chhots.category_view.Courses.HorizontalAdapter;
import com.example.chhots.category_view.Courses.course_purchase_view;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.MyView>{


    private List<CourseThumbnail> list;
    private Context context;

    public InstructorCourseAdapter(List<CourseThumbnail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_courses_item,parent,false);
        return new MyView(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.CourseName.setText(list.get(position).getCourseName());
        holder.CourseCategory.setText(list.get(position).getCourseName());
        Picasso.get().load(Uri.parse(list.get(position).getCourseImage())).into(holder.image);
        holder.courseId = list.get(position).getCourseId();
        holder.thumbnail = list.get(position).getCourseImage();
        holder.instructorId = list.get(position).getInstructorId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setData(List<CourseThumbnail> list)
    {
        this.list = list;
    }

    public class MyView extends RecyclerView.ViewHolder{

        TextView CourseName,CourseCategory;
        ImageView image;
        String courseId,instructorId,thumbnail;
        public MyView(@NonNull View view) {
            super(view);

            image = view.findViewById(R.id.raw_image_course);
            CourseName =view.findViewById(R.id.raw_course_name);
            CourseCategory = view.findViewById(R.id.raw_dance_form);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
