package com.example.chhots.bottom_navigation_fragments.Explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.InstructorLogin;
import com.example.chhots.Models.ExploreVideoModel;
import com.example.chhots.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SearchExploreAdapter extends RecyclerView.Adapter<SearchExploreAdapter.SearchExploreViewHolder> {

    private List<ExploreVideoModel> list;
    private Context context;

    public SearchExploreAdapter(List<ExploreVideoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search_item,parent,false);
        return new SearchExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchExploreViewHolder holder, int position) {
        holder.search_text.setText(list.get(position).getTitle());
        holder.videoId = list.get(position).getVideoId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setData(List<ExploreVideoModel> list)
    {
        this.list =list;
    }


    public class SearchExploreViewHolder extends RecyclerView.ViewHolder{

        TextView search_text;
        String videoId,thumbnail,userId,instructorId;
        FirebaseUser user;
        DatabaseReference mDatabaseReference;

        public SearchExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            search_text = itemView.findViewById(R.id.search_text);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(user==null)
                    {
                        Toast.makeText(context,"Login First",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, InstructorLogin.class);
                        context.startActivity(intent);

                    }
                    else {
                        int p = 1;
                        Fragment fragment = new See_Video();
                        Bundle bundle = new Bundle();
                        bundle.putString("videoId", videoId);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.drawer_layout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });

        }

    }



}

