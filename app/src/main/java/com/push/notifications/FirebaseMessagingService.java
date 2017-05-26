package com.push.notifications;

/**
 * Created by abhijitk on 9/7/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String message = null;
    private static final String TAG = "FBeMessagingService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message: " + remoteMessage.getData().get("message"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            message = remoteMessage.getNotification().getBody();
        }

        sendNotification(message);
    }

//Tab    foHoPoehwZI:APA91bFBb91fNBB-HNBt39HuUpsdjYZXFCkC-qyM-gKVkDM0WubshI346inmjmQLRxUjvqUgtJYgCC7rYIQC33s6AmkXZzPczADXysVyXlTolKrsiPB5k-CNsbVbSAF7IpZuUJcr9fQY

//Mine    dE0kNb9tOB0:APA91bFfHYAnzn4fDi5HjYKHILmiuqBDT2_uCc6Oel4rB1MO2hsN5ukXyd2PLif6OiGh_29QZ6AaedutMVNidKYR_IQgmFwAwIr7h8AlQx_8-JAtS97Bui3MaKtvRfzdyUE0LrdTdu1m

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_default)
                .setContentTitle("Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}