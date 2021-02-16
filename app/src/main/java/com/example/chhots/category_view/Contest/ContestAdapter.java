package com.example.chhots.category_view.Contest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chhots.LeaderboardAdapter;
import com.example.chhots.LeaderboardModel;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.explore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.Log;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.LayoutInflater.from;
import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class ContestAdapter extends PagerAdapter {
    private List<HostModel> models;
    private LayoutInflater layoutInflater;
    private Context context;
    private final String TAG = "ContestAdapter";
    PopupWindow mPopupWindow;
    RelativeLayout relativeLayout;


    public ContestAdapter(List<HostModel> models, Context context) {
        this.models = models;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        Log.e("ttttttt","ffffff");
        System.out.println(models.size()+"   kkkk  ");
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.raw_contest_item,container,false);

        Log.e(TAG,models.get(position).getImageUrl());
        System.out.println(models.get(position).getImageUrl()+"  jjjj");

        ImageView img1 = (ImageView)view.findViewById(R.id.contest_view);
        TextView txt2 = view.findViewById(R.id.contest_leaderboard);
        relativeLayout = view.findViewById(R.id.rr1);
        TextView contest_videos = view.findViewById(R.id.contest_see_videos);


        Picasso.get().load(Uri.parse(models.get(position).getImageUrl())).placeholder(R.drawable.action_button_bg).into(img1);
        final String info = models.get(position).getInfo();
        final String contestId = models.get(position).getContestId();
        System.out.println(img1+" pppp");



        contest_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("category","CONTEST");
                bundle.putString("contestId",models.get(position).getContestId());
                Fragment fragment = new explore();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.drawer_layout,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });



        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.contest_leaderboard,null);


                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }
                ImageView close = customView.findViewById(R.id.close_leaderboard);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
                mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            mPopupWindow.dismiss();
                        }
                        return false;
                    }
                });
                final RecyclerView recyclerView = customView.findViewById(R.id.contest_leaderboard);
                final LeaderboardAdapter mAdapter;
                final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                final List<LeaderboardModel> list = new ArrayList<>();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                mAdapter = new LeaderboardAdapter(list,context);
                recyclerView.setHasFixedSize(true);

                Query query = databaseReference.child("contest").child(contestId).orderByChild("like");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            /*
                            VideoModel mode = ds.getValue(VideoModel.class);
                            LeaderboardModel model = new LeaderboardModel("Name",mode.getLike(),mode.getUser());
                            list.add(0,model);
                            */
                        }
                        mAdapter.setData(list);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });


        container.addView(view);

        return view;
    }



    public void setFragment(Fragment fragment,final String info,final String contestId,final String imageUrl)
    {
        Bundle bundle = new Bundle();
        bundle.putString("contestId", contestId);
        bundle.putString("imageUrl",imageUrl);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
