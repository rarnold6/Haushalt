package com.example.haushalt.fragments_gefrierschrank;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.haushalt.R;
import com.example.haushalt.activity.MainActivity;
import com.example.haushalt.data.HaushaltContract;

public class AlertReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION_ELEMENT = "notification_element";
    public static String NOTIFICATION_DURABILITY = "notification_durability";
    public static String NOTIFICATION_PLACE = "notification_place";
    public static String NOTIFICATION_CASE = "notification_case";

    @Override
    public void onReceive(Context context, Intent intent) {
        long notification_id = intent.getLongExtra(NOTIFICATION_ID, -1);
        String notification_element = intent.getStringExtra(NOTIFICATION_ELEMENT);
        int notification_durability = intent.getIntExtra(NOTIFICATION_DURABILITY, -1);
        String notification_place = intent.getStringExtra(NOTIFICATION_PLACE);
        int notification_case = intent.getIntExtra(NOTIFICATION_CASE, -1);

        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.putExtra(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID, notification_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) notification_id, intent1, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle(notification_element + " abgelaufen")
                .setContentText(notification_element + " im " + notification_place + " in Fach " + notification_case + " ist jetzt abgelaufen!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notification_element + " im " + notification_place + " in Fach " + notification_case + " ist jetzt abgelaufen!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify((int) notification_id, builder.build());
    }
}
