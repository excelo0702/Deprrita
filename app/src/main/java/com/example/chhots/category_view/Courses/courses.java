package com.example.chhots.category_view.Courses;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chhots.LoadingDialog;
import com.example.chhots.Login;
import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class courses extends Fragment {

    public courses() {
        // Required empty public constructor
    }




    ViewPager viewPager;
    List<CourseThumbnail> modelList;
    Button upload_btn;
    private DatabaseReference databaseReference;
    private final String TAG = "Courses123";
    private String history,trending,mostly,for_you;
    private RecyclerView recyclerView1,recyclerView2,recyclerView3,recyclerView4,recyclerView;
    private HorizontalAdapter adapter;
    private Adapter Pageradapter;

    TextView allCourse;


    private List<CourseThumbnail> searchlist;
    private com.example.chhots.category_view.Courses.SearchAdapter searchAdapter;
    SearchView searchView;

    LoadingDialog loadingDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        upload_btn = view.findViewById(R.id.upload_course);

        databaseReference = FirebaseDatabase.getInstance().getReference("");
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),upload_course.class);
                startActivity(intent);
            }
        });

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
        databaseReference.child("CoursesThumbnail").keepSynced(true);

        allCourse = view.findViewById(R.id.access_all_course);
        allCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AllCourse();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.drawer_layout,fragment).addToBackStack(null).commit();
            }
        });

        viewPager = view.findViewById(R.id.courses_viewPager);

        recyclerView = view.findViewById(R.id.search_recycler_courses_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        searchlist = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchlist,getContext());

        recyclerView1 = view.findViewById(R.id.recycler_recently_course);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        recyclerView2 = view.findViewById(R.id.recycler_trending_course);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        recyclerView3 = view.findViewById(R.id.recycler_mostlyViewed_course);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        recyclerView4 = view.findViewById(R.id.recycler_ForYou_course);
        recyclerView4.setHasFixedSize(true);
        recyclerView4.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        modelList = new ArrayList<>();
        showCoursesImageSlider();
        showRecentCourse();
        showTrendingCourse();
        showMostlyViewedCourse();
        showForYouCourse();


        return view;
    }

    private void showForYouCourse() {
        Query query = databaseReference.child("CoursesThumbnail").orderByKey().limitToFirst(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG+" jjjjjj   ",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList.add(model);
                }
                adapter = new HorizontalAdapter(modelList,getContext());
                recyclerView4.setAdapter(adapter);
                Log.d(TAG,"fghjkk");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMostlyViewedCourse() {
        Query query = databaseReference.child("CoursesThumbnail").orderByKey().limitToFirst(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG+" jjjjjj   ",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList.add(model);
                }
                adapter = new HorizontalAdapter(modelList,getContext());
                recyclerView3.setAdapter(adapter);
                Log.d(TAG,"fghjkk");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void showTrendingCourse() {
        Query query = databaseReference.child("CoursesThumbnail").orderByKey().limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG+" jjjjjj   ",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList.add(model);
                }
                adapter = new HorizontalAdapter(modelList,getContext());
                recyclerView2.setAdapter(adapter);
                Log.d(TAG,"fghjkk");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showRecentCourse() {
        Query query = databaseReference.child("CoursesThumbnail").orderByKey().limitToFirst(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG+" jjjjjj   ",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList.add(model);
                }
                adapter = new HorizontalAdapter(modelList,getContext());
                recyclerView1.setAdapter(adapter);
                Log.d(TAG,"fghjkk");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showCoursesImageSlider() {
        databaseReference.child("CoursesThumbnail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("IIIImage",dataSnapshot.getChildrenCount()+"");

                modelList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d("IIIImage",ds.getValue().toString());
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList.add(model);

                }
                Pageradapter = new Adapter(modelList,getContext());
                viewPager.setAdapter(Pageradapter);
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
                    Query firebasequery = databaseReference.child("CoursesThumbnail").orderByChild("courseName").startAt(newText).endAt(newText+"\uf8ff");
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
