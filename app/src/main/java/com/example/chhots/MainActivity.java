package com.example.chhots;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.chhots.bottom_navigation_fragments.Calendar.calendar;
import com.example.chhots.bottom_navigation_fragments.Explore.explore;
import com.example.chhots.bottom_navigation_fragments.Explore.upload_video;
import com.example.chhots.category_view.Contest.form_contest;
import com.example.chhots.category_view.Courses.course_purchase_view;
import com.example.chhots.category_view.Courses.video_course;
import com.example.chhots.category_view.routine.routine;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.ui.About_Deprrita.about;
import com.example.chhots.ui.Category.category;
import com.example.chhots.ui.Dashboard.dashboard;
import com.example.chhots.ui.Feedback.feedback;
import com.example.chhots.ui.Setting.setting;
import com.example.chhots.ui.Subscription.subscription;
import com.example.chhots.ui.SupportUs.support;
import com.example.chhots.ui.home.HomeFragment;
import com.example.chhots.User_Profile.userprofile;
import com.example.chhots.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements  PaymentListener{

    private AppBarConfiguration mAppBarConfiguration;
    BottomNavigationView bottomNavigationView;
    VideoView videoView;
    TextView login;
    FirebaseAuth auth;
    ImageView user_profile_header;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference databaseReference;
    Button chatBtn;
    ActionBarDrawerToggle t;
    LoadingDialog loadingDialog;
    private static final String TAG = "MainActivity1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialog(MainActivity.this);
        getSupportFragmentManager().beginTransaction().add(R.id.drawer_layout,new HomeFragment()).commit();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer);
        t = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.Open,R.string.Close);

        drawer.addDrawerListener(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        t.setDrawerIndicatorEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        if(user==null)
                        {
                            Toast.makeText(getApplicationContext(),"Login First",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,Login.class);
                            startActivity(intent);
                        }
                        else
                        {

                            Fragment fragment = new dashboard();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.drawer_layout,fragment);
                            fragmentTransaction.addToBackStack(null);
                                  Bundle bundle = new Bundle();
                                bundle.putString("category","MainActivity");
                              fragment.setArguments(bundle);
                            Log.d("main222","fragment");
                            fragmentTransaction.commit();
                        }
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_notification:
                        setFragment(new NotificationsFragment());
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_subscription:
                        setFragment(new subscription());
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_category:
                        setFragment(new category());
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_setting:
                        setFragment(new setting());
                        drawer.closeDrawers();
                        break;

                    case R.id.nav_feedback:
                        setFragment(new feedback());
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about:
                        setFragment(new about());
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_support:
                        setFragment(new support());
                        drawer.closeDrawers();
                        break;
                }
                return true;
            }
        });


        View headerview = navigationView.getHeaderView(0);
        login = (TextView) headerview.findViewById(R.id.login_textview);
        user_profile_header = headerview.findViewById(R.id.imageView_header);
        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);



        askPermission();


        if (auth.getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("");
            login.setText(user.getEmail());
            login.setPaintFlags(login.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            Query query = databaseReference.child(getString(R.string.InstructorInfo)).child(auth.getCurrentUser().getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                    Log.d(TAG,dataSnapshot.getValue()+"");
                      login.setText(model.getUserName());
                      Picasso.get().load(Uri.parse(model.getUserImageurl())).placeholder(R.mipmap.ic_logo).into(user_profile_header);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,new userprofile(),"1");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    drawer.closeDrawers();

                }
            });

        }
        else
        {
            login.setText("Login|Signup");
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  FragmentTransaction fe=getSupportActionBar();
                    // getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fe,).addToBackStack(null).commit();
                    Intent intent = new Intent(MainActivity.this,Login.class);
                    startActivity(intent);
                }
            });
        }


        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_dashboard:
                                setFragment(new HomeFragment());

                                Toast.makeText(getApplicationContext(), "Dashboard", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_favorites:
                                setFragment(new explore());
                                Toast.makeText(getApplicationContext(), "Favorites", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_trending:
                                setFragment(new calendar());
                                Toast.makeText(getApplicationContext(), "Calendar", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.action_instructor:
                                setFragment(new instructor());
                                Toast.makeText(getApplicationContext(), "Instructor", Toast.LENGTH_SHORT).show();
                                break;


                            default:
                                setFragment(new HomeFragment());
                                Toast.makeText(getApplicationContext(), "Dashboard", Toast.LENGTH_SHORT).show();

                        }
                        return true;
                    }

                });
    }

    private void askPermission() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,2004);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem item2 = menu.findItem(R.id.action_search_fragment);
        item.setVisible(false);
        item2.setVisible(false);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //search for instructors name,users name,artist name
                Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setFragment(Fragment fragment)
    {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setEnabled(true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment);
        fragmentTransaction.commit();
    }



    @Override
    public void onPaymentSuccess(String s) {

        try{

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for(Fragment f : fragments) {
                if (f != null && f instanceof routine_purchase)
                    ((routine_purchase) f).onPaymentSuccess(s);
                if (f != null && f instanceof course_purchase_view)
                    ((course_purchase_view) f).onPaymentSuccess(s);
                if (f != null && f instanceof form_contest)
                    ((form_contest) f).onPaymentSuccess(s);
            }
        }
        catch (Exception e)
        {

        }

/*
        try {
            Toast.makeText(getApplicationContext(), "Payment Successful: " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }   */
    }

    @Override
    public void onPaymentError(int i, String s) {
        //   Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        try {
            Toast.makeText(getApplicationContext(), "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }    }

    @Override
    public void onBackPressed() {
        int p = tellFragments();

        if(p==0) {
            super.onBackPressed();
        }
    }

    private int tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && (f instanceof upload_video)) {
                ((upload_video) f).onBackPressed();
            }

            if(f != null && (f instanceof form_contest)) {
                ((form_contest) f).onBackPressed();
            }

            if(f != null && (f instanceof video_course)) {
                ((video_course) f).onBackPressed();
            }
            if(f != null && (f instanceof dashboard)) {
                ((dashboard) f).onBackPressed();
                return 1;
            }
            if(f != null && (f instanceof NotificationsFragment)) {
                ((NotificationsFragment) f).onBackPressed();
                return 1;
            }
            if(f != null && (f instanceof category)) {
                ((category) f).onBackPressed();
                return 1;
            }
            if(f != null && (f instanceof setting)) {
                ((setting) f).onBackPressed();
                return 1;
            }
            if(f != null && (f instanceof feedback)) {
                ((feedback) f).onBackPressed();
                return 1;
            }

            if(f != null && (f instanceof routine)) {
                ((routine) f).onBackPressed();
                return 1;
            }
        }
        return 0;
    }



}