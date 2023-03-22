package com.example.haushalt.fragments_gefrierschrank;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.haushalt.R;
import com.example.haushalt.activity.MainActivity;
import com.example.haushalt.data.HaushaltContract;

public class Utils {

    public static void setAlarm(Context context, long timeOfAlarm, long gefrierschrankElementID, String gefrierschrankElementName, String gefrierschrankLabel, int fachNummer){
        Intent notificationIntent = new Intent(context, AlertReceiver.class);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ID, gefrierschrankElementID);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ELEMENT, gefrierschrankElementName);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_DURABILITY, timeOfAlarm);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_PLACE, gefrierschrankLabel);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_CASE, fachNummer);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) gefrierschrankElementID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(System.currentTimeMillis() < timeOfAlarm){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeOfAlarm, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfAlarm, pendingIntent);
            }
        } else {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID, gefrierschrankElementID);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, (int)gefrierschrankElementID, intent1, 0);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.ic_home)
                    .setContentTitle(gefrierschrankElementName + " abgelaufen")
                    .setContentText(gefrierschrankElementName + " im " + gefrierschrankLabel + " in Fach " + fachNummer + " ist jetzt abgelaufen!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(gefrierschrankElementName + " im " + gefrierschrankLabel + " in Fach " + fachNummer + " ist jetzt abgelaufen!"))
                    .setContentIntent(pendingIntent1)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            notificationManagerCompat.notify((int) gefrierschrankElementID, builder.build());
        }
    }
}
