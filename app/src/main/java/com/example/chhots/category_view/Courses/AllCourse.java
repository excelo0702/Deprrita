package com.example.chhots.category_view.Courses;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.R;
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
public class AllCourse extends Fragment {


    public AllCourse() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private AllCourseAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<CourseThumbnail> list;
    private ProgressBar progressBar;


    private List<CourseThumbnail> searchlist;
    private RecyclerView srecyclerview;
    private com.example.chhots.category_view.Courses.SearchAdapter searchAdapter;
    SearchView searchView;


    private DatabaseReference mDatabaseRef;
    private static final String TAG = "AllCourse";
    private FirebaseAuth auth;
    private FirebaseUser user;
    boolean isScrolling = false;

    int currentItems,scrolloutItems,TotalItems;

    String mLastKey;

    private TextView filter,sort,allCourses;
    int l=0,u=18,so=0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_course, container, false);
        recyclerView = view.findViewById(R.id.recycler_all_course_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        progressBar= view.findViewById(R.id.progress_bar_all_course);
        user = auth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAdapter = new AllCourseAdapter(list, getContext(),"allCourse");
        allCourses = view.findViewById(R.id.all_course);

        srecyclerview = view.findViewById(R.id.search_all_courses_view);
        srecyclerview.setHasFixedSize(true);
        srecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());


        filter = view.findViewById(R.id.fliter_course);
        sort = view.findViewById(R.id.sort_course);





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


        mDatabaseRef.child("CoursesThumbnail").keepSynced(true);


        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            String categ = bundle.getString("category");
            l=bundle.getInt("lower",0);
            u=bundle.getInt("upper",18);
            filter.setVisibility(GONE);
            allCourses.setText(categ);
        }
        showAllCourse(l,u);


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

                if(isScrolling && currentItems+scrolloutItems==TotalItems) {
        //            datafetch();
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
                                showAllCourse(l,u);
                                break;
                            case R.id.old:
                                so=1;
                                showCourseOld(l,u);
                                break;
                            case R.id.rating:
                                so=2;
                                showCourseRating(l,u);
                                break;
                            case R.id.mostly_viewed:
                                so=3;
                                showCourseMostlyViewed(l,u);
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
                                showAllCourse(l,u);
                                break;

                            case R.id.classical:
                                l=9;
                                u=13;
                                showAllCourse(l,u);
                                break;
                            case R.id.other:
                                l=13;
                                u=18;
                                showAllCourse(l,u);
                                break;
                            case R.id.ClearAll:
                                l=0;
                                u=18;
                                showAllCourse(l,u);
                                break;
                            case R.id.recommended:
                                //TODO:l and u are of users
                                l=0;
                                u=18;
                                showAllCourse(l,u);
                                break;
                        }
                        return true;
                    }
                });
            }
        });


        return view;
    }


    private void datafetch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastKey == null) {
                    progressBar.setVisibility(GONE);
                    return;
                }
                final int k = list.size();
                recyclerView.smoothScrollToPosition(list.size() - 1);

                    mDatabaseRef.child("VIDEOS").orderByKey().endAt(mLastKey).limitToLast(10).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                                Log.d(TAG, model.getCourseId() + " p p p ");


                                    list.add(k - 1, model);

                                if (list.size() == 6 + k) {
                                    mLastKey = list.get(k).getCourseId();
                                    break;
                                }
                            }
                            if (list.size() < 6 + k) {
                                mLastKey = null;
                            }
                            mAdapter.setData(list);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }


        },2000);
    }

    private void showRecommended() {
        list.clear();

        mDatabaseRef.child("CoursesThumbnail").orderByChild("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
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
                        list.add(0, model);
                    }
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

    private void showCourseMostlyViewed(final int l,final int u) {
        list.clear();

        mDatabaseRef.child("CoursesThumbnail").orderByChild("views").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
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
                        list.add(0, model);
                    }
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

    private void showCourseRating(final int l,final int u) {
        list.clear();

        mDatabaseRef.child("CoursesThumbnail").orderByChild("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
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
                        list.add(0, model);
                    }
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

    private void showCourseOld(final int l,final  int u) {

        list.clear();

        mDatabaseRef.child("CoursesThumbnail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
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
                        list.add( model);
                    }
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

    private void showAllCourse(final int l,final int u) {
        list.clear();
        mDatabaseRef.child("CoursesThumbnail").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
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
                        list.add(0, model);
                    }
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
                    Query firebasequery = mDatabaseRef.child("CoursesThumbnail").orderByChild("courseName").startAt(newText).endAt(newText+"\uf8ff");
                    firebasequery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                                searchlist.add(model);
                            }
                            searchAdapter.setData(searchlist);
                            recyclerView.setAdapter(searchAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                    return true;
                }
            });
        }
    }



}
