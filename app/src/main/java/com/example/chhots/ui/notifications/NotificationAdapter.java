package com.example.chhots.ui.notifications;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chhots.Models.NotificationModel;
import com.example.chhots.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private static final String TAG = "NotificationViewAdapter";
    private List<NotificationModel> notificationList;
    private Context context;

    public NotificationAdapter(List<NotificationModel> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_fragment_notification,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.timing_notification.setText(notificationList.get(position).getDate());
        holder.name_notification.setText(notificationList.get(position).getCategory());
        holder.description_notification.setText(notificationList.get(position).getDescription());
        Picasso.get().load(Uri.parse(notificationList.get(position).getThumbnail())).into(holder.imageNotification);

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView imageNotification;
        TextView name_notification,description_notification,timing_notification;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageNotification = itemView.findViewById(R.id.imageNotification_fragment);
            name_notification = itemView.findViewById(R.id.name_notification_fragment);
            description_notification = itemView.findViewById(R.id.description_notification_fragment);
            timing_notification = itemView.findViewById(R.id.timing_notification_fragment);
        }
    }

}
