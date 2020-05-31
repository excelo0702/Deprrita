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

import com.example.chhots.LoadingDialog;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.See_Video;
import com.example.chhots.SubscriptionModel;
import com.example.chhots.UserClass;
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
    private final String TAG = "HOrizontalAdapter";
    private String activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CourseThumbnail model;
    int a1=0,a2=0,a3=0;
    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    int points=0;

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
        Log.d(TAG,"fghjkk");

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


        Log.d(TAG+" ppp ",list.size()+"");

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
fetchUserPoints();
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=0;
                    if (user == null) {
                        loadingDialog.startLoadingDialog();

                        Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                    }
                    else if(user.getUid()==instructorId)
                    {
                        loadingDialog.startLoadingDialog();


                        databaseReference.child("CoursesThumbnail").child(courseId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                model = dataSnapshot.getValue(CourseThumbnail.class);
                                model.setViews(model.getViews()+1);
                                int k = dataDifference(date,model.getDate());
                                model.setTrending((double)((1.0*model.getViews())/k));
                                if(a1==0){
                                    a1=1;
                                    databaseReference.child("CourseHistory").child(courseId).setValue(model);

                                    databaseReference.child("CoursesThumbnail").child(courseId).setValue(model);
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Fragment fragment = new routine_view();
                        Bundle bundle = new Bundle();
                        bundle.putString("category","Course");
                        bundle.putString("courseId",courseId);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else {
                        loadingDialog.startLoadingDialog();
                        if (p == 0) {
                            //      p = checkPurchased();
                        }
                    }
                    p=1;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.DismissDialog();
                        }
                    },3000);
                    if(p==1)
                    {


                        PointModel popo = new PointModel(instructorId,points+25);
                        databaseReference.child("PointsInstructor").child(user.getUid()).setValue(popo);
                        databaseReference.child("CoursesThumbnail").child(courseId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                model = dataSnapshot.getValue(CourseThumbnail.class);
                                model.setViews(model.getViews()+1);
                                int k = dataDifference(date,model.getDate());
                                model.setTrending((double)((1.0*model.getViews())/k));
                                if(a2==0){
                                    a2=1;
                                    databaseReference.child("CourseHistory").child(courseId).setValue(model);

                                    databaseReference.child("CoursesThumbnail").child(courseId).setValue(model);
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Fragment fragment = new routine_view();
                        Bundle bundle = new Bundle();
                        bundle.putString("category","Course");
                        bundle.putString("courseId",courseId);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else
                    {
                        Fragment fragment = new course_purchase_view();
                        Bundle bundle = new Bundle();
                        bundle.putString("courseId", courseId);
                        bundle.putString("thumbnail", thumbnail);
                        bundle.putString("userId", user.getUid());
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });



        }

        public int dataDifference(String date1,String date2)
        {
            int k=0;

            String sy1 = date1.substring(0,4);
            String sy2 = date2.substring(0,4);

            int y = 365*(Integer.parseInt(sy1)-Integer.parseInt(sy2));


            String sm1 = date1.substring(5,7);
            String sm2 = date2.substring(5,7);
            int m = 30*(Integer.parseInt(sm1)-Integer.parseInt(sm2));

            String sd1 = date1.substring(8,10);
            String sd2 =  date2.substring(8,10);
            int d = Integer.parseInt(sd1)-Integer.parseInt(sd2);
            k = y+m+d+1;
            return k;
        }



        private void fetchUserPoints() {
           /* databaseReference.child("PointsInstructor").child(instructorId).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                           //     PointModel model = dataSnapshot.getValue(PointModel.class);
                             //   points = model.getPoints();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );*/
        }



        public int checkSubscription()
        {
            Log.d(TAG," pqq ");
            final int[] flag = new int[1];
            mDatabaseReference.child("COURSESUBSCRIPTION").child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                Log.d(TAG,ds.getValue()+"");

                                SubscriptionModel model = ds.getValue(SubscriptionModel.class);
                                if(model.getVideoId().equals(courseId))
                                {
                                    Log.d(TAG," pqq ");
                                    flag[0] =1;
                                    return;
                                }

                            }
                            if(flag[0]==1)
                            {


                                databaseReference.child("CoursesThumbnail").child(courseId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        model = dataSnapshot.getValue(CourseThumbnail.class);
                                        model.setViews(model.getViews()+1);
                                        int k = dataDifference(date,model.getDate());
                                        model.setTrending((double)((1.0*model.getViews())/k));
                                        if(a3==0){
                                            a3=1;
                                            databaseReference.child("CourseHistory").child(courseId).setValue(model);

                                            databaseReference.child("CoursesThumbnail").child(courseId).setValue(model);
                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Fragment fragment = new See_Video();
                                Bundle bundle = new Bundle();
                                bundle.putString("videoId", courseId);
                                fragment.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.drawer_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            if(flag[0]==1)
                return 1;


            return 0;
        }

        public int checkPurchased()
        {
            Log.d(TAG," pqq ");
            final int[] flag = new int[1];
            mDatabaseReference.child("USERS").child(user.getUid()).child("courses")
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG,dataSnapshot.getValue()+"");

                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                Log.d(TAG,ds.getValue()+"");

                                UserClass model = ds.getValue(UserClass.class);
                                if(model.getCourseId().equals(courseId))
                                {
                                    Log.d(TAG," peee ");
                                    flag[0] =1;
                                    Log.d(TAG,flag[0]+" oo ");
                                }
                            }
                            if(flag[0]==1) {


                                PointModel popo = new PointModel(instructorId,points+25);
                                databaseReference.child("PointsInstructor").child(user.getUid()).setValue(popo);

                                Fragment fragment = new course_view();
                                Bundle bundle = new Bundle();
                                bundle.putString("courseId", courseId);
                                bundle.putString("thumbnail",thumbnail);
                                bundle.putString("instructorId",instructorId);
                                fragment.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.drawer_layout, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                return;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            if(flag[0]==1)
            {
                return 1;
            }

            //TODO: handler for wait

            return 0;
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if(activity.equals("MyCourse")) {
                MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");
                delete.setOnMenuItemClickListener(onDeleteMenu);
            }
        }

        private final MenuItem.OnMenuItemClickListener onDeleteMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(activity.equals("MyCourse")) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            databaseReference.child("Courses").child(courseId).removeValue();
                            databaseReference.child("CoursesThumbnail").child(courseId).removeValue();
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
