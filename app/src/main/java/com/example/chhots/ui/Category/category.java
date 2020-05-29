package com.example.chhots.ui.Category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.onBackPressed;
import com.example.chhots.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class category extends Fragment implements onBackPressed {


    private TextView Breaking,Krump,locking,popping,house,waacking,hip_hop,bharatnatyam;
    private TextView kathak,khatakali,kuchipudi,manipuri,mohiniyattam,odissi,sattriya;
    private TextView afro,ballet,contemporary,dance_hall,jazz,litfeet;

    private List<CategoryModel> list;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private CategoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_category, container, false);


        list = new ArrayList<>();
        recyclerView = root.findViewById(R.id.category_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        list.add(new CategoryModel("Breaking",R.drawable.w1));
        list.add(new CategoryModel("Krump",R.drawable.w2));
        list.add(new CategoryModel("Locking",R.drawable.w3));
        list.add(new CategoryModel("Popping",R.drawable.w4));
        list.add(new CategoryModel("Waacking",R.drawable.w5));
        list.add(new CategoryModel("Hip Hop",R.drawable.w7));
        adapter = new CategoryAdapter(list,getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void setFragment(Fragment fragment, String value) {
        Bundle bundle = new Bundle();
        bundle.putString("category",value);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout,fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        View BottomnavBar = getActivity().findViewById(R.id.bottom_navigation);
        BottomnavBar.setVisibility(View.VISIBLE);
        setFragment(new HomeFragment(),"");
    }

}