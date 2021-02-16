package com.example.chhots.bottom_navigation_fragments.InstructorPackage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chhots.R;
import com.example.chhots.category_view.Courses.CourseThumbnail;
import com.example.chhots.category_view.routine.RoutineThumbnailModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InstructorFull extends Fragment {

    public InstructorFull() {
        // Required empty public constructor
    }


    private ImageView img,coverPhoto;
    TextView points,name,crewName,styles,about;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    String instructorId;
    InstructorInfoModel model,styleModel;
    String style="";

    private InstructorRoutineAdapter adapterRoutine;
    private List<RoutineThumbnailModel> listRoutine;
    private RecyclerView recyclerViewRoutine,recyclerViewCourse;

    private InstructorCourseAdapter adapterCourse;
    private List<CourseThumbnail> listCourse;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.activity_instructor_full_info, container, false);


        init(view);


        Bundle bundle = getArguments();
        instructorId = bundle.getString("instructorId");
        name.setText(bundle.getString("name"));
        Picasso.get().load(Uri.parse(bundle.getString("thumbnail"))).into(img);


        fetchInstructorInfo();
        fetchRoutine();
        fetchCourse();
        return view;


    }

    private void fetchRoutine() {
        databaseReference.child(getResources().getString(R.string.RoutineThumbnail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            final RoutineThumbnailModel model = ds.getValue(RoutineThumbnailModel.class);

                            if(model!=null && model.getInstructorId().equals(instructorId))
                            {
                                listRoutine.add(model);
                            }
                        }
                        adapterRoutine = new InstructorRoutineAdapter(listRoutine,getContext());
                        recyclerViewRoutine.setAdapter(adapterRoutine);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void fetchCourse() {

        databaseReference.child(getResources().getString(R.string.CoursesThumbnail)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p = 0;
//                Log.d(TAG,dataSnapshot.getValue().toString()+"");


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    final CourseThumbnail model = ds.getValue(CourseThumbnail.class);
                    if(model!=null && model.getInstructorId().equals(instructorId))
                    {
                        listCourse.add(model);
                    }
                    adapterCourse = new InstructorCourseAdapter(listCourse,getContext());
                    recyclerViewCourse.setAdapter(adapterCourse);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void fetchInstructorInfo() {
        databaseReference.child(getResources().getString(R.string.InstructorInfo)).child(instructorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("pop pop pop",dataSnapshot.getValue()+"");
                        model = dataSnapshot.getValue(InstructorInfoModel.class);
                        about.setText(model.getUserAbout());
                        points.setText(model.getPoints());
                        databaseReference.child(getResources().getString(R.string.InstructorStyle)).child(instructorId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        styleModel = dataSnapshot.getValue(InstructorInfoModel.class);
                                        if(styleModel!=null)
                                        {
                                            int k=0;
                                            if (styleModel.getStyle().charAt(0)=='1'){
                                                k++;
                                                style+="Breaking \n";
                                            }

                                            if (styleModel.getStyle().charAt(1)=='1'){
                                                k++;
                                                style+="Locking\n";
                                            }

                                            if (styleModel.getStyle().charAt(2)=='1'){
                                                k++;
                                                style+="Krump\n";
                                            }

                                            if (styleModel.getStyle().charAt(3)=='1'){
                                                if (k<3){

                                                    style+="Afro\n";

                                                }
                                                k++;

                                            }

                                            if (styleModel.getStyle().charAt(4)=='1'){
                                                if (k<3){
                                                    style+="Kathak\n";
                                                }
                                                k++;

                                            }

                                            if (styleModel.getStyle().charAt(5)=='1'){
                                                if (k<3){
                                                    style+="Kuchipudi\n";
                                                }
                                                k++;

                                            }
                                            styles.setText(style);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        databaseReference.child(getResources().getString(R.string.InstructorCoverPhoto)).child(instructorId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        InstructorInfoModel model12 = dataSnapshot.getValue(InstructorInfoModel.class);
                                        if(model12!=null)
                                            Picasso.get().load(Uri.parse(model12.getCoverPhoto())).placeholder(R.drawable.smurfooo).into(coverPhoto);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init(View view) {
        img = view.findViewById(R.id.instructor_dashboard_profile1);
        about = view.findViewById(R.id.instructor_description);
        points = view.findViewById(R.id.instructor_points);
        name = view.findViewById(R.id.instructor_name);
        crewName = view.findViewById(R.id.instructor_crew_name);
        styles = view.findViewById(R.id.instructor_style);
        coverPhoto = view.findViewById(R.id.instructor_cover_photo);
        recyclerViewRoutine = view.findViewById(R.id.instructor_routine);
        listRoutine=new ArrayList<>();
        recyclerViewRoutine.setHasFixedSize(true);
        recyclerViewRoutine.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        listCourse = new ArrayList<>();
        recyclerViewCourse =view.findViewById(R.id.instructor_course);
        recyclerViewCourse.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));


        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }



}