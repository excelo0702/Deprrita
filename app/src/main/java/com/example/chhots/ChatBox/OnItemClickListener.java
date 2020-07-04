package com.example.chhots.ChatBox;

import android.widget.TextView;

import com.example.chhots.category_view.routine.RoutineModel;
import com.example.chhots.ui.Subscription.SubscriptionModel;

public interface OnItemClickListener {
    void onItemClick(MessageModel model);

    void onItemClick(SubscriptionModel model, String plan, String price, int pos);

    void onItemClick(RoutineModel model, int selected);
}
