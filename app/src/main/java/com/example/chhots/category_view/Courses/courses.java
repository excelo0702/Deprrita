package com.example.chhots.category_view.Courses;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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
import android.widget.TextView;

import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.LoadingDialog;
import com.example.chhots.R;
import com.google.android.material.tabs.TabLayout;
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

public class courses extends Fragment {

    public courses() {
        // Required empty public constructor
    }




    ViewPager viewPager;
    List<CourseThumbnail> modelList1,modelList2,modelList3,modelList4,modelList5;
    Button upload_btn;
    private DatabaseReference databaseReference;
    private final String TAG = "Courses123";
    private String history,trending,mostly,for_you;
    private RecyclerView recyclerView1,recyclerView2,recyclerView3,recyclerView4,recyclerView;
    private HorizontalAdapter adapter1,adapter2,adapter3,adapter4;
    private Adapter Pageradapter;

    TextView allCourse;
    InstructorInfoModel mode;

    private List<CourseThumbnail> searchlist;
    private com.example.chhots.category_view.Courses.SearchAdapter searchAdapter;
    SearchView searchView;

    LoadingDialog loadingDialog;
    FirebaseUser user;
    TabLayout tabLayout;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.DismissDialog();
            }
        },1500);

        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        upload_btn = view.findViewById(R.id.upload_course);


        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),upload_course.class);
                startActivity(intent);
            }
        });


        tabLayout = view.findViewById(R.id.tab_layoutt);

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
        databaseReference = FirebaseDatabase.getInstance().getReference("");

        databaseReference.child("CoursesThumbnail").keepSynced(true);



        user = FirebaseAuth.getInstance().getCurrentUser();


        allCourse = view.findViewById(R.id.access_all_course);
        allCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AllCourse();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.drawer_layout,fragment).addToBackStack(null).commit();
            }
        });


        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager = view.findViewById(R.id.courses_viewPager);
        modelList1 = new ArrayList<>();
        modelList2 = new ArrayList<>();
        modelList3 = new ArrayList<>();
        modelList4 = new ArrayList<>();
        modelList5 = new ArrayList<>();

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

        fetchUserInfo();

        showRecentCourse();
        showMostlyViewedCourse();

        showTrendingCourse();

        showCoursesImageSlider();

        return view;

    }


    private void showForYouCourse(final String interest) {
        Query query = databaseReference.child(getResources().getString(R.string.CoursesThumbnail));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList4.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    for(int i=0;i<interest.length() && i<model.getCategory().length();i++)
                    {
                        if(interest.charAt(i)==model.getCategory().charAt(i) && interest.charAt(i)=='1')
                        {
                            modelList4.add(0,model);
                            break;
                        }
                    }
                }
                adapter4 = new HorizontalAdapter(modelList4,getContext());
                recyclerView4.setAdapter(adapter4);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void showMostlyViewedCourse() {
        Query query = databaseReference.child(getResources().getString(R.string.CoursesThumbnail)).orderByChild("views");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList3.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList3.add(0,model);
                }
                adapter3 = new HorizontalAdapter(modelList3,getContext());
                recyclerView3.setAdapter(adapter3);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void showTrendingCourse() {
        Query query = databaseReference.child(getResources().getString(R.string.CoursesThumbnail)).orderByChild("trending");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList2.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d(TAG+" jjjjjj   ",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList2.add(0,model);
                }
                adapter2 = new HorizontalAdapter(modelList2,getContext());
                recyclerView2.setAdapter(adapter2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        Log.d(TAG+" jjjjjj   ",modelList2.size()+"");

    }

    private void showRecentCourse() {
        modelList1.clear();


        Query query = databaseReference.child(getResources().getString(R.string.CourseHistory)).child(user.getUid()).orderByChild("time").limitToLast(7);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList1.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Log.d("popop",ds.getValue()+"");
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList1.add(0,model);
                }
                adapter4 = new HorizontalAdapter(modelList1,getContext());
                recyclerView1.setAdapter(adapter4);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showCoursesImageSlider() {
        databaseReference.child(getResources().getString(R.string.CoursesThumbnail)).limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                modelList5.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    modelList5.add(0,model);

                }
                Pageradapter = new Adapter(modelList5,getContext());
                viewPager.setAdapter(Pageradapter);
                tabLayout.setupWithViewPager(viewPager,true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchUserInfo()
    {
        databaseReference.child(getResources().getString(R.string.InstructorInfo)).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mode = dataSnapshot.getValue(InstructorInfoModel.class);
                final String interest = mode.getInterest();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showForYouCourse(interest);

                    }
                },500);

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
