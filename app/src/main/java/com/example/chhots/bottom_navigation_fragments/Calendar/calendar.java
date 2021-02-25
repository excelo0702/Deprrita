package com.example.chhots.bottom_navigation_fragments.Calendar;


import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.chhots.MainActivity;
import com.example.chhots.Models.CalendarModel;
import com.example.chhots.R;
import com.example.chhots.Models.InstructorInfoModel;
import com.example.chhots.onBackPressed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class calendar extends Fragment implements onBackPressed {


    public calendar() {
        // Required empty public constructor
    }

    private TextView date;
    CalendarView calendarView;
    private RecyclerView recyclerView;
    private CalendarAdapter mAdapter;
    private List<CalendarModel> list;
    LinearLayoutManager mLayoutManager;
    String TAG = "Calendar";

    private DatabaseReference databaseReference;
    List<EventDay> events;
    List<Calendar> calendars;

    Button select_date;

    private PopupWindow mPopupWindow;
    String date_text;

    private FirebaseUser user;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        date = view.findViewById(R.id.date_view);
        recyclerView = view.findViewById(R.id.event_list);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        select_date = view.findViewById(R.id.select_datee);
        list = new ArrayList<>();
        mAdapter = new CalendarAdapter(list,getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

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


        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });

        events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.ic_star1));

        calendarView = (CalendarView)view. findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String d;
                if(clickedDayCalendar.get(Calendar.MONTH)<10)
                {
                    if(clickedDayCalendar.get(Calendar.DATE)<10)
                    {
                        d = clickedDayCalendar.getWeekYear()+" 0"+(clickedDayCalendar.get(Calendar.MONTH))+" 0"+clickedDayCalendar.get(Calendar.DATE);

                    }
                    else
                    {
                        d = clickedDayCalendar.getWeekYear()+" 0"+(clickedDayCalendar.get(Calendar.MONTH))+" "+clickedDayCalendar.get(Calendar.DATE);
                    }
                }
                else
                {

                    if(clickedDayCalendar.get(Calendar.DATE)<10)
                    {
                        d = clickedDayCalendar.getWeekYear()+" "+(clickedDayCalendar.get(Calendar.MONTH))+" 0"+clickedDayCalendar.get(Calendar.DATE);
                    }
                    else
                    {
                        d = clickedDayCalendar.getWeekYear()+" "+(clickedDayCalendar.get(Calendar.MONTH))+" "+clickedDayCalendar.get(Calendar.DATE);
                    }
                }
                Toast.makeText(getContext(),d+" l "+d.length(),Toast.LENGTH_SHORT).show();
                dateClick(d);
            }
        });

        List<Calendar> selectedDates = calendarView.getSelectedDates();
        DatesetDate();
        fetchUserInfo();

        MainActivity.fetchUserPoints();

        calendars = new ArrayList<>();

        return view;
    }

    private void DatesetDate()
    {
        databaseReference.child(getResources().getString(R.string.CALENDAR)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String date = ds.getKey();


                    int y = Integer.parseInt(date.substring(0,4));
                    int m = Integer.parseInt(date.substring(5,7));
                    int d = Integer.parseInt(date.substring(8,10));
                    Log.d("pop pop coc",y+" "+m+" "+" "+d+"  ");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(y,m,d);
                    calendars.add(calendar);
                    events.add(new EventDay(calendar, R.drawable.ic_star1, Color.parseColor("#228B22")));
                }
                calendarView.setEvents(events);
                calendarView.setHighlightedDays(calendars);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void dateClick(String d) {

        databaseReference.child(getResources().getString(R.string.CALENDAR)).child(d)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            CalendarModel model = ds.getValue(CalendarModel.class);
                            list.add(model);
                        }
                        Log.d("pop pop lol",list.size()+"");
                        mAdapter.setData(list);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        databaseReference.child(getResources().getString(R.string.CALENDAR)).child(d).keepSynced(true);

    }

    public void addDate(){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add date");
                View customView = getLayoutInflater().inflate(R.layout.raw_calendar,null);
                builder.setView(customView);
                Button submit = customView.findViewById(R.id.submit_date);
                final EditText event = customView.findViewById(R.id.edit_event);
                final EditText time = customView.findViewById(R.id.edit_time);
                final android.widget.CalendarView calendarView = customView.findViewById(R.id.calender);

                final AlertDialog dialog = builder.create();
                dialog.show();
                calendarView.setOnDateChangeListener(new android.widget.CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull android.widget.CalendarView calendarView, int year, int month, int day) {
                    if(month<10)
                    {
                        if(day<10)
                        {
                            date_text = String.valueOf(year)+" 0"+String.valueOf(month)+" 0"+String.valueOf(day);

                        }
                        else
                        {
                            date_text = String.valueOf(year)+" 0"+String.valueOf(month)+" "+String.valueOf(day);
                        }

                    }
                    else
                    {
                        if(day<10)
                        {
                            date_text = String.valueOf(year)+" "+String.valueOf(month)+" 0"+String.valueOf(day);

                        }
                        else
                        {
                            date_text = String.valueOf(year)+" "+String.valueOf(month)+" "+String.valueOf(day);
                        }
                    }
                    }
        });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CalendarModel model = new CalendarModel(event.getText().toString(),date_text,"","",time.getText().toString(),url);
                        databaseReference.child(getResources().getString(R.string.CALENDAR)).child(date_text).child(System.currentTimeMillis()+"").setValue(model);
                        MainActivity.increaseUserPoints(20);
                        dialog.cancel();
                        DatesetDate();
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
            activitySearchMenu.setVisible(false);
        }
    }

    private void fetchUserInfo() {
        databaseReference.child("InstructorInfo").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        InstructorInfoModel model = dataSnapshot.getValue(InstructorInfoModel.class);
                        url = model.getUserImageurl();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    @Override
    public void onBackPressed() {
   //     getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new HomeFragment()).commit();
    }

}
