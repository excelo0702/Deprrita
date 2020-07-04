package com.example.chhots.category_view.routine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
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
import android.widget.RelativeLayout;
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
    String mLastKey,category,tempkey,lastId;

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
    int l=0,u=18;
    RelativeLayout tt;
    int Flag=0;


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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabaseRef =FirebaseDatabase.getInstance().getReference();
        progressBar =view.findViewById(R.id.routine_progressbar);
        mAdapter = new RoutineAdapter(videolist,getContext(),"routine");
        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());
        srecyclerView = view.findViewById(R.id.search_recycle_routine_view);
        srecyclerView.setHasFixedSize(true);
        sLayoutManager = new LinearLayoutManager(getContext());
        category="111111111111111111";
        swipeRefreshLayout = view.findViewById(R.id.swipe_routine);
        addRoutine = view.findViewById(R.id.add_routine_video);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //        so=0;
                //        showRoutine(l,u);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },100);
            }
        });
        so=0;
        showRoutine(l,u);
        fetchLast();
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



        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        View NavBar = getActivity().findViewById(R.id.nav_view);
        NavBar.setVisibility(View.VISIBLE);

        tt = getActivity().findViewById(R.id.tt);
        tt.setVisibility(View.VISIBLE);

        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling=true;
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
                                showRoutine(l,u);
                                break;
                            case R.id.old:
                                so=1;
                                showRoutineOld(l,u);
                                break;

                            case R.id.mostly_viewed:
                                so=2;
                                showRoutineView(l,u);
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

                                l = 0;
                                u=8;
                                showRoutine(l,u);
                                break;

                            case R.id.classical:
                                l=9;
                                u=13;
                                showRoutine(l,u);
                                break;
                            case R.id.other:
                                l=13;
                                u=18;
                                showRoutine(l,u);
                                break;
                            case R.id.ClearAll:
                                l=0;
                                u=18;
                                showRoutine(l,u);
                                break;
                            case R.id.recommended:
                                l=0;
                                u=18;
                                showRoutine(l,u);
                                break;
                        }
                        return true;
                    }
                });
            }
        });


        addRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideos();
            }
        });


        return view;
    }

    private void fetchLast() {
        mDatabaseRef.child("ROUTINE_THUMBNAIL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                    lastId = model.getRoutineId();
                    Log.d("pop pop hoh",lastId+" lastId ");
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRoutineView(final int l,final int u) {
        videolist.clear();

        mDatabaseRef.child("ROUTINE_THUMBNAIL").orderByChild("views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p=0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG,ds.getValue()+"");
                    RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                    String news = model.getCategory().substring(l,u);
                    int flag=0;
                    for(int i=0;i<news.length();i++)
                    {
                        if(news.charAt(i)=='1')
                        {
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1)
                    {
                        videolist.add( model);
                    }
                }
                mAdapter.setData(videolist);
                Log.d("pop pop lol", videolist.size() + "  mmm  ");
                Log.d("pop pop pop pop ", mLastKey + " ooo ");
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showRoutine(final int l,final int u) {
        videolist.clear();

        Query query = mDatabaseRef.child("ROUTINE_THUMBNAIL");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                    String news = model.getCategory().substring(l,u);
                    int flag=0;
                    for(int i=0;i<news.length();i++)
                    {
                        if(news.charAt(i)=='1')
                        {
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1)
                    {
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


    private void showRoutineOld(final int l,final int u) {
        videolist.clear();
        mDatabaseRef.child("ROUTINE_THUMBNAIL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG,ds.getValue()+"");
                    RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                    String news = model.getCategory().substring(l,u);
                    int flag=0;
                    for(int i=0;i<news.length();i++)
                    {
                        if(news.charAt(i)=='1')
                        {
                            flag=1;
                            break;
                        }
                    }
                    if(flag==1)
                    {
                        videolist.add(model);
                    }
                }
                mAdapter.setData(videolist);
                Log.d("pop pop lol ", videolist.size() + "  mmm  ");
                Log.d("pop pop qoq", mLastKey + " ooo ");
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

                    Query firebasequery = mDatabaseRef.child("ROUTINE_THUMBNAIL").orderByChild("title").startAt(newText).endAt(newText+"\uf8ff");
                    firebasequery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);
                                searchlist.add(model);

                            }
                            searchAdapter.setData(searchlist);
                            //    Collections.reverse(videolist);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) videolist);
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




}
