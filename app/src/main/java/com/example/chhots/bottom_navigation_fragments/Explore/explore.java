package com.example.chhots.bottom_navigation_fragments.Explore;


import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.Models.ExploreVideoModel;
import com.example.chhots.R;
import com.example.chhots.onBackPressed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class explore extends Fragment implements onBackPressed {


    public explore() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    private ExploreVideoAdapter mAdapter;
    LinearLayoutManager mLayoutManager,sLayoutManager;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<ExploreVideoModel> videolist;
    private static final String TAG = "Explore";
    private TextView AddVideos;



    FirebaseUser user;

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int flag=0;
    boolean isScrolling=false;


    private List<ExploreVideoModel> searchlist;
    private SearchExploreAdapter searchAdapter;
    private SearchView searchView;
    private RecyclerView srecyclerView;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_explore, container, false);

        init(view);


            swipeRefreshLayout = view.findViewById(R.id.swipe_explore);
            showVideos();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    showVideos();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            AddVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addVideos();
                }
            });
/*
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
                }

            }
        });
*/
        return view;
    }

    private void init(View view) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videolist = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_explore_view);
        AddVideos = view.findViewById(R.id.add_normal_videos);
        progressBar =view.findViewById(R.id.explore_progressbar);
        mAdapter = new ExploreVideoAdapter(videolist,getContext());
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ExploreVideos));
        searchlist = new ArrayList<>();
        srecyclerView = view.findViewById(R.id.search_recycler_explore_view);
        srecyclerView.setHasFixedSize(true);
        sLayoutManager = new LinearLayoutManager(getContext());
        bundle = getArguments();
    }

    private void addVideos() {
        if(user==null)
        {
            Toast.makeText(getContext(),"You have to Sign in First",Toast.LENGTH_SHORT).show();
        }
        else {
            Fragment fragment = new upload_video();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    private void showVideos() {
        videolist.clear();
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    ExploreVideoModel model = ds.getValue(ExploreVideoModel.class);
                    videolist.add(0, model);
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
      /*  if (menu != null) {
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
        */
    }

    @Override
    public void onBackPressed() {
 //       getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new HomeFragment()).commit();
    }

}