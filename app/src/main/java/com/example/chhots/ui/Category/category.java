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

    private List<CategoryModel> list,list2,list3;
    private RecyclerView recyclerView,recyclerView2,recyclerView3;
    private LinearLayoutManager mLayoutManager,mLayoutManager2,mLayoutManager3;
    private CategoryAdapter adapter,adapter2,adapter3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_category, container, false);


        list = new ArrayList<>();
        recyclerView = root.findViewById(R.id.category_recycler_view1);
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


        list3 = new ArrayList<>();
        recyclerView3 = root.findViewById(R.id.category_recycler_view3);
        recyclerView.setHasFixedSize(true);
        mLayoutManager3 = new LinearLayoutManager(getContext());
        list3.add(new CategoryModel("Ballet",R.drawable.w1));
        list3.add(new CategoryModel("Afro",R.drawable.w2));
        list3.add(new CategoryModel("Dance Hall",R.drawable.w3));
        list3.add(new CategoryModel("Jazz",R.drawable.w4));
        list3.add(new CategoryModel("Litefeet",R.drawable.w5));
        adapter3 = new CategoryAdapter(list3,getContext());
        recyclerView3.setLayoutManager(mLayoutManager3);
        recyclerView3.setAdapter(adapter3);


        list2 = new ArrayList<>();
        recyclerView2 = root.findViewById(R.id.category_recycler_view2);
        recyclerView2.setHasFixedSize(true);
        mLayoutManager2 = new LinearLayoutManager(getContext());
        list2.add(new CategoryModel("Bharatnatyam",R.drawable.w1));
        list2.add(new CategoryModel("Kathak",R.drawable.w2));
        list2.add(new CategoryModel("Kuchupudi",R.drawable.w3));
        list2.add(new CategoryModel("Manipuri",R.drawable.w4));
        list2.add(new CategoryModel("Contemporary",R.drawable.w5));
        adapter2 = new CategoryAdapter(list2,getContext());
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setAdapter(adapter2);

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