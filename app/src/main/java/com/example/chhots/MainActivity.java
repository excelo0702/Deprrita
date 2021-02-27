package com.example.chhots;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.chhots.Services.MyNetworkReceiver;
import com.example.chhots.User_Profile.edit_profile;
import com.example.chhots.bottom_navigation_fragments.Calendar.calendar;
import com.example.chhots.bottom_navigation_fragments.Explore.explore;
import com.example.chhots.bottom_navigation_fragments.Explore.upload_video;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.bottom_navigation_fragments.InstructorPackage.instructor;
import com.example.chhots.category_view.Contest.form_contest;
import com.example.chhots.category_view.Courses.course_purchase_view;
import com.example.chhots.category_view.routine.routine_purchase;
import com.example.chhots.category_view.routine.routine_view;
import com.example.chhots.ui.About_Deprrita.about;
import com.example.chhots.ui.Category.category;
import com.example.chhots.ui.Dashboard.ApproveVideo.ChatPeopleList;
import com.example.chhots.ui.Dashboard.PointModel;
import com.example.chhots.ui.Dashboard.dashboard;
import com.example.chhots.ui.Feedback.feedback;
import com.example.chhots.ui.Subscription.subscription;
import com.example.chhots.ui.SupportUs.support;
import com.example.chhots.ui.home.HomeFragment;
import com.example.chhots.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import static android.graphics.Typeface.BOLD;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

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

    TextView notification_text,dashboard_text;
    private String original_text;
    int k=0;
    NavigationView navigationView;

    public static int pointsO=0,pointsW=0;

    private BroadcastReceiver myNetworkReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase offline capabilities
        Intent intent = getIntent();
        if(intent.getStringExtra("Category")==null)
        {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

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



        navigationView = findViewById(R.id.nav_view);

        myNetworkReceiver = new MyNetworkReceiver();
        broadcastIntent();

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
                        setFragment(new NotificationsFragment(),"not home");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_subscription:
                        setFragment(new subscription(),"not home");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_category:
                        setFragment(new category(),"not home");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_feedback:
                        setFragment(new feedback(),"not home");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about:
                        setFragment(new about(),"not home");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_support:
                        setFragment(new support(),"not home");
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
            databaseReference.child("NotificationNumber").child(user.getUid()).child("dashboard")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            k=(int)dataSnapshot.getChildrenCount();
                            Log.d("mmmmmm",dataSnapshot.getValue()+""+dataSnapshot.getChildrenCount()+"  "+k);

                            if(k>0) {
                                Menu menu = navigationView.getMenu();
                                MenuItem nav_dashboard = menu.findItem(R.id.nav_home);
                                MenuItem nav_notification = menu.findItem(R.id.nav_notification);
                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                String original = "dashboard               " + k;
                                SpannableString spannableString = new SpannableString(original);
                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
                                spannableString.setSpan(foregroundColorSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new StyleSpan(BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                nav_dashboard.setTitle(spannableString);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
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
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            databaseReference.child(getString(R.string.InstructorInfo)).child(auth.getCurrentUser().getUid()).keepSynced(true);


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,new edit_profile(),"1");
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
                                setFragment(new HomeFragment(),"home");
                                break;
                            case R.id.action_favorites:
                                setFragment(new explore(),"not home");
                                break;
                            case R.id.action_trending:
                                setFragment(new calendar(),"not home");
                                break;

                            case R.id.action_instructor:
                                setFragment(new instructor(),"not home");
                                break;
                            default:
                                setFragment(new HomeFragment(),"not home");

                        }
                        return true;
                    }

                });
    }

    private void broadcastIntent() {
        registerReceiver(myNetworkReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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

    public void setFragment(Fragment fragment,String name)
    {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setEnabled(true);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment);
        final int count = fragmentManager.getBackStackEntryCount();
        if( name.equals( "not home") ) {
            fragmentTransaction.addToBackStack(name);
        }

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if( fragmentManager.getBackStackEntryCount() <= count){
                    // pop all the fragment and remove the listener
                    fragmentManager.popBackStack("not home", POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        });

        fragmentTransaction.commit();
    }

    @Override
    public void onPaymentSuccess(String s) {

        try{

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for(Fragment f : fragments) {
                if (f != null && f instanceof form_contest)
                    ((form_contest) f).onPaymentSuccess(s);
                if (f != null && f instanceof subscription)
                    ((subscription) f).onPaymentSuccess(s);
            }
        }
        catch (Exception e)
        {

        }

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
                super.onBackPressed();
            }
            if(f != null && (f instanceof routine_purchase)) {
                ((routine_purchase) f).onBackPressed();
            }


            if(f != null && (f instanceof routine_view)) {
                ((routine_view) f).onBackPressed();
            }

            if(f != null && (f instanceof instructor)) {
                ((instructor) f).onBackPressed();
            }

            if(f != null && (f instanceof explore)) {
                ((explore) f).onBackPressed();
            }


            if(f != null && (f instanceof calendar)) {
                ((calendar) f).onBackPressed();
            }


            if(f != null && (f instanceof ChatPeopleList)) {
                ((ChatPeopleList) f).onBackPressed();
            }

            if(f != null && (f instanceof course_purchase_view)) {
                ((course_purchase_view) f).onBackPressed();
            }

            if(f != null && (f instanceof form_contest)) {
                ((form_contest) f).onBackPressed();
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
            if(f != null && (f instanceof feedback)) {
                ((feedback) f).onBackPressed();
                return 1;
            }

        }
        return 0;
    }

    public void onRadioButtonClick(View view)
    {
        Fragment fragment = new edit_profile();
        ((edit_profile) fragment).onRadioButtonClick(view);
    }

    public static void fetchUserPoints() {
        DatabaseReference sdatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sdatabaseReference.child("PointsInstructor").child("OverAll").child(user.getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            PointModel model = dataSnapshot.getValue(PointModel.class);
                            pointsO = model.getPoints();
                            Log.d("pop pop p",pointsO+"");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        sdatabaseReference.child("PointsInstructor").child("weekly").child(user.getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            PointModel model = dataSnapshot.getValue(PointModel.class);
                            pointsW = model.getPoints();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public static void increaseUserPoints(int p)
    {
        DatabaseReference sdatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MainActivity.pointsO-=p;
        MainActivity.pointsW-=p;
        sdatabaseReference.child("PointsInstructor").child("OverAll").child(user.getUid()).child("points").setValue(MainActivity.pointsO);
        sdatabaseReference.child("PointsInstructor").child("weekly").child(user.getUid()).child("points").setValue(MainActivity.pointsW);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myNetworkReceiver);
    }
}