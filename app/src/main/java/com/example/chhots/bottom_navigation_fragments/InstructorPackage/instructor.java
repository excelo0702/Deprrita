package com.example.chhots.bottom_navigation_fragments.InstructorPackage;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chhots.R;
import com.example.chhots.Demo.demo;
import com.example.chhots.bottom_navigation_fragments.Explore.upload_video;
import com.example.chhots.category_view.Booking.booking_view;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.home.HomeFragment;
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

public class instructor extends Fragment implements onBackPressed {

    public instructor() { }

    private RecyclerView recyclerView;
    private List<InstructorInfoModel> list;
    private InstructorAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructor, container, false);
        recyclerView = view.findViewById(R.id.instructor_list);
        list = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        adapter = new InstructorAdapter(list,getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        showInstructor();

        return view;
    }

    private void showInstructor() {
        databaseReference.child(getResources().getString(R.string.InstructorInfo)).orderByChild("earn")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            InstructorInfoModel model = ds.getValue(InstructorInfoModel.class);
                            list.add(model);
                        }
                        adapter.setData(list);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
    //    getFragmentManager().beginTransaction().replace(R.id.drawer_layout,new HomeFragment()).commit();
    }
}
