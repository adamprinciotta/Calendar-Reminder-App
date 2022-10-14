package com.example.homeworkcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String assignment = null;
        String description = null;
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            assignment = (String) bundle.get("assignment");
            description = (String) bundle.get("description");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationTest")
                .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                .setContentTitle(assignment)
                .setContentText(intent.getStringExtra("description"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(intent.getStringExtra("description")))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE);


        NotificationManagerCompat notificationManger = NotificationManagerCompat.from(context);

        notificationManger.notify(200, builder.build());
    }
}
