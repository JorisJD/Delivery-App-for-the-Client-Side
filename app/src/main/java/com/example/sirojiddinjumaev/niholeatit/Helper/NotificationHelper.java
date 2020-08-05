package com.example.sirojiddinjumaev.niholeatit.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.sirojiddinjumaev.niholeatit.R;

public class NotificationHelper extends ContextWrapper {

    private static final String NIHOL_CHANEL_ID = "com.example.sirojiddinjumaev.niholeatit.NiholEatIt";
    private static final String NIHOL_CHANEL_NAME = "Nihol Eat It";

    private NotificationManager manager;



    public NotificationHelper(Context base) {

        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //Only working this function if API 26 or highher
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel niholChannel = new NotificationChannel(NIHOL_CHANEL_ID,
                NIHOL_CHANEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        niholChannel.enableLights(false);
        niholChannel.enableVibration(true);
        niholChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(niholChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getEatItChannelNotification (String title, String body, PendingIntent contentIntent,
                                                                         Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(), NIHOL_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getEatItChannelNotification (String title, String body,
                                                                         Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(), NIHOL_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
