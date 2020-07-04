package com.example.chhots.category_view.Courses;

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
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.See_Video;
import com.example.chhots.SubscriptionModel;
import com.example.chhots.UserClass;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.category_view.routine.routine_view;
import com.example.chhots.ui.Dashboard.PointModel;
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

public class AllCourseAdapter extends RecyclerView.Adapter<AllCourseAdapter.MyView> {

    private List<CourseThumbnail> list;
    private Context context;
    private final String TAG = "HorizontalAdapter";
    private String activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CourseThumbnail model;
    int a1=0,a2=0,a3=0;
    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    int points=0,flag=0;

    public AllCourseAdapter(List<CourseThumbnail> list, Context context, String activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AllCourseAdapter.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_all_course_item,parent,false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCourseAdapter.MyView holder, int position) {
        holder.CourseName.setText(list.get(position).getCourseName());
        holder.CourseCategory.setText(list.get(position).getCourseName());
        Picasso.get().load(Uri.parse(list.get(position).getCourseImage())).into(holder.image);
        holder.courseId = list.get(position).getCourseId();
        holder.thumbnail = list.get(position).getCourseImage();
        holder.instructorId = list.get(position).getInstructorId();

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

    }

    public void setData(List<CourseThumbnail> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView
            extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        // Text View
        TextView CourseName,CourseCategory;
        ImageView image;
        String courseId,instructorId,thumbnail;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;
        LoadingDialog loadingDialog;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view)
        {
            super(view);
            image = view.findViewById(R.id.raw_image_all_course);
            CourseName =view.findViewById(R.id.raw_all_course_name);
            CourseCategory = view.findViewById(R.id.raw_all_course_description);
            loadingDialog = new LoadingDialog(((AppCompatActivity) context));

            view.setOnCreateContextMenuListener(this);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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
                    else if(user.getUid()==instructorId)
                    {
                        coursePurchase();
                    }
                    else
                    {
                        //        p = checkSubscription();
                    }
                    coursePurchase();

                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                    else if(user.getUid()==instructorId)
                    {
                        coursePurchase();
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

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if(activity.equals(context.getResources().getString(R.string.MyCourse))) {
                MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");
                delete.setOnMenuItemClickListener(onDeleteMenu);
            }
        }

        private final MenuItem.OnMenuItemClickListener onDeleteMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(activity.equals(context.getResources().getString(R.string.MyCourse))) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            databaseReference.child(context.getResources().getString(R.string.Courses)).child(courseId).removeValue();
                            databaseReference.child(context.getResources().getString(R.string.CoursesThumbnail)).child(courseId).removeValue();
                            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(thumbnail);
                            StorageReference ref2 = FirebaseStorage.getInstance().getReference("Course").child(courseId + "courseName");
                            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "SuccessFully Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            ref2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "SuccessFully Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                }
                return true;
            }
        };



    }


}
