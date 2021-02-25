package com.example.chhots.ChatBox;

import com.example.chhots.Models.RoutineModel;
import com.example.chhots.Models.SubscriptionModel;

public interface OnItemClickListener {
    void onItemClick(MessageModel model);

    void onItemClick(SubscriptionModel model, String plan, String price, int pos);

    void onItemClick(RoutineModel model, int selected);
}
