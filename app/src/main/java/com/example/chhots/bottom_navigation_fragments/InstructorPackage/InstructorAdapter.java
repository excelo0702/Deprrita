package com.example.chhots.bottom_navigation_fragments.InstructorPackage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.R;
import com.example.chhots.category_view.routine.routine_purchase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.MyView>{

    private List<InstructorInfoModel> list;
    private Context context;

    public InstructorAdapter(List<InstructorInfoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_instruction_list,parent,false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        Picasso.get().load(Uri.parse(list.get(position).getUserImageurl())).into(holder.img);
        holder.name.setText(list.get(position).getUserName());
        holder.instructorId = list.get(position).getUserId();
        holder.nameP = list.get(position).getUserName();
        holder.thumbnail = list.get(position).getUserImageurl();
    }

    public void setData(List<InstructorInfoModel> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder{

        ImageView img;
        TextView name;
        String thumbnail,instructorId,nameP;



        public MyView(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.raw_instrcutor_item);
            name = itemView.findViewById(R.id.raw_instrcutor_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment fragment = new InstructorFull();
                    Bundle bundle = new Bundle();
                    bundle.putString("thumbnail",thumbnail);
                    bundle.putString("instructorId",instructorId);
                    bundle.putString("name",nameP);
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.drawer_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });
        }
    }
}
