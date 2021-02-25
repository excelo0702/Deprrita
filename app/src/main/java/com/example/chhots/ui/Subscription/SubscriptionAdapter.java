package com.example.chhots.ui.Subscription;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.ChatBox.OnItemClickListener;
import com.example.chhots.Models.SubscriptionModel;
import com.example.chhots.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private List<SubscriptionModel> list;
    private Context context;
    private Activity activityM;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private OnItemClickListener listener;


    public SubscriptionAdapter(List<SubscriptionModel> list, Context context, Activity activityM, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.activityM = activityM;
        this.listener = listener;
    }

    public SubscriptionAdapter() {
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_subscripton_item,parent,false);
        return new SubscriptionViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        holder.Subscription_text.setText(list.get(position).getSubscriptonName());
        holder.free.setText(list.get(position).getFree_trial());
        holder.plan1.setText(list.get(position).getPlan1());
        holder.plan2.setText(list.get(position).getPlan2());
        holder.plan3.setText(list.get(position).getPlan3());
        holder.Plan1 = list.get(position).getPlann1();
        holder.Plan2 = list.get(position).getPlann2();
        holder.Plan3 = list.get(position).getPlann3();
        holder.id = list.get(position).getId();
        holder.thumbnail = list.get(position).getThumbnail();

        holder.bind(list.get(position),listener);
    }

    public void setData(List<SubscriptionModel> list)
    {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        TextView Subscription_text,free,plan1,plan2,plan3;
        Button choose_plan;
        String Plan1,Plan2,Plan3;
        String id,thumbnail;
        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);

            Subscription_text = itemView.findViewById(R.id.raw_subscription_name);
            free = itemView.findViewById(R.id.raw_free_trial);
            plan1 = itemView.findViewById(R.id.raw_plan1);
            plan2 =itemView.findViewById(R.id.raw_plan2);
            plan3 = itemView.findViewById(R.id.raw_plan3);
            choose_plan = itemView.findViewById(R.id.choose_plan);
        }

        public void bind(final SubscriptionModel model,final OnItemClickListener listener)
        {
            final int k = getAdapterPosition();
            if(k==0)
            {
                free.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Free", "0", k);
                    }
                });
                plan1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1month", Plan1, k);
                    }
                });

                plan2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "6month", Plan2, k);
                    }
                });

                plan3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1year", Plan3, k);
                    }
                });
            }
            else if(k==1)
            {
                free.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Free", "0", k);
                    }
                });
                plan1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1month", Plan1, k);
                    }
                });

                plan2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "6month", Plan2, k);
                    }
                });

                plan3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1year", Plan3, k);
                    }
                });
            }
            else if(k==2)
            {
                free.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Free", "0", k);
                    }
                });
                plan1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1month", Plan1, k);
                    }
                });

                plan2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "6month", Plan2, k);
                    }
                });

                plan3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "1year", Plan3, k);
                    }
                });
            }
            else if(k==3)
            {
                free.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Free", "0", k);
                    }
                });
                plan1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Routine", Plan1, k);
                    }
                });

                plan2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(model, "Course", Plan2, k);
                    }
                });

                plan3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                  //      listener.onItemClick(model, "1year", Plan3, k);
                    }
                });
            }

        }




    }
}
