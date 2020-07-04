package com.example.chhots.ui.Category;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chhots.R;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoModel;
import com.example.chhots.bottom_navigation_fragments.Explore.VideoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class category_view extends Fragment {


    public category_view() {
        // Required empty public constructor
    }



    RecyclerView recyclerView;
    VideoAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private TextView filter,sort,title;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<VideoModel> videolist;
    private static final String TAG = "CategoryView";

    FirebaseAuth auth;
    FirebaseUser user;
    String category;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_view, container, false);

        videolist = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_category_view);
        title = view.findViewById(R.id.category_title_text);
        filter = view.findViewById(R.id.fliter_category);
        sort = view.findViewById(R.id.sort_category);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Bundle bundle = new Bundle();
        category = bundle.getString("category");


        title.setText(category);


        return view;
    }

}
