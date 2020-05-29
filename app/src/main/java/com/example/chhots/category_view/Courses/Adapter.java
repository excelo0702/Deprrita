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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chhots.InstructorLogin;
import com.example.chhots.LoadingDialog;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.UserClass;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.category_view.routine.routine_view;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<CourseThumbnail> models;
    private LayoutInflater layoutInflater;
    private Context context;
    String instructorId;
    LoadingDialog loadingDialog;
    DatabaseReference mDatabaseReference;
    FirebaseUser user;


    private String TAG = "ppq";
    String courseId;




    public Adapter(List<CourseThumbnail> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        Log.d("111111","fghjkllkjhg");
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.raw_course_viewpager_item,container,false);

        TextView course_name,des;
        ImageView img;
        final String thumbnail = models.get(position).getCourseImage();


        course_name = (TextView)view.findViewById(R.id.raw_course_viewpager_name);
        des = (TextView)view.findViewById(R.id.raw_course_viewpager_description);
        img = (ImageView)view.findViewById(R.id.raw_course_viewpager_image);
        user = FirebaseAuth.getInstance().getCurrentUser();
        instructorId = models.get(position).getInstructorId();


        course_name.setText(models.get(position).getCourseName());
        des.setText(models.get(position).getCourseName());
        Picasso.get().load(Uri.parse(models.get(position).getCourseImage())).resize(400,300).into(img);
        loadingDialog = new LoadingDialog(((AppCompatActivity) context));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p=0;
                courseId = models.get(position).getCourseId();
                if (user == null) {
                    Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Login.class);
                    context.startActivity(intent);
                }
                else if(user.getUid()==instructorId)
                {
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

        container.addView(view);
        return view;
    }

    public int checkPurchased()
    {
        Log.d(TAG," pqq ");
        final int[] flag = new int[1];
        mDatabaseReference.child("USER_PURCHASED_ROUTINES").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG,dataSnapshot.getValue()+"");

                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            Log.d(TAG,ds.getValue()+"");
                            UserClass model = ds.getValue(UserClass.class);
                            if(model.getVideoId().equals(courseId))
                            {
                                Log.d(TAG," peee ");
                                flag[0] =1;
                                Log.d(TAG,flag[0]+" oo ");
                            }
                        }
                        if(flag[0]==1) {
                            Fragment fragment = new routine_view();
                            Bundle bundle = new Bundle();
                            bundle.putString("routineId", courseId);
                            bundle.putString("category","Routine");
                            fragment.setArguments(bundle);
                            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.drawer_layout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        //TODO: handler for wait
        return 0;
    }




    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
