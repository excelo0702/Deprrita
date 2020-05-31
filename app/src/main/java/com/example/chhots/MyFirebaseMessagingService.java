package com.example.chhots;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.chhots.ChatBox.ChatWithInstructor;
import com.example.chhots.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static NotificationBadgeModel notification_no = new NotificationBadgeModel();
    DatabaseReference databaseReference;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Map<String,String> extraData = remoteMessage.getData();
        String category = extraData.get("category");
        Log.d("Notifyyy1","Success");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"TAC")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);
        Intent intent;
        if(category.equals("Chat"))
        {
//            startActivity(intent);
    //        Toast.makeText(this,"NNNNN",Toast.LENGTH_SHORT).show();
            intent = new Intent(getApplicationContext(), ChatWithInstructor.class);
            intent.putExtra("routineId",extraData.get("routineId"));
            intent.putExtra("category","Instructor");
            PendingIntent pendingIntent = PendingIntent.getActivity(this,10,intent,PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }
        else{
            Log.d("Notifyyy5","Success");
//            Toast.makeText(this,"NNNNN",Toast.LENGTH_SHORT).show();
        }


        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        int id = (int)System.currentTimeMillis();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("TAC","demo",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id,notificationBuilder.build());
    }
}
