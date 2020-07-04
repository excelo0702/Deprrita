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
import com.example.chhots.onBackPressed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */

public class explore extends Fragment implements onBackPressed {


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
    private TextView NormalVideos,ContestVideos,AddVideos,explore;

    int currentItems,scrolloutItems,TotalItems;
    String mLastKey,category,tempkey;

    FirebaseUser user;
    String lastId;


    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int flag=0;
    boolean isScrolling=false;


    private List<VideoModel> searchlist;
    private SearchExploreAdapter searchAdapter;
    private SearchView searchView;
    private RecyclerView srecyclerView;
    Bundle bundle;


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
        mAdapter = new VideoAdapter(videolist,getContext(),getResources().getString(R.string.Explore));
        category="";
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.VIDEOS));
        explore = view.findViewById(R.id.explore);


        searchlist = new ArrayList<>();
        searchAdapter = new SearchExploreAdapter(searchlist,getContext());
        srecyclerView = view.findViewById(R.id.search_recycler_explore_view);
        srecyclerView.setHasFixedSize(true);
        sLayoutManager = new LinearLayoutManager(getContext());
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

        bundle = getArguments();
        if(bundle!=null)
        {
            String category = bundle.getString("category");
            String contestId = bundle.getString("contestId");
            explore.setVisibility(GONE);
            AddVideos.setVisibility(GONE);
            NormalVideos.setVisibility(GONE);
            ContestVideos.setVisibility(GONE);
            category=getResources().getString(R.string.CONTEST);
            showContestVideos(category,contestId);
        }
        else {

            category = getResources().getString(R.string.NORMAL);
            swipeRefreshLayout = view.findViewById(R.id.swipe_explore);
            showVideos(category);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    if (flag == 0) {
                        showVideos(category);
                    } else {
                        showVideos(category);
                    }
                    //    showRoutine(category);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


            fetchLastVide();

            SpannableString content = new SpannableString("Normal");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            NormalVideos.setText(content);
            NormalVideos.setTextSize(20);
            ContestVideos.setTextSize(20);

            NormalVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString content = new SpannableString("Normal");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    NormalVideos.setText(content);
                    ContestVideos.setText("Contest");
                    NormalVideos.setTextSize(20);
                    ContestVideos.setTextSize(20);
                    flag = 0;
                    category = "Normal";
                    showVideos(category);
                }
            });

            ContestVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString content = new SpannableString("Contest");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    ContestVideos.setText(content);
                    ContestVideos.setTextSize(20);
                    NormalVideos.setText("Normal");
                    NormalVideos.setTextSize(20);
                    flag = 1;
                    category = getResources().getString(R.string.CONTEST);
                    showVideos(category);
                }
            });

            AddVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addVideos();
                }
            });
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling=true;
                    progressBar.setVisibility(GONE);
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
                    //     progressBar.setVisibility(View.VISIBLE);
                    //      datafetch(category);
                }

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
                    progressBar.setVisibility(GONE);
                    return;
                } else {
                    final int k = videolist.size();
                    if(videolist.size()>0)
                    {
                        recyclerView.smoothScrollToPosition(videolist.size());
                    }
                    Log.d("pop pop ioi", mLastKey + " mlast ");

                    mDatabaseRef.orderByKey().endAt(mLastKey).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int p = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                VideoModel model = ds.getValue(VideoModel.class);
                                Log.d("pop pop ioi", model.getVideoId() + " p p p ");

                                if (model.getSub_category() != null && model.getSub_category().equals(category)) {
                                    videolist.add(k , model);
                                }
                                if (p == 0) {
                                    tempkey = model.getVideoId();
                                }
                                p++;
                            }
                            if (videolist.size() < 4 + k) {
                                mLastKey = tempkey;
                                mAdapter.setData(videolist);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                mLastKey = videolist.get(videolist.size() - 1).getVideoId();
                                if (mLastKey == lastId) {
                                    mLastKey = null;
                                    mAdapter.setData(videolist);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    videolist.remove(videolist.size() - 1);
                                    mAdapter.setData(videolist);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(mAdapter);
                                }
                            }
                            if (mLastKey == lastId) {
                                mLastKey = null;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });


                }
            }
        },1000);
    }

    private void fetchLastVide()
    {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    VideoModel model = ds.getValue(VideoModel.class);
                    lastId = model.getVideoId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showVideos(final String category) {
        videolist.clear();
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int k=0;
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    VideoModel model = ds.getValue(VideoModel.class);
                    if (model.getSub_category() != null && model.getSub_category().equals(category)) {
                        Log.d("pop pop ",model.getVideoId()+" pop ");
                        videolist.add(0, model);
                    }
                    if(k==0) {
                        tempkey = model.getVideoId();
                    }
                    k++;
                }

                if(videolist.size()>0)
                {
                    mLastKey = videolist.get(videolist.size()-1).getVideoId();
                    mAdapter.setData(videolist);
                    Log.d("pop pop lol ", videolist.size() + "  mmm  ");
                    Log.d("pop pop qoq", mLastKey + " ooo ");
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);
                }
                else
                {
                    mLastKey=tempkey;

                    mAdapter.setData(videolist);
                    Log.d("pop pop lol ", videolist.size() + "  mmm  ");
                    Log.d("pop pop qoq", mLastKey + " ooo ");
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);

                }

                if(mLastKey == lastId)
                {
                    mLastKey=null;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void showContestVideos(final String category,final String contestId) {
        videolist.clear();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int k=0;
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    VideoModel model = ds.getValue(VideoModel.class);
                    if (model.getSub_category() != null && model.getSub_category().equals(category) && model.getContestId().equals(contestId)) {
                        videolist.add(0, model);
                    }
                }
                mAdapter.setData(videolist);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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

                    videolist.clear();
                    Query firebasequery = mDatabaseRef.orderByChild(getResources().getString(R.string.title)).startAt(query.toLowerCase()).endAt(query.toLowerCase()+"\uf8ff");
                    firebasequery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.getChildrenCount()==0)
                            {

                            }
                            else {
                                int k = 0;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    VideoModel model = ds.getValue(VideoModel.class);
                                    if (model.getSub_category() != null && model.getSub_category().equals(category)) {
                                        Log.d("pop pop ", model.getVideoId() + " pop ");
                                        videolist.add(0, model);
                                    }
                                    if (k == 0) {
                                        tempkey = model.getVideoId();
                                    }
                                    k++;
                                }
                                if (videolist.size() > 0) {
                                    mLastKey = videolist.get(videolist.size() - 1).getVideoId();
                                    mAdapter.setData(videolist);
                                    Log.d("pop pop lol ", videolist.size() + "  mmm  ");
                                    Log.d("pop pop qoq", mLastKey + " ooo ");
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    mLastKey = tempkey;

                                    mAdapter.setData(videolist);
                                    Log.d("pop pop lol ", videolist.size() + "  mmm  ");
                                    Log.d("pop pop qoq", mLastKey + " ooo ");
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(mAdapter);

                                }

                                if (mLastKey == lastId) {
                                    mLastKey = null;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });



                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchlist.clear();
                    Query firebasequery = mDatabaseRef.orderByChild(getResources().getString(R.string.title)).startAt(newText.toLowerCase()).endAt(newText.toLowerCase()+"\uf8ff");
                    firebasequery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                VideoModel model = ds.getValue(VideoModel.class);
                                searchlist.add(model);
                            }
                            searchAdapter.setData(searchlist);
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

    @Override
    public void onBackPressed() {
 //       getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new HomeFragment()).commit();
    }

}