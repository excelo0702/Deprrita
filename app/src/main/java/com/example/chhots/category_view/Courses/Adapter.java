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
import com.example.chhots.ui.Dashboard.PointModel;
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


public class Adapter extends PagerAdapter {

    private List<CourseThumbnail> models;
    private LayoutInflater layoutInflater;
    private Context context;
    String instructorId,courseId;
    LoadingDialog loadingDialog;
    DatabaseReference mDatabaseReference;
    FirebaseUser user;
    int flag=0;
    String thumbnail;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CourseThumbnail model;
    int a1=0,a2=0,a3=0;

    private String TAG = "ppq";
    int points=0;

    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());



    public Adapter(List<CourseThumbnail> models, Context context) {
        this.models = models;
        this.context = context;
    }

    public void setData(List<CourseThumbnail> models)
    {
        this.models = models;
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
        thumbnail = models.get(position).getCourseImage();
        courseId = models.get(position).getCourseId();

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
//        notifyDataSetChanged();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p=0;
                if(user==null)
                {
                    Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, InstructorLogin.class);
                    context.startActivity(intent);
                }
                else if(user.getUid()==instructorId)
                {
                    coursePurchase();
                }
                else
                {
                 //   loadingDialog.startLoadingDialog();
                  //  p = checkSubscription();
                }
                coursePurchase();



            }
        });

        container.addView(view);
        return view;
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




    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
