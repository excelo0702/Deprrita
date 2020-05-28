package com.example.chhots.category_view.routine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoModel;
import com.example.chhots.bottom_navigation_fragments.Explore.upload_video;
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
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class routine extends Fragment {

    public routine() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    RoutineAdapter mAdapter;
    LinearLayoutManager mLayoutManager,sLayoutManager;
    private TextView filter,sort,addRoutine;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<RoutineThumbnailModel> videolist;
    private static final String TAG = "Routine";
    boolean isScrolling=false;
    int currentItems,scrolloutItems,TotalItems;
    String mLastKey,category,tempkey;

    FirebaseAuth auth;
    FirebaseUser user;

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int fi=0,so=0;

    private List<RoutineThumbnailModel> searchlist;
    private SearchAdapter searchAdapter;
    SearchView searchView;
    private RecyclerView srecyclerView;
    LoadingDialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.DismissDialog();
            }
        },1500);

        videolist = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_routine_view);
        filter = view.findViewById(R.id.fliter_routine);
        sort = view.findViewById(R.id.sort_routine);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        addRoutine = view.findViewById(R.id.add_routine_video);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabaseRef =FirebaseDatabase.getInstance().getReference();
        progressBar =view.findViewById(R.id.routine_progressbar);
        mAdapter = new RoutineAdapter(videolist,getContext());
        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());
        srecyclerView = view.findViewById(R.id.search_recycle_routine_view);
        srecyclerView.setHasFixedSize(true);
        sLayoutManager = new LinearLayoutManager(getContext());
        category="111111111111111111";
        swipeRefreshLayout = view.findViewById(R.id.swipe_routine);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                so=0;
                showRoutine(category);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        so=0;
        showRoutine(category);
        mDatabaseRef.child("ROUTINE_THUMBNAIL").keepSynced(true);
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


        addRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideos();
            }
        });

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
                    progressBar.setVisibility(View.VISIBLE);
                    if(so==0)
                    datafetch("");
                    else if(so==1)
                    {
                        datafetchOld("");
                    }

                }
            }
        });


        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(),view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.sort_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId())
                        {
                            case R.id.latest:
                                so=0;
                                showRoutine("");
                                break;
                            case R.id.old:
                                so=1;
                                showRoutineOld("");
                                break;
                        }
                        return true;
                    }
                });
            }
        });



        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(),view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.filter_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId())
                        {
                            case R.id.street:

                                category = "111111110000000000";
                                showRoutine(category);
                                break;

                            case R.id.classical:
                                category = "111111110000000000";
                                showRoutine(category);
                                break;

                            case R.id.other:
                                category = "111111110000000000";
                                showRoutine(category);
                                break;

                            default:
                                category="111111110000000000";
                                showRoutine(category);
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        return view;
    }

    private void datafetch(final String category) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastKey == null) {
                    progressBar.setVisibility(GONE);
                    return;
                }
                final int k = videolist.size();
                recyclerView.smoothScrollToPosition(videolist.size() - 1);
                    mDatabaseRef.child("ROUTINE_THUMBNAIL").orderByKey().endAt(mLastKey).limitToLast(10).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                            //    Log.d(TAG, model.getVideoId() + " p p p ");

                                if (model.category.equals(category)) {
                                    videolist.add(k - 1, model);
                                }
                                if (videolist.size() == 8 + k) {
                                    mLastKey = videolist.get(k).getRoutineId();
                                    break;
                                }
                            }
                            if (videolist.size() < 8+ k) {
                                mLastKey = null;
                            }
                            mAdapter.setData(videolist);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });


            }
        },2000);
    }


    private void datafetchOld(final String category) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastKey == null) {
                    progressBar.setVisibility(GONE);
                    return;
                }
                final int k = videolist.size();
                recyclerView.smoothScrollToPosition(videolist.size() - 1);
                    mDatabaseRef.child("ROUTINE_THUMBNAIL").orderByKey().startAt(mLastKey).limitToFirst(10).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
//                                Log.d(TAG, model.getVideoId() + " p p p ");

                                if (model.category.equals(category)) {
                                    videolist.add(model);
                                }
                                if (videolist.size() == 8 + k) {
                                    mLastKey = videolist.get(k).getRoutineId();
                                    break;
                                }
                            }
                            if (videolist.size() < 6 + k) {
                                mLastKey = null;
                            }
                            mAdapter.setData(videolist);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

            }
        },2000);
    }

    private void addVideos() {

        if(user==null)
        {
            Toast.makeText(getContext(),"You have to Sign in First",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getContext(),addRoutine.class);
            getContext().startActivity(intent);
        }
    }


    private void showRoutine(final String category) {
        videolist.clear();

            mDatabaseRef.child("ROUTINE_THUMBNAIL").limitToLast(8).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int p=0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Log.d(TAG,ds.getValue()+"");
                        RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                        if(category.compareTo(model.category)>0)
                            videolist.add(0, model);

                        if(p==0) {
                            tempkey = model.getRoutineId();
                        }
                        p++;
                    }
                    if(videolist.size()==0)
                    {
                        mLastKey=null;
                    }
                    else
                    {
                        mLastKey = videolist.get(videolist.size() - 1).getRoutineId();
                    }
                    mAdapter.setData(videolist);
                    Log.d(TAG, videolist.size() + "  mmm  ");
                    Log.d(TAG, mLastKey + " ooo ");
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }



    private void showRoutineOld(final String category) {
        videolist.clear();

        mDatabaseRef.child("ROUTINE_THUMBNAIL").limitToFirst(8).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p=0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG,ds.getValue()+"");
                    RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                    if(model.category.equals(category))
                        videolist.add( model);

                    if(p==0) {
                        tempkey = model.getRoutineId();
                    }
                    p++;
                }
                if(videolist.size()==0)
                {
                    mLastKey=null;
                }
                else
                {
                    mLastKey = videolist.get(videolist.size() - 1).getRoutineId();
                }
                mAdapter.setData(videolist);
                Log.d(TAG, videolist.size() + "  mmm  ");
                Log.d(TAG, mLastKey + " ooo ");
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
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchlist.clear();
                    Toast.makeText(getContext(),"hhhh",Toast.LENGTH_SHORT).show();
                    Query firebasequery = mDatabaseRef.child("ROUTINE_THUMBNAIL").orderByChild("title").startAt(newText).endAt(newText+"\uf8ff");
                    firebasequery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                                searchlist.add(model);
                                Log.d(TAG+"  gggg ::",model.getTitle()+"   ppp ");

                            }
                            searchAdapter.setData(searchlist);
                            //    Collections.reverse(videolist);
                            Log.d(TAG, videolist.size() + "  mmm  ");
                            Log.d(TAG, mLastKey + " ooo ");
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
