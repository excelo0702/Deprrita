package com.example.chhots.bottom_navigation_fragments.InstructorPackage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.chhots.R;
import com.example.chhots.category_view.Courses.HorizontalAdapter;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.example.chhots.category_view.routine.routine_purchase;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructorRoutineAdapter extends RecyclerView.Adapter<InstructorRoutineAdapter.MyView>{

    private List<RoutineThumbnailModel> list;
    private Context context;

    public InstructorRoutineAdapter(List<RoutineThumbnailModel> list, Context context) {
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
        holder.RoutineName.setText(list.get(position).getTitle());
        Picasso.get().load(Uri.parse(list.get(position).getRoutineThumbnail())).into(holder.image);
        holder.routineId = list.get(position).getRoutineId();
        holder.instructorId = list.get(position).getInstructorId();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<RoutineThumbnailModel> list)
    {
        this.list = list;
    }


    public class MyView extends RecyclerView.ViewHolder{

        TextView RoutineName;
        ImageView image;
        String routineId,instructorId;

        public MyView(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.raw_image_course);
            RoutineName = itemView.findViewById(R.id.raw_course_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    routinePurchase();
                }
            });

        }


        public void routinePurchase(){

            Toast.makeText(context,"fff",Toast.LENGTH_SHORT).show();
            Fragment fragment = new routine_purchase();
            Bundle bundle = new Bundle();
            bundle.putString("routineId",routineId);
            bundle.putString("userId",instructorId);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }


    }
}
