package com.example.chhots.category_view.routine;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.OnItemClickListener;
import com.example.chhots.Models.RoutineModel;
import com.example.chhots.R;

import java.util.List;

import static android.view.View.GONE;

public class RoutineViewAdapter extends RecyclerView.Adapter<RoutineViewAdapter.RoutineViewHolder>{

    public RoutineViewAdapter() {
    }

    private List<RoutineModel> list;
    private Context context;
    private OnItemClickListener listener;
    int selected=-1;


    private final String Tag = "RoutineViewAdapter1";

    public RoutineViewAdapter(List<RoutineModel> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routine_view_item,parent,false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        if(selected==position)
        {
            holder.section.setTextColor(Color.BLUE);
            holder.img3.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_blue));

        }
        else
        {
            holder.section.setTextColor(Color.BLACK);
            holder.img3.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
        }

        holder.bind(list.get(position),listener);

        holder.section.setText(list.get(position).getSequenceNo()+". "+list.get(position).getTitle());
        holder.description.setText(list.get(position).getDescription());
        holder.videoURL = list.get(position).getVideoUrl();
        holder.routineId = list.get(position).getRoutineId();
    }


    public void setData(List<RoutineModel> list)
    {
        this.list = list;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RoutineViewHolder extends RecyclerView.ViewHolder{
        TextView section,description;
        String videoURL,routineId;
        ImageView img1,img2, img3;
        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            section = itemView.findViewById(R.id.routine_sectionNo);
            description = itemView.findViewById(R.id.routine_description);
            img1 = itemView.findViewById(R.id.maximize);
            img2 = itemView.findViewById(R.id.minimize);
            img3 = itemView.findViewById(R.id.pause);
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(GONE);
            description.setVisibility(GONE);


        }
        public void bind(final RoutineModel model, final OnItemClickListener listener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selected = getAdapterPosition();
                    Log.d(Tag,selected+"");
                    listener.onItemClick(model,selected);
                    notifyDataSetChanged();

                }
            });
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    description.setVisibility(View.VISIBLE);
                    img1.setVisibility(GONE);
                    img2.setVisibility(View.VISIBLE);
                }
            });
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    description.setVisibility(GONE);
                    img1.setVisibility(View.VISIBLE);
                    img2.setVisibility(GONE);
                }
            });
        }
    }

}
