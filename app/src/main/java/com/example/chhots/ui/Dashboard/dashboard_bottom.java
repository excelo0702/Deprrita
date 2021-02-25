package com.example.chhots.ui.Dashboard;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.LeaderboardAdapter;
import com.example.chhots.LeaderboardModel;
import com.example.chhots.MainActivity;
import com.example.chhots.R;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.UserClass;
import com.example.chhots.ui.Dashboard.HistoryPackage.history;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class dashboard_bottom extends Fragment {

    public dashboard_bottom() { }

    private Uri videouri,mImageUri;

    private Button history,leaderboard;
    BottomNavigationView bottomNavigationView;
    private ImageView userImage,userBadge;
    TextView userName,userPoints,userPoint,userLeaderbardName;
    String cat="p";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    CircularProgressBar circularProgressBar,circularProgressBar2,circularProgressBarcourse,circularProgressBarroutine,circularProgressBarinidvidual;
    String TAG ="dashboard";
    int points=0;

    RelativeLayout relativeLayout;
    PopupWindow mPopupWindow;
    String category = "Weekly";
    RecyclerView recyclerView;
    LeaderboardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    List<LeaderboardModel> list,list2;
    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_bottom, container, false);


        Bundle bundle = this.getArguments();
        cat = bundle.getString("category");
        Log.d("main22222",cat);


        FirebaseOffline();
        init(view);
        int animationDuration = 2500; // 2500ms = 2,5s
        circularProgressBarinidvidual.setBackgroundProgressBarWidth(20);



        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new history();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.dashboard_layout,fragment);
                fragmentTransaction.commit();
            }
        });


        if(!cat.equals("MainActivity"))
        {
            history.setEnabled(false);
            history.setVisibility(GONE);
            leaderboard.setVisibility(GONE);
            leaderboard.setEnabled(false);
        }


        fetchUserInfo();
        MainActivity.fetchUserPoints();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchUserPoints();
            }
        },1000);


        userPoints.setText(String.valueOf(points));
        if(points<200)
        {
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));
        }
        else if(points<300)
        {
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));
        }
        else{
            userBadge.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star1));
        }

        circularProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Toast",Toast.LENGTH_SHORT).show();
            }
        });
        circularProgressBar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"2 2 2 2 ",Toast.LENGTH_SHORT).show();
            }
        });
        circularProgressBarcourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"course",Toast.LENGTH_SHORT).show();
            }
        });
        circularProgressBarroutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"rout",Toast.LENGTH_SHORT).show();
            }
        });

        circularProgressBarinidvidual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"indi",Toast.LENGTH_SHORT).show();
            }
        });


        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaderboardUtil();
            }
        });
        checkSubscription();
        return view;

    }

    @SuppressLint("ResourceAsColor")
    private void showLeaderboardUtil() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.contest_leaderboard,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Leaderboard");
        builder.setView(customView);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        ImageView close = customView.findViewById(R.id.close_leaderboard);
        final TextView weekly = customView.findViewById(R.id.weekly);
        final TextView overAll = customView.findViewById(R.id.Overall);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        userLeaderbardName = customView.findViewById(R.id.userName_leaderboard_user);
        userPoint = customView.findViewById(R.id.points_leaderboard_user);
        recyclerView = customView.findViewById(R.id.contest_leaderboards);
        mLayoutManager = new LinearLayoutManager(getContext());
        final List<LeaderboardModel> list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mAdapter = new LeaderboardAdapter(list,getContext());
        recyclerView.setHasFixedSize(true);

        category="weekly";
        showLeaderboard(category);
        showLeaderboardForUser(category);

        SpannableString content = new SpannableString("Weekly");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        weekly.setText(content);
        weekly.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.Smurfogreen));
        overAll.setText("OverAll");
        weekly.setTextSize(24);
        overAll.setTextSize(22);
        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weekly.setTextSize(18);
                category="weekly";
                weekly.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.Smurfogreen));
                overAll.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
                SpannableString content = new SpannableString("Weekly");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                weekly.setText(content);
                overAll.setText("OverAll");
                weekly.setTextSize(22);
                overAll.setTextSize(20);
                showLeaderboard(category);
                showLeaderboardForUser(category);
            }
        });

        overAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overAll.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.Smurfogreen));
                weekly.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
                SpannableString content = new SpannableString("OverAll");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                weekly.setText("Weekly");
                overAll.setText(content);
                weekly.setTextSize(20);
                overAll.setTextSize(22);
                category="OverAll";
                showLeaderboardForUser(category);
                showLeaderboard(category);
            }
        });
    }

    private void showLeaderboard(String category) {
        Query query = databaseReference.child(getResources().getString(R.string.PointsInstructor)).child(category).orderByChild("points");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int i=1;
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    if(list.size()>5)
                    {
                        break;
                    }
                    PointModel mode = ds.getValue(PointModel.class);
                    LeaderboardModel model = new LeaderboardModel(i+"."+mode.getName(),String.valueOf(-1*mode.getPoints()),mode.getId());
                    list.add(model);
                    i++;
                }
                mAdapter.setData(list);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void showLeaderboardForUser(String category) {

        Query query = databaseReference.child(getResources().getString(R.string.PointsInstructor)).child(category).orderByChild("points");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int pos=0;
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    pos++;
                    PointModel mode = ds.getValue(PointModel.class);
                    if(mode.getId().equals(user.getUid()))
                    {
                        userLeaderbardName.setText(pos+". "+userName.getText().toString());
                        userPoint.setText(String.valueOf(-1*mode.getPoints()));
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init(View view) {
        relativeLayout = view.findViewById(R.id.rr6);

        circularProgressBar = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart_full);
        circularProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBar.setBackgroundColor(Color.GRAY);

        circularProgressBarcourse = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart_course);
        circularProgressBarcourse.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBarcourse.setBackgroundColor(Color.GRAY);

        circularProgressBarroutine = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart_routine);
        circularProgressBarroutine.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBarroutine.setBackgroundColor(Color.GRAY);

        circularProgressBarinidvidual = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart_individual);
        circularProgressBarinidvidual.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBarinidvidual.setBackgroundColor(Color.GRAY);


        circularProgressBar2 = (CircularProgressBar)view.findViewById(R.id.progress_bar_chart2);
        circularProgressBar2.setColor(ContextCompat.getColor(getContext(), R.color.Smurfogreen));
        circularProgressBar2.setBackgroundColor(Color.GRAY);

        userPoints = view.findViewById(R.id.user_dashboard_points);
        userBadge = view.findViewById(R.id.user_dashboard_badge);
        history = view.findViewById(R.id.history);
        leaderboard = view.findViewById(R.id.learderboard);
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(GONE);
        list = new ArrayList<>();
        userImage = view.findViewById(R.id.user_dashboard_profile);
        userName = view.findViewById(R.id.user_dashboard_name);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    private void FirebaseOffline() {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("disconnectmessage");
        presenceRef.onDisconnect().setValue("I disconnected!");
        presenceRef.onDisconnect().removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference reference) {
                if (error != null) {
                    Log.d(TAG, "could not establish onDisconnect event:" + error.getMessage());
                }
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "connected");
                } else {
                    Log.d(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

    }

    private void fetchUserInfo() {
        databaseReference.child("InstructorInfo").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                        Picasso.get().load(Uri.parse(model.getUserImageurl())).into(userImage);
                        userName.setText(model.getUserName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void fetchUserPoints() {
        Log.d("pop po ",MainActivity.pointsO+"");
        userPoints.setText(-1*MainActivity.pointsO+"");
    }

    public void checkSubscription()
    {

        //kon sa subscription h uske pas


        Log.d(TAG," pqq ");
        final int[] flag = new int[1];

        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        //      mDatabaseReference.child("USER_PURCHASED_ROUTINES").child(userId).child("Individual").child()

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Course")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        //find category
                        if (model != null) {
                            int k = dataDifference(date, model.getDate());
                            if (model.getCategory().equals("1month")) {
                                if(k>30)
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(k*100/30,20);
                                }
                            } else if (model.getCategory().equals("6month")) {
                                if(k>180)
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(k*100/180,20);
                                }
                            } else if (model.getCategory().equals("1year")) {
                                if(k>30)
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(100,40);
                                }
                                else
                                {
                                    circularProgressBarcourse.setProgressWithAnimation(k*100/365,40);
                                }
                            }
                        }
                        else
                        {
                            circularProgressBarcourse.setProgressWithAnimation(0,10);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Routine")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        //find category
                        if (model != null) {
                            int k = dataDifference(date, model.getDate());
                            if (model.getCategory().equals("1month")) {
                                if(k>30)
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(k*100/30,20);
                                }
                            } else if (model.getCategory().equals("6month")) {
                                if(k>180)
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(k*100/180,20);
                                }
                            } else if (model.getCategory().equals("1year")) {
                                if(k>365)
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(100,40);
                                }
                                else
                                {
                                    circularProgressBarroutine.setProgressWithAnimation(k*100/365,40);
                                }
                            }
                        }
                        else
                        {
                            circularProgressBarroutine.setProgressWithAnimation(0,10);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Full")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        //find category
                        if (model != null) {
                            int k = dataDifference(date, model.getDate());
                            if (model.getCategory().equals("1month")) {
                                if(k>30)
                                {
                                    circularProgressBar.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBar.setProgressWithAnimation(k*100/30,20);
                                }
                            } else if (model.getCategory().equals("6month")) {
                                if(k>180)
                                {
                                    circularProgressBar.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBar.setProgressWithAnimation(k*100/180,20);
                                }
                            } else if (model.getCategory().equals("1year")) {
                                if(k>365)
                                {
                                    circularProgressBar.setProgressWithAnimation(100,40);
                                }
                                else
                                {
                                    circularProgressBar.setProgressWithAnimation(k*100/365,40);
                                }
                            }
                        }
                        else
                        {
                            circularProgressBar.setProgressWithAnimation(0,10);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        mDatabaseReference.child("USER_PURCHASED").child(user.getUid()).child("Individual")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserClass model = dataSnapshot.getValue(UserClass.class);
                        //find category
                        if (model != null) {
                            int k = dataDifference(date, model.getDate());
                            if (model.getCategory().equals("3days")) {
                                if(k>3)
                                {
                                    circularProgressBarinidvidual.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarinidvidual.setProgressWithAnimation(k*100/3,20);
                                }
                            } else if (model.getCategory().equals("10days")) {
                                if(k>10)
                                {
                                    circularProgressBarinidvidual.setProgressWithAnimation(100,20);
                                }
                                else
                                {
                                    circularProgressBarinidvidual.setProgressWithAnimation(k*100/10,20);
                                }
                            }
                        }
                        else
                        {
                            circularProgressBarinidvidual.setProgressWithAnimation(0,10);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("2323232","111111");


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
