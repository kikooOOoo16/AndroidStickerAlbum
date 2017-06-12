package com.example.neshh.androidstickeralbum;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

/**
 * Created by neshH on 05-Jun-17.
 */

public class Notifications extends FirebaseMessagingService {

    public static String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String image = remoteMessage.getNotification().getIcon();
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();

        Vector<String> o = new Vector<>();
        o.add(image);
        o.add(title);
        o.add(text);
        o.add(sound);
        o.add(image);

        Object obj = remoteMessage.getData().get("message_id");
        if (obj != null) {
            o.add(obj.toString());
        }
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        sendNotification(o);

    }
    public void sendNotification(Vector<String> obj) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("StickersTrade", obj.get(2));

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = null;
        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(URLDecoder.decode(obj.get(1), "UTF-8"))
                    .setContentText(URLDecoder.decode(obj.get(2), "UTF-8"))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            Log.d(TAG, "Cannot Create notification");
        }
    }
}
