package com.example.chhots.ui.Dashboard.ApproveVideo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chhots.LoadingDialog;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.InstructorInfoModel;
import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.example.chhots.ui.Subscription.SUbscriptionViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApproveVideo extends Fragment {


    public ApproveVideo() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ApproveVideoAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    private DatabaseReference mDatabaseReference;
    private List<MyRoutineModel> list;

    private FirebaseUser user;
    private InstructorInfoModel User_model;
    private String TAG="ApproveVideo";

    double viewIPAYL,viewIR,viewRC1,viewRC6,viewRC1Y,viewF1,viewF6,viewF1Y,viewPAYL,views;
    double viewTIR,viewTIC,viewTPAYL,viewTR1M,viewTR6M,viewTR1Y,viewTF1M,viewTF6M,viewTF1Y,viewTC1M,viewTC6M,viewTC1Y;

    private LoadingDialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_approve_video, container, false);
        recyclerView = view.findViewById(R.id.routine_videos);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        mAdapter = new ApproveVideoAdapter(list,getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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

        fetchTotalUsers();
        fetchUserInfo();
        fetchRoutine();
        return view;
    }

    private void fetchUserInfo() {
        mDatabaseReference.child(getString(R.string.InstructorInfo)).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User_model = dataSnapshot.getValue(InstructorInfoModel.class);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void fetchRoutine() {
        mDatabaseReference.child("ROUTINE_THUMBNAIL")
           .addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot ds: dataSnapshot.getChildren())
                   {
                       final RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);

                           if(model!=null && model.getInstructorId().equals(user.getUid()))
                           {
                               Log.d("uuuuss",ds.getValue()+"");

                               String id = model.getRoutineId();
                               final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SubscriptionRoutineViews").child(id);
                               databaseReference.child("Individual")
                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               for(DataSnapshot ds: dataSnapshot.getChildren())
                                               {
                                                   Log.d("uuuutt",ds.getValue()+"");
                                                   SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                                   if(model.getCategory().equals("Routine"))
                                                   {
                                                       viewIR=model.getViews();
                                                   }
                                                   else if(model.getCategory().equals("PAYL"))
                                                   {
                                                       viewIPAYL=model.getViews();
                                                   }
                                               }
                                               databaseReference.child("Routine")
                                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                               for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                   Log.d("uuuurr",ds.getValue()+"");

                                                                   SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                                                   if (model.getCategory().equals("1month")) {
                                                                       viewRC1 = model.getViews();
                                                                   } else if (model.getCategory().equals("6month")) {
                                                                       viewRC6 = model.getViews();

                                                                   } else if (model.getCategory().equals("1year")) {
                                                                       viewRC1Y = model.getViews();
                                                                   }

                                                               }
                                                               Log.d("uuuuuu", viewRC1 + " " + viewRC6 + " " + viewRC1Y);

                                                               databaseReference.child("Full")
                                                                       .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                           @Override
                                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                               for(DataSnapshot ds: dataSnapshot.getChildren())
                                                                               {
                                                                                   Log.d("uuuuee",ds.getValue()+"");

                                                                                   SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                                                                                   if(model.getCategory().equals("1month"))
                                                                                   {
                                                                                       viewF1=model.getViews();
                                                                                   }
                                                                                   else if(model.getCategory().equals("6month"))
                                                                                   {
                                                                                       viewF6=model.getViews();
                                                                                   }
                                                                                   else if(model.getCategory().equals("1year"))
                                                                                   {
                                                                                       viewF1Y=model.getViews();
                                                                                   }
                                                                                   Log.d("uuuuuu",viewF1+" "+viewF6+" "+viewF1Y);
                                                                               }

                                                                               double money = ((30*viewIR)/max(1.0,viewTIR) + (viewRC1*120)/max(1.0,viewTR1M) + (viewRC6*670)/max(1.0,viewTR6M) + (viewRC1Y*1280)/max(1.0,viewTR1Y) + (viewF1*450)/max(1.0,viewTF1M)
                                                                                       +(viewF6*2650)/max(1.0,viewTF6M) + (viewF1Y*5240)/max(1.0,viewTF1Y))*0.7;

                                                                               Log.d("?????",money+"   "+viewRC1+" "+viewTR1M);
                                                                               DecimalFormat dec = new DecimalFormat("#0.00");

                                                                               MyRoutineModel mode = new MyRoutineModel(model.getTitle(),model.getRoutineId(),model.getRoutineThumbnail(), dec.format(money));
                                                                               list.add(0, mode);
                                                                               mAdapter.setData(list);
                                                                               recyclerView.setLayoutManager(mLayoutManager);
                                                                               recyclerView.setAdapter(mAdapter);
                                                                               loadingDialog.DismissDialog();

                                                                           }
                                                                           @Override
                                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                           }
                                                                       });
                                                           }
                                                           @Override
                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                           }
                                                       });
                                           }
                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });
                           }
                   }
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           loadingDialog.DismissDialog();
                       }
                   },1000);


               }
               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
    }


    public void fetchTotalUsers()
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TotalUsersView");
        databaseReference.child("Routine").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuoo",ds.getValue()+"");
                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTR1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTR6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTR1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuupp",ds.getValue()+"");

                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTC1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTC6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTC1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Individual").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuqq",ds.getValue()+"");

                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("Routine"))
                    {
                        viewTIR = model.getViews();
                    }
                    else if(model.getCategory().equals("Course"))
                    {
                        viewTIC = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Full").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("uuuuww",ds.getValue()+"");
                    SUbscriptionViewModel model = ds.getValue(SUbscriptionViewModel.class);
                    if(model.getCategory().equals("1month"))
                    {
                        viewTF1M = model.getViews();
                    }
                    else if(model.getCategory().equals("6month"))
                    {
                        viewTF6M = model.getViews();
                    }
                    else if(model.getCategory().equals("1year"))
                    {
                        viewTF1Y = model.getViews();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






}
