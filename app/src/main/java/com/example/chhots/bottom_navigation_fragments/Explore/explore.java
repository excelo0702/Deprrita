package com.example.chhots.bottom_navigation_fragments.Explore;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.example.chhots.category_view.routine.SearchAdapter;
import com.example.chhots.category_view.routine.VideoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class explore extends Fragment{


    public explore() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    VideoAdapter mAdapter;
    LinearLayoutManager mLayoutManager,sLayoutManager;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<VideoModel> videolist;
    private static final String TAG = "Explore";
    private TextView NormalVideos,ContestVideos,AddVideos;

    int currentItems,scrolloutItems,TotalItems;
    String mLastKey,category,tempkey;

    FirebaseUser user;


    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int flag=0;
    boolean isScrolling=false;


    private List<RoutineThumbnailModel> searchlist;
    private SearchAdapter searchAdapter;
    private SearchView searchView;
    private RecyclerView srecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_explore, container, false);

        videolist = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_explore_view);
        NormalVideos = view.findViewById(R.id.normal_videos);
        ContestVideos = view.findViewById(R.id.contest_videos);
        AddVideos = view.findViewById(R.id.add_normal_videos);
        progressBar =view.findViewById(R.id.explore_progressbar);
        mAdapter = new VideoAdapter(videolist,getContext());
        category="";
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("VIDEOS");


        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());
        srecyclerView = view.findViewById(R.id.search_recycler_explore_view);
        srecyclerView.setHasFixedSize(true);
        sLayoutManager = new LinearLayoutManager(getContext());


        category="Normal";
        swipeRefreshLayout = view.findViewById(R.id.swipe_explore);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(flag==0)
                {
                    showVideos(category);
                }
                else
                {
                    showVideos(category);
                }
            //    showRoutine(category);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        showVideos(category);

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



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling=true;
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = mLayoutManager.getChildCount();
                TotalItems = mLayoutManager.getItemCount();
                scrolloutItems = mLayoutManager.findFirstVisibleItemPosition();


                if(isScrolling && currentItems+scrolloutItems==TotalItems)
                {
                    isScrolling=false;
                    progressBar.setVisibility(View.VISIBLE);
                    datafetch(category);


                }

            }
        });

        SpannableString content = new SpannableString("Normal");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        NormalVideos.setText(content);
        NormalVideos.setTextSize(24);

        NormalVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableString content = new SpannableString("Normal");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                NormalVideos.setText(content);
                ContestVideos.setText("Contest");
                NormalVideos.setTextSize(24);
                ContestVideos.setTextSize(22);
                flag=0;
                category="Normal";
                showVideos(category);
            }
        });

        ContestVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableString content = new SpannableString("Contest");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                ContestVideos.setText(content);
                ContestVideos.setTextSize(24);
                NormalVideos.setText("Normal");
                NormalVideos.setTextSize(22);
                flag=1;
                category="CONTEST";
                showVideos(category);
            }
        });

        AddVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideos();
            }
        });


        return view;
    }

    private void addVideos() {
        if(user==null)
        {
            Toast.makeText(getContext(),"You have to Sign in First",Toast.LENGTH_SHORT).show();
        }
        else {
            Fragment fragment = new upload_video();
            Bundle bundle = new Bundle();
            bundle.putString("subCategory", "NormalVideos");
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    private void datafetch(final String category) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastKey == null) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                final int k = videolist.size();
                recyclerView.smoothScrollToPosition(videolist.size() - 1);
                mDatabaseRef.child("VIDEOS").orderByKey().endAt(mLastKey).limitToLast(10).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int p=0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                VideoModel model = ds.getValue(VideoModel.class);
                                Log.d(TAG, model.getVideoId() + " p p p ");

                                if (model.getSub_category() != null && model.getSub_category().equals(category)) {
                                    videolist.add(k - 1, model);
                                }
                                if (videolist.size() == 6 + k) {
                                    mLastKey = videolist.get(k).getVideoId();
                                    break;
                                }
                                if(p==0) {
                                    tempkey = model.getVideoId();
                                }
                                p++;
                            }
                            if (videolist.size() < 6 + k) {
                                mLastKey = tempkey;
                            }
                            mAdapter.setData(videolist);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                mDatabaseRef.keepSynced(true);

            }
        },2000);
    }




    private void showVideos(final String category) {
        videolist.clear();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int k=0;
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    VideoModel model = ds.getValue(VideoModel.class);
                    if (model.getSub_category() != null && model.getSub_category().equals(category)) {
                        videolist.add(0, model);
                    }
                    if(videolist.size()>6)
                    {
                        break;
                    }
                    if(k==0) {
                        tempkey = model.getVideoId();
                    }
                    k++;
                }
                if(videolist.size()==0)
                {
                    mLastKey=tempkey;
                }
                else
                {
                    mLastKey = videolist.get(videolist.size() - 1).getVideoId();
                }
                //TODO:little change in queries of routine
                mAdapter.setData(videolist);
                Log.d(TAG, videolist.size() + "  mmm  ");
                Log.d(TAG, mLastKey + " ooo ");
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        mDatabaseRef.keepSynced(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null) {
            final MenuItem activitySearchMenu = menu.findItem(R.id.action_search);
            final MenuItem item = menu.findItem(R.id.action_search_fragment);
            activitySearchMenu.setVisible(false);
            item.setVisible(true);

            searchView= (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchlist.clear();
                    Toast.makeText(getContext(),"hhhh",Toast.LENGTH_SHORT).show();
                    Query firebasequery = mDatabaseRef.orderByChild("title").startAt(newText).endAt(newText+"\uf8ff");
                    firebasequery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     //       Log.d(TAG, dataSnapshot.getChildrenCount() + "  ppp  ");

                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);

                                    searchlist.add(model);
                                //    Log.d(TAG+"  gggg ::",model.getDescription()+"   ppp ");
                                
                            }
                            searchAdapter.setData(searchlist);
                            //    Collections.reverse(videolist);
                         //   Log.d(TAG, searchlist.size() + " popop  ");
                            srecyclerView.setLayoutManager(sLayoutManager);
                            srecyclerView.setAdapter(searchAdapter);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    return true;
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    searchlist.clear();
                    Log.e(TAG,searchlist.size()+" q ");
                }
            });
        }
    }






}
