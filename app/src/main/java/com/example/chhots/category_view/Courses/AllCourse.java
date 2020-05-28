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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
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
        mAdapter = new AllCourseAdapter(list, getContext());


        srecyclerview = view.findViewById(R.id.search_all_courses_view);
        srecyclerview.setHasFixedSize(true);
        srecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());


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


        showAllCourse();
        mDatabaseRef.child("CoursesThumbnail").keepSynced(true);

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

                if(isScrolling && currentItems+scrolloutItems==TotalItems) {
                    datafetch();
                }

            }
        });




        return view;
    }


    private void datafetch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastKey == null) {
                    progressBar.setVisibility(View.GONE);
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



    private void showAllCourse() {

        mDatabaseRef.child("CoursesThumbnail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    list.add(0, model);
                }
                if (list.size() == 0) {
                    mLastKey = null;
                } else {
                    mLastKey = list.get(list.size() - 1).getCourseId();
                }

                //    Collections.reverse(videolist);
                mAdapter.setData(list);
                Log.d(TAG, mAdapter.getItemCount() + "  mmm  ");
                Log.d(TAG, mLastKey + " ooo ");
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
